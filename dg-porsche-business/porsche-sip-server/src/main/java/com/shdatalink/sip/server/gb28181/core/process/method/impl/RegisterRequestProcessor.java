package com.shdatalink.sip.server.gb28181.core.process.method.impl;

import com.shdatalink.framework.common.service.EventPublisher;
import com.shdatalink.sip.server.config.SipConfigProperties;
import com.shdatalink.sip.server.gb28181.core.bean.annotations.SipEvent;
import com.shdatalink.sip.server.gb28181.core.bean.constants.SipEnum;
import com.shdatalink.sip.server.gb28181.core.bean.model.device.Dto.RemoteInfo;
import com.shdatalink.sip.server.gb28181.core.builder.ResponseBuilder;
import com.shdatalink.sip.server.gb28181.core.process.method.AbstractSipRequestProcessor;
import com.shdatalink.sip.server.module.device.event.DeviceInfoUpdateEvent;
import com.shdatalink.sip.server.module.device.event.DeviceOnlineEvent;
import com.shdatalink.sip.server.module.device.event.DeviceRegisterEvent;
import com.shdatalink.sip.server.module.device.service.DeviceChannelService;
import com.shdatalink.sip.server.module.device.service.DeviceService;
import com.shdatalink.sip.server.utils.DigestAuthenticationUtil;
import com.shdatalink.sip.server.utils.SipUtil;
import gov.nist.javax.sip.header.Authorization;
import gov.nist.javax.sip.message.SIPRequest;
import io.quarkus.runtime.Startup;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.header.WWWAuthenticateHeader;

import static com.shdatalink.sip.server.gb28181.core.bean.constants.SipConstant.REGISTER_DIGEST_MD5_ALGORITHM;

@Startup
@SipEvent(SipEnum.Method.REGISTER)
@ApplicationScoped
public class RegisterRequestProcessor extends AbstractSipRequestProcessor {
    private static final Logger logger = LoggerFactory.getLogger(RegisterRequestProcessor.class);

    @Inject
    SipConfigProperties sipConfigProperties;

    @Inject
    DeviceService deviceService;


    @Inject
    DeviceChannelService deviceChannelService;

    @Inject
    EventPublisher publisher;

    /*
         注册鉴权流程:

         1. 下级向上级发送不带鉴权信息的Register请求
         2. 上级向下级返回401错误(生成挑战信息)
         3. 下级向上级发送携带鉴权信息的Register请求
         4. 上级向下级返回鉴权处理结果，注册成功返回200 OK， 失败返回4XX


         注销鉴权流程:

         注销流程和注册流程完全相同，也是分4个步骤完成。 注册和注销的唯一区别是:
          1、注册时,Message Header中Expires 大于0
          2、注销时,Message Header中Expires 等于0
     */

    @Override
    public void request(RequestEvent requestEvent) {
        SIPRequest request = (SIPRequest) requestEvent.getRequest();
        String deviceId = SipUtil.getUserIdFromFromHeader(request);
        String serverDomain = sipConfigProperties.server().domain();

        boolean isRegistration = request.getExpires().getExpires() > 0;

        deviceService.getEnabledByDeviceId(deviceId)
                .ifPresentOrElse(deviceConfig -> {
                        Authorization clientAuthorization = request.getAuthorization();
                        if (clientAuthorization == null) {
                            // server challenge
                            WWWAuthenticateHeader wwwAuthenticateHeader = DigestAuthenticationUtil.generateChallenge(serverDomain, true, REGISTER_DIGEST_MD5_ALGORITHM);
                            ResponseBuilder.of(requestEvent)
                                    .unauthorized()
                                    .addHeader(request.getContactHeader())
                                    .addHeader(wwwAuthenticateHeader)
                                    .execute();
                        } else {
                            boolean f = DigestAuthenticationUtil.doAuthenticatePlainTextPassword(request.getMethod(), clientAuthorization, deviceConfig.getRegisterPassword());
                            if (f) {
                                publisher.fire(new DeviceRegisterEvent(deviceId, isRegistration, DeviceRegisterEvent.Status.Success));
                                if (isRegistration) {
                                    // 1.  发送注册成功响应消息
                                    ResponseBuilder.of(requestEvent).buildRegisterOfResponse().execute();

                                    // 2. 持久化设备信息
                                    RemoteInfo remoteInfo = SipUtil.getRemoteInfoFromRequest(request);
                                    boolean f2 = deviceService.saveDevice(deviceConfig, remoteInfo);
                                    if (f2 == false) {
                                        logger.info("{} 设备信息更新失败,请联系管理员", deviceId);
                                    }
                                    // 3. 更新通道信息
                                    try{
                                        deviceChannelService.renewalChannel(deviceId);
                                    }catch (Exception e){
                                        // ignore  exception
                                    }

                                    publisher.fire(new DeviceOnlineEvent(deviceId, true));
                                    publisher.fire(new DeviceInfoUpdateEvent(deviceConfig));
                                } else {
                                    logger.info("{} 设备注销.", deviceId);
                                    publisher.fire(new DeviceOnlineEvent(deviceId, false));
                                    deviceService.updateOnline(deviceId, false);
                                    deviceChannelService.setDeviceOffline(deviceId);
                                    ResponseBuilder.of(requestEvent).buildRegisterOfResponse().execute();
                                }
                            } else {
                                publisher.fire(new DeviceRegisterEvent(deviceId, isRegistration, DeviceRegisterEvent.Status.Fail));
                                logger.info("{} 设备注册失败, 密码不正确.", deviceId);
                                ResponseBuilder.of(requestEvent).forbidden().execute();
                            }
                        }

                }, () -> {
                    // illegal request
                    logger.info("非法请求: {} 设备不存在或设备被禁用", deviceId);
                    ResponseBuilder.of(requestEvent).forbidden().execute();
                });
    }

    @Override
    public void response(ResponseEvent responseEvent) {
        logger.info("RegisterRequestProcessor response method invoke!!!");
    }
}
