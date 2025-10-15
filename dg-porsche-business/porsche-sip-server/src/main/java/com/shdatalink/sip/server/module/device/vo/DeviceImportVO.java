package com.shdatalink.sip.server.module.device.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

@Data
public class DeviceImportVO {
    @ExcelProperty("设备名称")
    @NotBlank(message = "设备名称不能为空")
    @Length(min = 1, max = 20)
    private String name;

    @ExcelProperty("生产厂商")
    @NotBlank(message = "生产厂商不能为空")
    private String manufacturer;

    @ExcelProperty("注册密码")
    @NotBlank(message = "注册密码不能为空")
    @Length(min = 6, max = 20)
    private String registerPassword;

    @ExcelProperty("设备类型")
    @NotBlank(message = "设备类型不能为空")
    private String deviceType;

    @ExcelProperty("通道数")
    @NotNull
    @Range(min = 1, max = 300, message = "通道数最少{min}个，最多{max}个")
    private Integer channelCount = 1;

}
