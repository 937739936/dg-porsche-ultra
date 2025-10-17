package com.shdatalink.sip.server.config.web;

import com.shdatalink.framework.common.utils.QuarkusUtil;
import com.shdatalink.sip.server.config.SipConfigProperties;
import com.shdatalink.sip.server.gb28181.core.bean.constants.SipConstant;
import com.shdatalink.sip.server.gb28181.core.parser.GbStringMsgParserFactory;
import gov.nist.javax.sip.SipProviderImpl;
import gov.nist.javax.sip.SipStackImpl;
import io.quarkus.runtime.Startup;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Produces;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.sip.*;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.TooManyListenersException;

@ApplicationScoped
@Slf4j
public class SipServerConfiguration {

    public Properties getProperties(String ip, String logLevel) {
        Properties properties = new Properties();
        properties.setProperty("javax.sip.STACK_NAME", "GB28181_SIP");
        properties.setProperty("javax.sip.IP_ADDRESS", ip);
        // 关闭自动会话
        properties.setProperty("javax.sip.AUTOMATIC_DIALOG_SUPPORT", "off");

        // 接收所有notify请求，即使没有订阅
        properties.setProperty("gov.nist.javax.sip.DELIVER_UNSOLICITED_NOTIFY", "true");
        properties.setProperty("gov.nist.javax.sip.AUTOMATIC_DIALOG_ERROR_HANDLING", "false");
        properties.setProperty("gov.nist.javax.sip.CANCEL_CLIENT_TRANSACTION_CHECKED", "true");
        // 为_NULL _对话框传递_终止的_事件
        properties.setProperty("gov.nist.javax.sip.DELIVER_TERMINATED_EVENT_FOR_NULL_DIALOG", "true");
        // 是否自动计算content length的实际长度，默认不计算
        properties.setProperty("gov.nist.javax.sip.COMPUTE_CONTENT_LENGTH_FROM_MESSAGE_BODY", "true");
        // 会话清理策略
        properties.setProperty("gov.nist.javax.sip.RELEASE_REFERENCES_STRATEGY", "Normal");
        // 处理由该服务器处理的基于底层TCP的保持生存超时
        properties.setProperty("gov.nist.javax.sip.RELIABLE_CONNECTION_KEEP_ALIVE_TIMEOUT", "60");
        // 获取实际内容长度，不使用header中的长度信息
        properties.setProperty("gov.nist.javax.sip.COMPUTE_CONTENT_LENGTH_FROM_MESSAGE_BODY", "true");
        // 线程可重入
        properties.setProperty("gov.nist.javax.sip.REENTRANT_LISTENER", "true");
        // 定义应用程序打算多久审计一次 SIP 堆栈，了解其内部线程的健康状况（该属性指定连续审计之间的时间（以毫秒为单位））
        properties.setProperty("gov.nist.javax.sip.THREAD_AUDIT_INTERVAL_IN_MILLISECS", "30000");

        /**
         * sip_server_log.log 和 sip_debug_log.log ERROR, INFO, WARNING, OFF, DEBUG, TRACE
         */
//        if ("OFF".equals(logLevel)) {
//            log.info("「SIP」日志已关闭");
//        }else {
//            properties.setProperty("gov.nist.javax.sip.TRACE_LEVEL", logLevel);
//            properties.setProperty("gov.nist.javax.sip.STACK_LOGGER", "com.shdatalink.sip.server.gb28181.logger.StackLoggerImpl");
//            properties.setProperty("gov.nist.javax.sip.SERVER_LOGGER", "com.shdatalink.sip.server.gb28181.logger.ServerLoggerImpl");
//            properties.setProperty("gov.nist.javax.sip.LOG_MESSAGE_CONTENT", "true");
//            log.info("「SIP」日志已开启");
//        }
        return properties;
    }
    @Produces
    @Named("sipFactory")
    public SipFactory sipFactory() {
        SipFactory instance = SipFactory.getInstance();
        instance.setPathName("gov.nist");
        return instance;
    }

