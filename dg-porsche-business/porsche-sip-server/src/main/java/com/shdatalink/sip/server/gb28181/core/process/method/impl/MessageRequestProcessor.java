package com.shdatalink.sip.server.gb28181.core.process.method.impl;

import com.shdatalink.framework.common.service.EventPublisher;
import com.shdatalink.framework.json.utils.JsonUtil;
import com.shdatalink.sip.server.gb28181.core.bean.annotations.SipEvent;
import com.shdatalink.sip.server.gb28181.core.bean.constants.SipEnum;
import com.shdatalink.sip.server.gb28181.core.bean.model.base.DeviceBase;
import com.shdatalink.sip.server.gb28181.core.bean.model.device.message.notify.response.DeviceMobilePosition;
import com.shdatalink.sip.server.gb28181.core.bean.model.device.message.notify.response.MediaStatus;
import com.shdatalink.sip.server.gb28181.core.bean.model.device.message.query.response.*;
import com.shdatalink.sip.server.gb28181.core.builder.ResponseBuilder;
import com.shdatalink.sip.server.gb28181.core.builder.SipPublisher;
import com.shdatalink.sip.server.gb28181.core.builder.SipPublisherHandler;
import com.shdatalink.sip.server.gb28181.core.process.method.AbstractSipRequestProcessor;
import com.shdatalink.sip.server.module.alarmplan.service.AlarmRecordService;
import com.shdatalink.sip.server.module.device.entity.Device;
import com.shdatalink.sip.server.module.device.service.DeviceChannelService;
import com.shdatalink.sip.server.module.device.service.DeviceService;
import com.shdatalink.sip.server.module.plan.event.MediaDownloadDoneEvent;
import com.shdatalink.sip.server.utils.SipUtil;
import com.shdatalink.sip.server.utils.XmlUtil;
import gov.nist.javax.sip.message.SIPRequest;
import io.quarkus.runtime.Startup;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.address.Address;
import java.util.Optional;

@Startup
@SipEvent(SipEnum.Method.MESSAGE)
@ApplicationScoped
@Slf4j
public class MessageRequestProcessor extends AbstractSipRequestProcessor {
    @Inject
    DeviceService deviceService;

    @Inject
    DeviceChannelService deviceChannelService;

    @Inject
    AlarmRecordService alarmRecordService;
    @Inject
    EventPublisher eventPublisher;

    @SneakyThrows
    @Override
    public void request(RequestEvent event) {
        SIPRequest request = (SIPRequest) event.getRequest();
        Address address = SipUtil.getAddressFromFromHeader(request);
        byte[] content = request.getRawContent();
        DeviceBase deviceBase = XmlUtil.parse(content, DeviceBase.class);
        if("Keepalive".equals(deviceBase.getCmdType())){
            log.info("收到 {} 响应", deviceBase.getCmdType());
        }else{
            log.info("收到 {} 响应,原始报文:{}", deviceBase.getCmdType(), new String(content));
        }

        String key = SipUtil.genSubscribeKey(deviceBase.getCmdType(), deviceBase.getDeviceId(), deviceBase.getSn());

        if (deviceBase.getCmdType().equals(SipEnum.Cmd.Keepalive.name())) {
            Optional<Device> deviceOptional = deviceService.getByDeviceId(deviceBase.getDeviceId());
            if (deviceOptional.isPresent()) {
                Device device = deviceOptional.get();
                if (!device.getEnable()) {
                    log.info("设备{}已禁用，返回403", deviceBase.getDeviceId());
                    ResponseBuilder.of(event).forbidden().execute();
                    return;
                }
                if (device.getRegisterTime() == null) {
                    log.info("设备{}未注册，返回403", deviceBase.getDeviceId());
                    ResponseBuilder.of(event).forbidden().execute();
                    return;
                }
            } else {
                log.info("设备{}不存在，返回403", deviceBase.getDeviceId());
                ResponseBuilder.of(event).forbidden().execute();
                return;
            }

//            if (!deviceService.updateKeepAliveTime(deviceBase.getDeviceId())) {
//                log.info("未更新keepAliveTime「{}」", deviceBase.getCmdType());
//                ResponseBuilder.of(event).forbidden().execute();
//                return;
//            }
//            log.info("回复请求「{}」OK", deviceBase.getCmdType());

        } else if (deviceBase.getCmdType().equals(SipEnum.Cmd.Catalog.name())) {
            SipPublisherHandler handler = SipPublisher.handler(key);
            DeviceCatalog deviceCatalog = handler.staging(XmlUtil.parse(content, DeviceCatalog.class));
            if (deviceCatalog.getSumNum() > 0) {
                if (deviceCatalog.getDeviceList().getDeviceList().size() == deviceCatalog.getSumNum()) {
                    handler.ofOk(deviceCatalog);
                }
            } else {
                handler.ofFail("没有设备信息");
            }

        } else if (deviceBase.getCmdType().equals(SipEnum.Cmd.DeviceInfo.name())) {

            DeviceInfo deviceInfo = XmlUtil.parse(content, DeviceInfo.class);
            SipPublisher.handler(key).ofOk(deviceInfo);

        } else if (deviceBase.getCmdType().equals(SipEnum.Cmd.DeviceStatus.name())) {

            DeviceStatus deviceStatus = XmlUtil.parse(content, DeviceStatus.class);
            SipPublisher.handler(key).ofOk(deviceStatus);

        } else if (deviceBase.getCmdType().equals(SipEnum.Cmd.ConfigDownload.name())) {
            ConfigDownload configDownload = XmlUtil.parse(content, ConfigDownload.class);
            SipPublisher.handler(key).ofOk(configDownload);
        } else if (deviceBase.getCmdType().equals(SipEnum.Cmd.RecordInfo.name())) {
            SipPublisherHandler handler = SipPublisher.handler(key);
            RecordInfo recordInfo = handler.staging(XmlUtil.parse(content, RecordInfo.class));
            if (recordInfo.getSumNum() != null && recordInfo.getSumNum() > 0) {
                if (recordInfo.getRecordList().size() == recordInfo.getSumNum()) {
                    handler.ofOk(recordInfo);
                }
            } else {
                handler.ofFail("终端没有录像文件");
            }
        } else if (deviceBase.getCmdType().equals(SipEnum.Cmd.MobilePosition.name())) {
            DeviceMobilePosition deviceMobilePosition = XmlUtil.parse(content, DeviceMobilePosition.class);
            log.info("deviceMobilePosition, {}", JsonUtil.toJsonString(deviceMobilePosition));
        } else if (deviceBase.getCmdType().equals(SipEnum.Cmd.MediaStatus.name())) {
            MediaStatus mediaStatus = XmlUtil.parse(content, MediaStatus.class);
            eventPublisher.fireAsync(new MediaDownloadDoneEvent(mediaStatus.getDeviceId()));
            log.info("mediaStatus, {}", JsonUtil.toJsonString(mediaStatus));
        } else {
            log.info("收到其他类型消息,无法处理.{}", deviceBase.getCmdType());
        }

        ResponseBuilder.of(event).ok().execute();
    }

    @Override
    public void response(ResponseEvent responseEvent) {
    }
}
