package com.shdatalink.sip.service.module.device.enums;

import com.shdatalink.framework.common.model.IDict;
import lombok.Getter;

import java.util.Objects;

@Getter
public enum PtzTypeEnum implements IDict<String> {
    None("None", "未知", null),
    PTZCamera("PTZCamera", "球机", 1),
    DomeCamera("DomeCamera", "半球机", 2),
    BulletCamera("BulletCamera", "枪机",3),
    RemoteBulletCamera("RemoteBulletCamera", "遥控枪机",4),
    RemoteDomeCamera("RemoteDomeCamera", "遥控半球",5),
    ;

    private final Integer identifier;

    PtzTypeEnum(String code, String text, Integer identifier) {
        init(code, text);
        this.identifier = identifier;
    }

    public static PtzTypeEnum getByIdentifier(Integer identifier) {
        for (PtzTypeEnum ptzTypeEnum : PtzTypeEnum.values()) {
            if (Objects.equals(identifier, ptzTypeEnum.getIdentifier())) {
                return ptzTypeEnum;
            }
        }
        return None;
    }
}
