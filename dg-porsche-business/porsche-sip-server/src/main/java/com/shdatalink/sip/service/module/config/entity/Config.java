package com.shdatalink.sip.service.module.config.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.shdatalink.sip.service.module.config.enums.ConfigTypesEnum;
import lombok.Data;

@Data
@TableName("t_config")
public class Config {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private String value;
    private ConfigTypesEnum type;
}
