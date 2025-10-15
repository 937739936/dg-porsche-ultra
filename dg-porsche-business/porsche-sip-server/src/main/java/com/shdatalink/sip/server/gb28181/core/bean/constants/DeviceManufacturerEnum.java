package com.shdatalink.sip.server.gb28181.core.bean.constants;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.shdatalink.framework.common.model.IDict;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.IOException;

@Getter
@AllArgsConstructor
public enum DeviceManufacturerEnum implements IDict<String> {


    NONE("NONE", "未知", "0"),
    HIKVISION("Hikvision", "海康威视", "1"),
    DAHUATECH("Dahua", "大华", "2"),
    UNIVIEW("UNIVIEW", "宇视", "3"),
    ;

    private final String identifier;

    DeviceManufacturerEnum(String code, String text, String identifier) {
        init(code, text);
        this.identifier = identifier;
    }

    public static DeviceManufacturerEnum fromCode(String manufacturer) {
        for (DeviceManufacturerEnum value : DeviceManufacturerEnum.values()) {
            if (value.getCode().equalsIgnoreCase(manufacturer)) {
                return value;
            }
        }
        return DeviceManufacturerEnum.NONE;
    }

    public static DeviceManufacturerEnum fromText(String manufacturer) {
        for (DeviceManufacturerEnum value : DeviceManufacturerEnum.values()) {
            if (value.getText().equalsIgnoreCase(manufacturer)) {
                return value;
            }
        }
        return null;
    }

    public static class Deserializer extends JsonDeserializer<DeviceManufacturerEnum> {
        @Override
        public DeviceManufacturerEnum deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
            final String code = p.getValueAsString();
            return DeviceManufacturerEnum.fromCode(code);
        }
    }
}
