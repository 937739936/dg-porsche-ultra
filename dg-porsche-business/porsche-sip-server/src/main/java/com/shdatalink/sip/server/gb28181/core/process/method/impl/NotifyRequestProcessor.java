package com.shdatalink.sip.server.gb28181.core.process.method.impl;

import com.shdatalink.sip.server.gb28181.core.bean.annotations.SipEvent;
import com.shdatalink.sip.server.gb28181.core.bean.constants.SipEnum;
import com.shdatalink.sip.server.gb28181.core.bean.model.base.DeviceBase;
import com.shdatalink.sip.server.gb28181.core.bean.model.device.message.notify.response.DeviceAlarm;
import com.shdatalink.sip.server.gb28181.core.bean.model.device.message.notify.response.DeviceMobilePosition;
import com.shdatalink.sip.server.gb28181.core.bean.model.device.message.notify.response.DeviceNotifyCatalog;
import com.shdatalink.sip.server.gb28181.core.builder.ResponseBuilder;
import com.shdatalink.sip.server.gb28181.core.builder.SipPublisher;
import com.shdatalink.sip.server.gb28181.core.process.method.AbstractSipRequestProcessor;
import com.shdatalink.sip.server.module.alarmplan.service.AlarmRecordService;
import com.shdatalink.sip.server.module.device.service.DeviceChannelService;
import com.shdatalink.sip.server.utils.SipUtil;
import com.shdatalink.sip.server.utils.XmlUtil;
import gov.nist.javax.sip.message.SIPRequest;
import io.quarkus.runtime.Startup;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;

@Startup
@Slf4j
@SipEvent(SipEnum.Method.NOTIFY)
@ApplicationScoped
public class NotifyRequestProcessor extends AbstractSipRequestProcessor {

    @Inject
    DeviceChannelService deviceChannelService;
    @Inject
    AlarmRecordService alarmRecordService;

    @Override
    public void request(RequestEvent event) {
        SIPRequest request = (SIPRequest) event.getRequest();
        byte[] content = request.getRawContent();
        DeviceBase deviceBase = XmlUtil.parse(content, DeviceBase.class);

        log.info("收到 {} 通知,原始报文:{}", deviceBase.getCmdType(), new String(content));
        String key = SipUtil.genSubscribeKey(deviceBase.getCmdType(), deviceBase.getDeviceId(), deviceBase.getSn());

        if (deviceBase.getCmdType().equals(SipEnum.Cmd.Catalog.name())) {
            DeviceNotifyCatalog deviceCatalog = XmlUtil.parse(content, DeviceNotifyCatalog.class);
            SipPublisher.handler(key).ofOk(deviceCatalog);
            if (deviceCatalog.getDeviceList() != null && CollectionUtils.isNotEmpty(deviceCatalog.getDeviceList().getDeviceList())) {
                DeviceNotifyCatalog.DeviceCatalogList.DeviceCatalogItem deviceCatalogItem = deviceCatalog.getDeviceList().getDeviceList().get(0);
                log.info("命令类型 : {}；sn : {}；设备Id : {}；通道id ：{}；事件类型:{}；通道状态: {}", deviceBase.getCmdType(),deviceBase.getSn(),deviceBase.getDeviceId(), deviceCatalogItem.getDeviceId(), deviceCatalogItem.getEvent(), deviceCatalogItem.getStatus());
                deviceChannelService.renewalChannelEvent(deviceBase.getDeviceId(),deviceCatalog.getDeviceList().getDeviceList());
            }
        } else if (deviceBase.getCmdType().equals(SipEnum.Cmd.Alarm.name())) {
            DeviceAlarm deviceAlarm = XmlUtil.parse(content, DeviceAlarm.class);
            String alarmType = deviceAlarm.getInfo() == null ? deviceAlarm.getAlarmType() : deviceAlarm.getInfo().getAlarmType();
            log.info("命令类型 : {}；sn : {}；设备Id : {}；报警级别 : {}；报警方式 : {}；报警类型 : {}", deviceBase.getCmdType(),deviceBase.getSn(),deviceBase.getDeviceId(), deviceAlarm.getAlarmPriority(), deviceAlarm.getAlarmMethod(), alarmType);
            alarmRecordService.handle(deviceAlarm);
        } else if (deviceBase.getCmdType().equals(SipEnum.Cmd.MobilePosition.name())) {
            DeviceMobilePosition deviceMobilePosition = XmlUtil.parse(content, DeviceMobilePosition.class);
            log.info("命令类型 : {}；sn : {}；设备Id : {}；经度 : {}；纬度 : {}", deviceBase.getCmdType(),deviceBase.getSn(),deviceBase.getDeviceId(), deviceMobilePosition.getLongitude(), deviceMobilePosition.getLatitude());
        } else {
            log.info("收到其他类型消息,无法处理.{}", deviceBase.getCmdType());
        }

        ResponseBuilder.of(event).ok().execute();
    }

    @Override
    public void response(ResponseEvent responseEvent) {
        log.info("通知后响应打印日志。。。");
    }

}
