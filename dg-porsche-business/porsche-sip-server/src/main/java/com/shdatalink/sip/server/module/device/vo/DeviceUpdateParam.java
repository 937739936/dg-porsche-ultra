package com.shdatalink.sip.server.module.device.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.shdatalink.sip.server.gb28181.core.bean.constants.DeviceManufacturerEnum;
import com.shdatalink.sip.server.gb28181.core.bean.constants.TransportTypeEnum;
import com.shdatalink.sip.server.module.device.enums.ProtocolTypeEnum;
import com.shdatalink.sip.server.module.device.valid.PullValid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class DeviceUpdateParam {
    /**
     * 设备id
     */
    @NotBlank(message = "设备id必填")
    private String deviceId;
    /**
     * 设备名称
     */
    @NotBlank(message = "设备名称必填")
    private String name;
    /**
     * 协议类型
     */
    @NotNull(message = "协议类型不能为空")
    private ProtocolTypeEnum protocolType;
    /**
     * 密码
     */
    private String password;

    /**
     * 厂商
     */
    @JsonDeserialize(using = DeviceManufacturerEnum.Deserializer.class)
    private DeviceManufacturerEnum manufacturer;

    /**
     * 拉流地址
     */
    @NotBlank(message = "拉流地址不能为空", groups = {PullValid.class})
    @Length(max = 200)
    private String streamUrl;

    /**
     * 备注
     */
    @Length(max = 100)
    private String remark;
    /**
     * 流传输模式
     */
    @NotNull(message = "流传输模式不能为空", groups = {PullValid.class})
    private TransportTypeEnum transport;
    /**
     * 是否开启音频
     */
    @NotNull(message = "开启音频不能为空", groups = {PullValid.class})
    private Boolean enableAudio;
}
