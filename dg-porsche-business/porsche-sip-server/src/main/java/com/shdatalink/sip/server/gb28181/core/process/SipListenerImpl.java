package com.shdatalink.sip.server.gb28181.core.process;

import com.shdatalink.sip.server.gb28181.core.builder.DialogHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.sip.*;
import javax.sip.header.CSeqHeader;
import javax.sip.message.Response;

/**
 *
 * 常见方法:
 *
 * REGISTER：用户向 Registrar 注册。
 * INVITE：建立会话（最常用）。
 * ACK：确认会话建立。
 * BYE：终止会话。
 * OPTIONS：查询能力。
 * CANCEL：取消未完成请求。
 * MESSAGE：即时消息（在部分扩展中）。
 * SUBSCRIBE/NOTIFY：事件订阅/通知（如状态、心跳）。
 *
 */
@Slf4j
@Component
@Async("sipMessageExecutor")
@Order(1)
public class SipListenerImpl implements SipListener {

    @Override
    public void processRequest(RequestEvent requestEvent) {
        String method = requestEvent.getRequest().getMethod();
        if (log.isInfoEnabled()) {
            log.info("\033[36;2m 来自摄像机「{}」请求\033[36;0m", method);
        }
        SipMethodContext.execute(method).request(requestEvent);
    }

    @Override
    public void processResponse(ResponseEvent responseEvent) {
        Response response = responseEvent.getResponse();
        int status = response.getStatusCode();
        CSeqHeader cseqHeader = (CSeqHeader) responseEvent.getResponse().getHeader(CSeqHeader.NAME);
        String method = cseqHeader.getMethod();

        log.info("\033[36;2m 来自摄像机「{}」的{}响应\033[36;0m", method, status);
        if (((status >= Response.OK) && (status < Response.MULTIPLE_CHOICES)) || status == Response.UNAUTHORIZED) {
            SipMethodContext.execute(method).response(responseEvent);
        } else if ((status >= Response.TRYING) && (status < Response.OK)) {
            // 增加其它无需回复的响应，如101、180等
            log.warn("\033[31;2m「无需回复的响应」{}\033[31;0m", response.getStatusCode());
        } else {
            log.error("\033[31;2m「错误信息」{}\033[31;0m", response.getReasonPhrase());
            if (responseEvent.getDialog() != null) {
                responseEvent.getDialog().delete();
            }
        }
    }

    @Override
    public void processTimeout(TimeoutEvent timeoutEvent) {
        if (log.isInfoEnabled()) {
            log.info("收到摄像机Timeout回调");
        }
    }

    @Override
    public void processIOException(IOExceptionEvent exceptionEvent) {
        if (log.isInfoEnabled()) {
            log.info("收到摄像机IOException回调");
        }
    }

    @Override
    public void processTransactionTerminated(TransactionTerminatedEvent transactionTerminatedEvent) {
        if (log.isInfoEnabled()) {
            log.info("收到摄像机TransactionTerminated回调");
        }
    }

    @Override
    public void processDialogTerminated(DialogTerminatedEvent dialogTerminatedEvent) {
        if (log.isInfoEnabled()) {
            log.info("收到摄像机DialogTerminated回调");
        }
        DialogHolder.removeDialogId(dialogTerminatedEvent.getDialog().getDialogId());
    }
}
