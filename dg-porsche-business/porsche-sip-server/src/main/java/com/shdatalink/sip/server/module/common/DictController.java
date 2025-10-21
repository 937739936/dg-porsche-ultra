package com.shdatalink.sip.server.app.common;

import com.shdatalink.framework.common.exception.BaseResultCodeEnum;
import com.shdatalink.framework.common.model.DictBean;
import com.shdatalink.framework.common.model.IDict;
import com.shdatalink.sip.server.gb28181.core.bean.constants.DeviceManufacturerEnum;
import com.shdatalink.sip.server.gb28181.core.bean.constants.DeviceTypeEnum;
import com.shdatalink.sip.server.gb28181.core.bean.constants.MediaStreamModeEnum;
import com.shdatalink.sip.server.gb28181.core.bean.constants.TransportTypeEnum;
import com.shdatalink.sip.server.module.alarmplan.enums.AlarmMethodEnum;
import com.shdatalink.sip.server.module.alarmplan.enums.AlarmPriorityEnum;
import com.shdatalink.sip.server.module.alarmplan.enums.AlarmTypeEnum;
import com.shdatalink.sip.server.module.alarmplan.enums.EventTypeEnum;
import com.shdatalink.sip.server.module.common.enums.EnableDisableEnum;
import com.shdatalink.sip.server.module.common.enums.OperateLogTypeEnum;
import com.shdatalink.sip.server.module.device.enums.MessageTypeEnum;
import com.shdatalink.sip.server.module.device.enums.ProtocolTypeEnum;
import com.shdatalink.sip.server.module.device.enums.PtzTypeEnum;
import com.shdatalink.sip.server.module.device.enums.SIPProtocolEnum;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;


import java.util.*;

/**
 * APP/通用接口/字典
 */
@Path("admin/dict")
public class DictController {

    private final static Map<String, Map<String, String>> dict = new HashMap<>();

    static {
        Set<Class<? extends IDict<?>>> classes = null;
        try {
            classes = scanEnums();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        for (Class<? extends IDict> enumClass : classes) {
            IDict[] constants = enumClass.getEnumConstants();
            if (constants == null) continue;
            Map<String, String> map = new LinkedHashMap<>();
            for (IDict constant : constants) {
                map.put(constant.getCode().toString(), constant.getText());
            }
            dict.put(enumClass.getSimpleName(), map);
        }

    }

    /**
     * 查询所有字典
     * @return
     */
    @GET
    @Path("all")
    public Map<String, Map<String, String>> all() {
        return dict;
    }

    public static Set<Class<? extends IDict<?>>> scanEnums() {
        Set<Class<? extends IDict<?>>> result = new HashSet<>();
        result.add(EnableDisableEnum.class);
        result.add(OperateLogTypeEnum.class);
        result.add(AlarmMethodEnum.class);
        result.add(AlarmPriorityEnum.class);
        result.add(AlarmTypeEnum.class);
        result.add(BaseResultCodeEnum.class);
        result.add(DeviceManufacturerEnum.class);
        result.add(DeviceTypeEnum.class);
        result.add(EventTypeEnum.class);
        result.add(MediaStreamModeEnum.class);
        result.add(MessageTypeEnum.class);
        result.add(ProtocolTypeEnum.class);
        result.add(PtzTypeEnum.class);
        result.add(SIPProtocolEnum.class);
        result.add(TransportTypeEnum.class);
        return result;
    }

}
