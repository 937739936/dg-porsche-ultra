package com.shdatalink.sip.server.module.device.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.shdatalink.sip.server.gb28181.core.bean.constants.DeviceManufacturerEnum;
import com.shdatalink.sip.server.gb28181.core.bean.constants.DeviceTypeEnum;
import com.shdatalink.sip.server.gb28181.core.bean.constants.TransportTypeEnum;
import com.shdatalink.sip.server.module.device.enums.ProtocolTypeEnum;
import com.shdatalink.sip.server.module.device.valid.Gb28181Valid;
import com.shdatalink.sip.server.module.device.valid.PullValid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

@Data
public class DeviceAddParam {
    /**
     * 设备名称
     */
    @NotBlank(message = "设备名称不能为空")
    @Length(min = 1, max = 20)
    private String name;
    /**
     * 协议类型
     */
    @NotNull(message = "协议类型不能为空")
    private ProtocolTypeEnum protocolType;
    /**
     * 生产厂商
     */
    @NotNull(message = "生产厂商不能为空", groups = {Gb28181Valid.class})
    @JsonDeserialize(using = DeviceManufacturerEnum.Deserializer.class)
    private DeviceManufacturerEnum manufacturer;

    /**
     * 注册密码
     */
    @NotBlank(message = "注册密码不能为空", groups = {Gb28181Valid.class})
    @Length(min = 6, max = 20)
    private String registerPassword;

    /**
     * 设备类型
     */
    @NotNull(message = "设备类型不能为空", groups = {Gb28181Valid.class})
    private DeviceTypeEnum deviceType;

    /**
     * 通道数
     */
    @NotNull(message = "通道数不能为空", groups = {Gb28181Valid.class})
    @Range(min = 1, max = 300)
    private Integer channelCount = 1;

    /**
     * 拉流地址
     */
    @NotBlank(message = "拉流地址不能为空", groups = {PullValid.class})
    @Length(max = 200)
    private String streamUrl;

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
    /**
     * 备注
     */
    @Length(max = 100)
    private String remark;


}
