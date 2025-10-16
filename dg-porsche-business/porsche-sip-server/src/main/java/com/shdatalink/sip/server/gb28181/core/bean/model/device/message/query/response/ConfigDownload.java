package com.shdatalink.sip.server.gb28181.core.bean.model.device.message.query.response;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.shdatalink.sip.server.gb28181.core.bean.model.base.DeviceBase;
import lombok.Data;

import java.util.List;

@Data
@JacksonXmlRootElement(localName = "Response")
public class ConfigDownload extends DeviceBase {
    /**
     * 基本参数
     */
    private BasicParam basicParam;
    /**
     * 视频参数范围
     */
    private VideoParamOpt videoParamOpt;
    /**
     * SVAC编码配置
     */
    private SVACEncodeConfig SVACEncodeConfig;
    /**
     * SVAC解码配置
     */
    private SVACDecodeConfig SVACDecodeConfig;
    /**
     * 视频参数属性配置
     */
    private List<VideoParamAttribute> videoParamAttribute;
    /**
     * 录像计划配置
     */
    private VideoRecordPlan videoRecordPlan;
    /**
     * 报警录像配置
     */
    private VideoAlarmRecord videoAlarmRecord;
    /**
     * 视频画面遮挡配置
     */
    private PictureMask pictureMask;
    /**
     * 0-不启用镜像，基准画面
     * 1-水平镜像（左右翻转）
     * 2-上下镜像（上下翻转）
     * 3-中心镜像（上下左右都翻转）
     */
    private Integer frameMirror;

    /**
     * 报警上报开关
     */
    private AlarmReport alarmReport;

    /**
     * 前端OSD配置
     */
    @JacksonXmlProperty(localName = "OSDConfig")
    private OSDConfig OSDConfig;
    /**
     * 图像抓拍配置
     */
    private SnapShotConfig snapShot;


    @Data
    public static class BasicParam {
        /**
         * 设备名称（可选）
         */
        private String name;
        /**
         * 注册过期时间(可选)
         */
        private Integer expiration;
        /**
         * 心跳间隔时间(可选)
         */
        private Integer heartBeatInterval;
        /**
         * 心跳超时次数(可选)
         */
        private Integer heartBeatCount;
    }

    @Data
    public static class VideoParamOpt {
        /**
         * 下载倍速范围（可选），各可选参数以“/”分隔
         */
        private String downloadSpeed;
        /**
         * 摄像机支持的分辨率（可选），可有多个分辨率值，各个取值见以“/”分隔。
         */
        private String resolution;
    }

    @Data
    public static class SVACEncodeConfig {
        /**
         * 感兴趣区域参数
         */
        private ROIParam ROIParam;
        /**
         * SVC参数
         */
        private SVCParam SVCParam;
        /**
         * 监控专用信息参数
         */
        private SurveillanceParam surveillanceParam;
        /**
         * 音频参数
         */
        private AudioParam audioParam;
        @Data
        public static class ROIParam {
            /**
             * 感兴趣区域开关，取值0：关闭，1：打开
             */
            private Integer RIOFlag;
            /**
             * 感兴趣区域数量，取值范围0～16
             */
            private Integer RIONumber;
            /**
             * 感兴趣区域（可选）
             */
            @JacksonXmlElementWrapper(useWrapping = false)
            private List<ROIParamItem> item;
        }

        @Data
        public static class ROIParamItem {
            /**
             * 感兴趣区域编号，取值范围1～16
             */
            private Integer ROISeq;
            /**
             * 感兴趣区域左上角坐标，取值为将图像按32x32划分后该坐标所在块按光栅扫描顺序的序号
             */
            private Integer topLeft;
            /**
             * 感兴趣区域右下角坐标，取值为将图像按32x32划分后该坐标所在块按光栅扫描顺序的序号
             */
            private Integer bottomRight;
            /**
             * ROI区域编码质量等级，取值0-一般；1-较好；2-好；3-很好
             */
            private Integer ROIQP;
        }

