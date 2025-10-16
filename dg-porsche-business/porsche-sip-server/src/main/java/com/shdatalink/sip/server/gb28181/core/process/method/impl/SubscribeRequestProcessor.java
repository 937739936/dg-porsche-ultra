package com.shdatalink.sip.server.gb28181.core.process.method.impl;

import com.shdatalink.sip.server.gb28181.core.bean.annotations.SipEvent;
import com.shdatalink.sip.server.gb28181.core.bean.constants.SipEnum;
import com.shdatalink.sip.server.gb28181.core.bean.model.device.message.notify.response.ResponseMessage;
import com.shdatalink.sip.server.gb28181.core.process.method.AbstractSipRequestProcessor;
import com.shdatalink.sip.server.module.alarmplan.service.SubscribeService;
import com.shdatalink.sip.server.util.JsonMapper;
import com.shdatalink.sip.server.util.XmlUtil;
import gov.nist.javax.sip.message.SIPResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.header.ExpiresHeader;
import javax.sip.message.Response;

@Slf4j
@SipEvent(SipEnum.Method.SUBSCRIBE)
@Component
public class SubscribeRequestProcessor extends AbstractSipRequestProcessor {

    @Autowired
    private SubscribeService subscribeService;

    @Override
    public void request(RequestEvent event) {
        log.info("SubscribeRequestProcessor request method invoke!!!");
    }

    @SneakyThrows
    @Override
    public void response(ResponseEvent responseEvent) {
        log.info("订阅后响应打印日志。。。");
        SIPResponse sipResponse = (SIPResponse) responseEvent.getResponse();
        int statusCode = sipResponse.getStatusCode();

        if (statusCode == Response.OK) {
            ExpiresHeader expiresHeader = sipResponse.getExpires();
            int expires = expiresHeader.getExpires();
            byte[] rawContent = sipResponse.getRawContent();
            if(rawContent == null){
                log.warn("订阅响应内容为空");
                return;
            }
            ResponseMessage responseMessage = XmlUtil.parse(rawContent, ResponseMessage.class);
            log.info("订阅响应内容:{}", JsonMapper.nonDefaultMapper().toJson(responseMessage));
            subscribeService.updateSubscribeExpires(responseMessage, expires);

            log.info("订阅成功");
        } else {
            log.info("订阅失败，statusCode:{}", statusCode);
        }
    }

}
