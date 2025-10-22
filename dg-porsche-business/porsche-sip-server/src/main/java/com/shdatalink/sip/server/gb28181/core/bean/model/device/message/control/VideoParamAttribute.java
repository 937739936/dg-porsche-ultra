package com.shdatalink.sip.server.gb28181.core.bean.model.device.message.control;

import com.fasterxml.jackson.annotation.JsonInclude;
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
public class VideoParamAttribute extends ControlMessage {
    @Builder.Default
    private ControlCmdType cmdType = ControlCmdType.DeviceConfig;
    @JacksonXmlProperty(localName = "VideoParamAttribute")
    private VideoParamAttributeCfgType videoParamAttribute;

    @Data
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class VideoParamAttributeCfgType {
        /**
         * 视频流编号（必选），用于实时视音频点播时指定码流编号。0-主码流；1-子码
         * 流1；2-子码流2，以此类推
         */
        @JacksonXmlProperty(localName = "StreamNumber")
        private  Integer streamNumber;
        /**
         * 视频编码格式当前配置值(必选)
         */
        @JacksonXmlProperty(localName = "VideoFormat")
        private String videoFormat;
        /**
         * 分辨率当前配置值(必选)
         */
        @JacksonXmlProperty(localName = "Resolution")
        private String resolution;
        /**
         * 帧率当前配置值(必选)
         */
        @JacksonXmlProperty(localName = "FrameRate")
        private String frameRate;
        /**
         * 码率类型配置值(必选)
         */
        @JacksonXmlProperty(localName = "BitRateType")
        private String bitRateType;
        /**
         * 视频码率配置值(固定码率时必选)
         */
        @JacksonXmlProperty(localName = "VideoBitRate")
        private String videoBitRate;
    }
}
