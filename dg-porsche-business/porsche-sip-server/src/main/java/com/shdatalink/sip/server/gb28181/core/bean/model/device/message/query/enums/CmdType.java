package com.shdatalink.sip.server.gb28181.core.bean.model.device.message.query.enums;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;

import java.util.stream.Stream;

/**
 * 命令类型
 */
public enum CmdType {
    NONE,

    /**
     * 设备信息
     */
    DeviceInfo,


    /**
     * 设备能力集
     */
    Capability,

    /**
     * keepalive
     */
    Keepalive,

    /**
     * 报警
     */
    Alarm,

    /**
     * 媒体状态
     */
    MediaStatus,

    /**
     * 移动位置
     */
    MobilePosition,

    /**
     * 目录
     */
    Catalog,

    /**
     * 设备状态
     */
    DeviceStatus,

    /**
     * 记录信息
     */
    RecordInfo,

    /**
     * 设备控制
     */
    DeviceControl,

    /**
     * 控制
     */
    Control,

    /**
     * 广播
     */
    Broadcast,

    /**
     * 配置下载
     */
    ConfigDownload,

    /**
     * 设备配置
     */
    DeviceConfig,

    /**
     * 预设查询
     */
    PresetQuery,
    ;

    /**
     * 根据传入的方法匹配枚举值
     *
     * @param cmd cmd
     * @return {@link CmdType}
     */
    public static CmdType resolve(String cmd) {
        return Stream.of(CmdType.values()).filter(item -> Strings.CI.equals(item.name(), cmd)).findFirst().orElse(null);
    }

}