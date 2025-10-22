package com.shdatalink.sip.server.gb28181.core.bean.model.device.message.control;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.shdatalink.sip.server.gb28181.core.bean.model.device.message.control.enums.ControlCmdType;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
@RegisterForReflection
public class SnapshotConfig extends ControlMessage {
    @Builder.Default
    private ControlCmdType cmdType = ControlCmdType.DeviceControl;
    @JacksonXmlProperty(localName = "SnapShotConfig")
    private SnapShotConfig snapShotConfig;

    @Data
    public static class SnapShotConfig {
        /**
         * 连拍张数，最多10张,当手动抓拍时，取值为1
         */
        @JacksonXmlProperty(localName = "SnapNum")
        private Integer snapNum;
        /**
         * 单张抓拍间隔时间，单位：秒（必选），取值范围：最短1秒-
         */
        @JacksonXmlProperty(localName = "Interval")
        private Integer interval;
        /**
         * 抓拍图像上传路径
         */
        @JacksonXmlProperty(localName = "UploadURL")
        private String uploadURL;
        /**
         * 会话ID，由平台生成，用于关联抓拍的图像与平台请求（必选），SessionID由大小写英文字母、数字、短划线组成，长度不小于32字节，不大于128字节。
         */
        @JacksonXmlProperty(localName = "SessionID")
        private String sessionID;
    }
}