    @Produces
    @Named("sipStack")
    public SipStackImpl createSipStackImpl(SipConfigProperties sipConfig, @Named("sipFactory") SipFactory sipFactory) throws PeerUnavailableException {
        SipConfigProperties.SipServerConf server = sipConfig.server();
        if (Objects.isNull(server) || StringUtils.isEmpty(server.domain()) || StringUtils.isEmpty(server.id())) {
            throw new RuntimeException("sip.server.id 或 sip.server.domain 不能为空");
        }
        String ip = StringUtils.isEmpty(server.ip()) ? "0.0.0.0" : server.ip();
        String level = Optional.ofNullable(sipConfig.logs()).orElse("OFF");
        Properties sipProperties = getProperties(ip, level);
        SipStackImpl sipStack = (SipStackImpl) sipFactory.createSipStack(sipProperties);
        sipStack.setMessageParserFactory(new GbStringMsgParserFactory());
        sipStack.setStackName("gb_starter");
        return sipStack;
    }


    @Startup
    @Singleton
    @Named("tcpSipProvider")
    public SipProviderImpl startTcpListener(SipConfigProperties sipConfig, SipStackImpl sipStack, SipListener sipProcessor) {
        SipConfigProperties.SipServerConf server = sipConfig.server();
        String ip = StringUtils.isEmpty(server.ip()) ? "0.0.0.0" : server.ip();
        Integer port = server.port();
        try {
            ListeningPoint tcpListeningPoint = sipStack.createListeningPoint(ip, port, SipConstant.TransPort.TCP);
            SipProviderImpl tcpSipProvider = (SipProviderImpl) sipStack.createSipProvider(tcpListeningPoint);
            tcpSipProvider.setDialogErrorsAutomaticallyHandled();
            tcpSipProvider.addSipListener(sipProcessor);
            log.info("\033[36;2m「SIP」 tcp://{}:{} 启动成功\033[36;0m", ip, port);
            return tcpSipProvider;
        } catch (TransportNotSupportedException
                 | ObjectInUseException
                 | TooManyListenersException
                 | InvalidArgumentException e) {
            log.error("\033[31;2m[SIP] tcp://{}:{} SIP服务启动失败,请检查端口是否被占用或者ip是否正确\033[31;0m", ip, port);
        }
        return null;
    }

    @PreDestroy
    public void cleanup() {
        SipStack stack = QuarkusUtil.getBean(SipStack.class);
        stack.stop();
    }


    @Startup
    @Singleton
    @Named("udpSipProvider")
    public SipProviderImpl startUdpListener(SipConfigProperties sipConfig, SipStackImpl sipStack, SipListener sipProcessor) {
        SipConfigProperties.SipServerConf server = sipConfig.server();
        String ip = StringUtils.isEmpty(server.ip()) ? "0.0.0.0" : server.ip();
        Integer port = server.port();
        try {
            ListeningPoint udpListeningPoint = sipStack.createListeningPoint(ip, port, SipConstant.TransPort.UDP);
            SipProviderImpl udpSipProvider = (SipProviderImpl) sipStack.createSipProvider(udpListeningPoint);
            udpSipProvider.setDialogErrorsAutomaticallyHandled();
            udpSipProvider.addSipListener(sipProcessor);
            log.info("\033[36;2m「SIP」 upd://{}:{} 启动成功\033[36;0m", ip, port);
            return udpSipProvider;
        } catch (TransportNotSupportedException
                 | ObjectInUseException
                 | TooManyListenersException
                 | InvalidArgumentException e) {
            log.error("\033[31;2m[SIP] upd://{}:{} SIP服务启动失败,请检查端口是否被占用或者ip是否正确\033[31;0m", ip, port);
        }
        return null;
    }

//    @Produces
//    @Named("executor")
//    public Executor executor() {
//        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//        executor.setCorePoolSize(5);       // 核心线程数
//        executor.setMaxPoolSize(200);       // 最大线程数
//        executor.setQueueCapacity(1000);     // 队列容量
//        executor.setKeepAliveSeconds(60);  // 线程空闲时间
//        executor.setThreadNamePrefix("taskExecutor-"); // 线程名前缀
//        executor.initialize();
//        return executor;
//    }
}
