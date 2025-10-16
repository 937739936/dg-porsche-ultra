package com.shdatalink.sip.server.gb28181;

import com.shdatalink.sip.server.gb28181.core.bean.constants.ConfigDownloadType;
import com.shdatalink.sip.server.gb28181.core.bean.model.base.GbDevice;
import com.shdatalink.sip.server.gb28181.core.bean.model.device.message.query.*;
import com.shdatalink.sip.server.gb28181.core.bean.model.device.message.query.response.ConfigDownload;
import com.shdatalink.sip.server.gb28181.core.bean.model.device.message.query.response.DeviceCatalog;
import com.shdatalink.sip.server.gb28181.core.bean.model.device.message.query.response.DeviceInfo;
import com.shdatalink.sip.server.gb28181.core.bean.model.device.message.query.response.DeviceStatus;
import com.shdatalink.sip.server.gb28181.core.builder.GBRequest;
import com.shdatalink.sip.server.util.SipUtil;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Log4j
public class SipMessageTemplate {

    @SneakyThrows
    private <T> T execute(GbDevice device, QueryMessage query) {
        return GBRequest.message(device).newSession().execute(query).get();
    }

    public DeviceInfo getDeviceInfo(GbDevice device) {
        QueryMessage query = DeviceInfoQuery.builder()
                .sn(SipUtil.generateSn())
                .deviceId(device.getChannelId())
                .build();
        return execute(device, query);
    }


    public DeviceStatus getDeviceStatus(GbDevice device) {
        return execute(device, DeviceStatusQuery.builder()
                .sn(SipUtil.generateSn())
                .deviceId(device.getChannelId()).build());
    }


    public DeviceCatalog getDeviceCatalog(GbDevice device) {
        return execute(device, CataLogQuery.builder()
                .sn(SipUtil.generateSn())
                .deviceId(device.getChannelId()).build());
    }

    public ConfigDownload getConfig(GbDevice device, List<ConfigDownloadType> type) {
        ConfigDownloadQuery query = ConfigDownloadQuery.builder().configType(type.stream().map(Enum::name).collect(Collectors.joining("/"))).build();
        return execute(device, query);
    }
}