        @Data
        public static class SVCParam {
            /**
             * 空域编码方式，取值0-基本层；1-1级增强（1个增强层）；2-2级增强（2个增强层）；3-3级增强（3个增强层）
             */
            private Integer SVCSpaceDomainMode;
            /**
             * 时域编码方式，取值0-基本层；1-1级增强；2-2级增强；3-3级增强（必选）
             */
            private Integer SVCTimeDomainMode;
            /**
             * SSVC增强层与基本层比例值，取值字符串，如4:3、2:1、4:1、6:1、8:1等具体比例值
             */
            private String SSVCRatioValue;
            /**
             * 空域编码能力，取值0-不支持；1-1级增强（1个增强层）；2-2级增强（2个增强层）；3-3级增强（3个增强层）
             */
            private Integer SVCSpaceSupportMode;
            /**
             * 时域编码能力，取值0：不支持；1-1级增强；2-2级增强；3-3级增强
             */
            private Integer SVCTimeSupportMode;
            /**
             * SSVC增强层与基本层比例能力，取值字符串，多个取值间用英文半角“/”分割，如4:3/2:1/4:1/6:1/8:1等具体比例值的一种或者多种
             */
            private String SSVCRatioSupportList;
        }

        @Data
        public static class SurveillanceParam {
            /**
             * 绝对时间信息开关，取值0-关闭；1-打开
             */
            private Integer timeFlag;
            /**
             * OSD信息开关，取值0-关闭；1-打开
             */
            private Integer OSDFlag;
            /**
             * 智能分析信息开关，取值0-关闭；1-打开
             */
            private Integer AIFlag;
            /**
             * 地理信息开关，取值0-关闭；1-打开
             */
            private Integer GISFlag;
        }

        @Data
        public static class AudioParam {
            /**
             * 声音识别特征参数开关，取值0-关闭；1-打开
             */
            private Integer audioRecognitionFlag;
        }
    }

    @Data
    public static class SVACDecodeConfig {
        /**
         * SVC参数
         */
        private SVCParam SVCParam;
        /**
         * 监控专用信息参数
         */
        private SurveillanceParam surveillanceParam;
        @Data
        public static class SVCParam {
            /**
             * 码流显示模式，取值0-基本层码流单独显示方式；1-基本层+1个增强层码流方式；2-基本层+2个增强层码流方式；3-基本层+3个增强层码流方式；
             */
            private Integer SVCSTMMode;
            /**
             * 空域编码能力，取值0-不支持；1-1级增强（1个增强层）；2-2级增强（2个增强层）；3-3级增强（3个增强层）
             */
            private Integer SVCSpaceSupportMode;
            /**
             * 时域编码能力，取值0-不支持；1-1级增强；2-2级增强；3-3级增强
             */
            private Integer SVCTimeSupportMode;
        }

        @Data
        public static class SurveillanceParam{
            /**
             * 绝对时间信息显示开关，取值0-关闭；1-打开
             */
            private Integer timeShowFlag;
            /**
             * OSD信息显示开关，取值0-关闭；1-打开
             */
            private Integer OSDShowFlag;
            /**
             * 智能分析信息显示开关，取值0-关闭；1-打开
             */
            private Integer AIShowFlag;
            /**
             * 地理信息开关，取值0-关闭；1-打开
             */
            private Integer GISShowFlag;
        }
    }

    @Data
    public static class VideoParamAttribute {
        /**
         * 视频流编号（必选），用于实时视音频点播时指定码流编号。0-主码流；1-子码流1；2-子码流2，以此类推
         */
        private Integer streamNumber;
        /**
         * 视频编码格式当前配置值(必选)
         */
        private String videoFormat;
        /**
         * 分辨率当前配置值(必选)
         */
        private String resolution;
        /**
         * 帧率当前配置值(必选)
         */
        private String frameRate;
        /**
         * 码率类型配置值(必选)
         */
        private String bitRateType;
        /**
         * 视频码率配置值(固定码率时必选)
         */
        private String videoBitRate;
    }

