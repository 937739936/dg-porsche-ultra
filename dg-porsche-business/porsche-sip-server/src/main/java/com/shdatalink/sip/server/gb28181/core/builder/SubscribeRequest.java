package com.shdatalink.sip.server.gb28181.core.builder;

import com.shdatalink.framework.common.exception.BizException;
import com.shdatalink.sip.server.gb28181.core.bean.constants.SipConstant;
import com.shdatalink.sip.server.gb28181.core.bean.model.base.GbDevice;
import com.shdatalink.sip.server.gb28181.core.bean.model.base.Message;
import com.shdatalink.sip.server.gb28181.core.bean.model.device.message.notify.AlarmSubscribe;
import com.shdatalink.sip.server.gb28181.core.bean.model.device.message.notify.CatalogSubscribe;
import com.shdatalink.sip.server.gb28181.core.bean.model.device.message.notify.MobilePositionSubscribe;
import com.shdatalink.sip.server.gb28181.core.bean.model.device.message.notify.SubscribeMessage;
import com.shdatalink.sip.server.utils.SipUtil;
import com.shdatalink.sip.server.utils.XmlUtil;
import gov.nist.javax.sip.message.SIPRequest;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.sip.SipException;
import javax.sip.message.Request;

@Slf4j
public class SubscribeRequest extends AbstractRequest implements GBRequest {

    @Setter
    private Object content;
    private String key;

    public SubscribeRequest(GbDevice toDevice) {
        super(toDevice, SIPRequest.SUBSCRIBE);
    }

    @Override
    public SubscribeRequest execute(Object content) {
        this.content = content;
        byte[] xmlData = XmlUtil.toByteXml(content, SipConstant.CHARSET);
        String eventType;
        //订阅过期时间
        int expires = 60 * 60 * 2;
        if (!(content instanceof SubscribeMessage subscribeMessage)) {
            throw new BizException("当前消息类型不是订阅类型");
        }
        Boolean isSubscribe = subscribeMessage.getIsSubscribe();
        if(isSubscribe != null && !isSubscribe){
            expires = 0;
        }
        if(content instanceof AlarmSubscribe){
            eventType = "presence";
        }else if(content instanceof CatalogSubscribe){
            eventType = "Catalog;id=" + (int) ((Math.random() * 9 + 1) * 10000000);
        }else if(content instanceof MobilePositionSubscribe){
            eventType = "presence";
        }else {
            throw new BizException("未知的订阅类型");
        }
        subscribeRequest(Request.SUBSCRIBE, SipConstant.XML, xmlData, eventType, expires);
        return this;
    }

    @Override
    public <T> T get() throws SipException {
        log.info("SubscribeRequest:>>>>123");
        if (!(content instanceof SubscribeMessage subscribeMessage)) {
            return null;
        }
        if (key == null) {
            this.key = SipUtil.genSubscribeKey(subscribeMessage.getCmdType().name(), subscribeMessage.getDeviceId(), subscribeMessage.getSn());
        }
        FutureEvent<T> futureEvent = SipPublisher.subscribe(key).build();
        if (futureEvent.get().getCode() == Message.EnumState.OK) {
            return futureEvent.get().getData();
        } else {
            log.error("查询信息出错:"+ futureEvent.get().getCode());
            throw new SipException("SUBSCRIBE请求出错: cmdType="+subscribeMessage.getCmdType().name()+"; code = " + futureEvent.get().getCode());
        }
    }
}
