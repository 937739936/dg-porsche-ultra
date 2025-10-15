package com.shdatalink.sip.server.gb28181.constants;

import com.shdatalink.framework.common.model.IDict;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DeviceTypeEnum implements IDict<String> {

    // 录像机
    NVR("NVR", "录像机", 118),
    // 摄像头
    IPC("IPC", "摄像头", 132),
    ;


    private int identifier;

    DeviceTypeEnum(String code, String text, int identifier) {
        this.identifier = identifier;
        init(code, text);
    }


    public static DeviceTypeEnum fromText(String deviceType) {
        for (DeviceTypeEnum deviceTypeEnum : DeviceTypeEnum.values()) {
            if (deviceTypeEnum.getText().equalsIgnoreCase(deviceType)) {
                return deviceTypeEnum;
            }
        }
        return null;
    }
}
