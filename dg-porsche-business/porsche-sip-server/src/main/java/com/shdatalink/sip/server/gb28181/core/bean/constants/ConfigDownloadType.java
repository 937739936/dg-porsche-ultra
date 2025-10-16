package com.shdatalink.sip.server.gb28181.core.bean.constants;

public enum ConfigDownloadType {
    // 基本参数配置
    BasicParam,
    // 视频参数范围配置
    VideoParamOpt,
    // SVAC编码配置(大华无响应)
    SVACEncodeConfig,
    // SVAC解码配置(大华无响应)
    SVACDecodeConfig,
    // 视频参数属性配置
    VideoParamAttribute,
    // 录像计划
    VideoRecordPlan,
    // 报警录像
    VideoAlarmRecord,
    // 视频画面遮挡
    PictureMask,
    // 画面翻转
    FrameMirror,
    // 报警上报开关
    AlarmReport,
    // 前端OSD设置
    OSDConfig,
    // 图像抓拍配置
    SnapShotConfig
}