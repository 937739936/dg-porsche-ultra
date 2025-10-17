package com.shdatalink.sip.server.integration.vo;

import com.shdatalink.sip.server.module.device.enums.PtzControlAction;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

@Data
public class IntegrationPtzControlParam {
    /**
     * 通道id
     */
    @NotBlank(message = "通道id不能为空")
    private String channelId;
    /**
     * 动作
     */
    @NotNull(message = "动作不能为空")
    private PtzControlAction action;
    /**
     * 速度
     */
    @Range(min = 1, max = 10)
    @NotNull(message = "速度不能为空")
    private Integer speed;

}
