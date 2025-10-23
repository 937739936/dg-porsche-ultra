package com.shdatalink.framework.mysql.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * Entity基类(使用雪花算法ID）
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BaseEntityWithAssignId extends BaseEntity {

    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;


}
