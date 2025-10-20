package com.shdatalink.sip.server.module.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.shdatalink.framework.mysql.entity.BaseEntity;
import com.shdatalink.sip.server.module.common.enums.OperateLogTypeEnum;
import lombok.Data;

/**
 * 操作日志表
 */
@Data
@TableName("t_operate_log")
public class OperateLog extends BaseEntity {

	/**
	 * IP
	 */
	private String ip;

	/**
	 * 请求参数
	 */
	private String requestParam;

	/**
	 * 请求类型
	 */
	private String requestType;

	/**
	 * 请求方法
	 */
	private String method;
	/**
	 * 操作详细日志
	 */
	private String logContent;
	/**
	 * 操作类型
	 */
	private OperateLogTypeEnum operateType;
	/**
	 * 操作人用户名称
	 */
	private String username;

}