    @Data
    public static class VideoRecordPlan {
        /**
         * 是否启用时间计划录像配置：0-否，1-是
         */
        private Integer recordEnable;
        /**
         * 每周录像计划总天数
         */
        private Integer recordScheduleSumNum;
        /**
         * 一个星期的录像计划，可配置7天，对应周一至周日，每天最大支持8个时间段配置
         */
        private List<RecordSchedule> recordSchedule;
        @Data
        public static class RecordSchedule {
            /**
             * 周几（必选）取值1～7，表示周一到周日
             */
            private Integer weekDayNum;
            /**
             * 每天录像计划时间段总数
             */
            private Integer timeSegmentSumNum;
            /**
             * 录像时间段配置
             */
            private List<RecordScheduleTimeSegment> timeSegment;
        }

        @Data
        public static class RecordScheduleTimeSegment {
            /**
             * 开始时间：时,0～23
             */
            private Integer startHour;
            /**
             * 开始时间：分,0～59
             */
            private Integer startMin;
            /**
             * 开始时间：秒,0～59
             */
            private Integer startSec;
            /**
             * 结束时间：时,0～23
             */
            private Integer stopHour;
            /**
             * 结束时间：分,0～59
             */
            private Integer stopMin;
            /**
             * 结束时间：秒,0～59
             */
            private Integer stopSec;
        }
    }

    @Data
    public static class VideoAlarmRecord {
        /**
         * 是否启用报警录像配置：0-否，1-是
         */
        private Integer recordEnable;
        /**
         * 录像延时时间，报警时间点后的时间,单位“秒”
         */
        private Integer recordTime;
        /**
         * 预录时间：报警时间点前的时间,单位“秒”
         */
        private Integer preRecordTime;
        /**
         * 码流编号：0-主码流，1-子码流1，2-子码流2，以此类推
         */
        private Integer streamNumber;
    }

    @Data
    public static class PictureMask {
        private Integer on;
        private Integer sumNum;
        /**
         * 区域列表
         */
        private List<RegionListItem> regionList;
        @Data
        public static class RegionListItem {
            /**
             * 区域编号 1-4
             */
            private Integer seq;
            /**
             * 区域左上角、右下角坐标（lx,ly,rx,ry,单位像素）
             */
            private String point;
        }
    }

    @Data
    public static class  AlarmReport {
        /**
         * 移动侦测事件上报开关，取值0-关闭，1-打开
         */
        private Integer motionDetection;
        /**
         * 区域入侵事件上报开关，取值0-关闭，1-打开
         */
        private Integer fieldDetection;
    }

    @Data
    public static class OSDConfig {
        /**
         * 配置窗口长度像素值（必选）
         */
        private Integer length;
        /**
         * 配置窗口宽度像素值（必选）
         */
        private Integer width;
        /**
         * 时间X像素坐标（必选），以播放窗口左上角像素为原点，水平向右为正
         */
        private Integer timeX;
        /**
         * 时间Y像素坐标（必选），以播放窗口左上角像素为原点，竖直向下为正
         */
        private Integer timeY;
        /**
         * 显示时间开关（可选），0-关闭；1-打开（默认值）
         */
        private Integer timeEnable;
        /**
         * 时间显示类型（可选）：0-YYYY-MM-DD HH:MM:SS 1-YYYY年MM月DD日 HH:MM:SS
         */
        private Integer timeType;
        /**
         * 显示文字开关（可选）,0-关闭；1-打开
         */
        private Integer textEnable;
        /**
         * 显示文字总行书
         */
        private Integer sumNum;

        /**
         * 显示文字
         */
        @JacksonXmlElementWrapper(useWrapping = false)
        private List<Item> item;

        @Data
        public static class Item {
            /**
             * 文字内容
             */
            private String text;
            /**
             * 文字X坐标
             */
            private Integer x;
            /**
             * 文字Y坐标
             */
            private Integer y;
        }
    }

    @Data
    public static class SnapShotConfig {
        /**
         * 连拍张数，最多10张,当手动抓拍时，取值为1-
         */
        private Integer snapNum;
        /**
         * 单张抓拍间隔时间，单位：秒（必选），取值范围：最短1秒-
         */
        private Integer interval;
        /**
         * 抓拍图像上传路径
         */
        private String uploadURL;
        /**
         * 会话ID，由平台生成，用于关联抓拍的图像与平台请求（必选），SessionID由大小写英文字母、数字、短划线组成，长度不小于32字节，不大于128字节。
         */
        private String sessionID;
    }
}
