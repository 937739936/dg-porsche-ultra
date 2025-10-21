package com.shdatalink.sip.server.gb28181.core.bean.model.device.message.query.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.shdatalink.sip.server.gb28181.core.bean.constants.SipConstant;
import com.shdatalink.sip.server.gb28181.core.bean.model.base.DeviceBase;
import com.shdatalink.sip.server.gb28181.core.bean.model.device.message.MessageStating;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 设备目录响应数据
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JacksonXmlRootElement(localName = "Response")
@RegisterForReflection
public class DeviceCatalog extends DeviceBase implements MessageStating<DeviceCatalog> {

    private Integer sumNum;

    private DeviceCatalogList deviceList;

    @Override
    public void append(DeviceCatalog data) {
        this.deviceList.deviceList.addAll(data.getDeviceList().getDeviceList());
        this.deviceList.setNum(this.deviceList.deviceList.size());
    }

    @Data
    @JacksonXmlRootElement(localName = "DeviceList")
    public static class DeviceCatalogList {

        @JacksonXmlProperty(isAttribute = true)
        private Integer num = 0;

        @JacksonXmlProperty(localName = "Item")
        @JacksonXmlElementWrapper(useWrapping = false)
        private List<DeviceCatalogItem> deviceList = new ArrayList<>();


        @Data
        @JacksonXmlRootElement(localName = "Item")
        public static class DeviceCatalogItem {

            /**
             * 设备/区域/系统编码(必选)
             */
            @JacksonXmlProperty(localName = "DeviceID")
            private String deviceId;

            /**
             * 设备/区域/系统名称(必选)
             */
            private String name;

            /**
             * 当为设备时,设备厂商(必选)
             */
            private String manufacturer;

            /**
             * 当为设备时,设备型号(必选)
             */
            private String model;

            /**
             * 当为设备时,设备归属(必选)
             */
            private String owner;

            /**
             * 行政区域(必选)
             */
            @JacksonXmlProperty(localName = "CivilCode")
            private String civilCode;

            /**
             * 警区(可选)
             */
            private String block;

            /**
             * 当为设备时,安装地址(必选)
             */
            private String address;

            /**
             * 当为设备时,是否有子设备(必选)1有, 0没有
             */
            private Integer parental = 0;

            /**
             * 父设备/区域/系统ID(必选)
             */
            @JacksonXmlProperty(localName = "ParentID")
            private String parentId;

            /**
             * 信令安全模式(可选)缺省为0; 0:不采用;2:S/MIME 签名方式;3:S/ MIME加密签名同时采用方式;4:数字摘要方式
             */
            private Integer safetyWay;

            /**
             * 注册方式(必选)缺省为1;1:符合IETF RFC3261标准的认证注册模 式;2:基于口令的双向认证注册模式;3:基于数字证书的双向认证注册模式
             */
            private Integer registerWay;

            /**
             * 证书序列号(有证书的设备必选)
             */
            private String certNum;

            /**
             * 证书有效标识(有证书的设备必选)缺省为0;证书有效标识:0:无效 1: 有效
             */
            private Integer certifiable;

            /**
             * 无效原因码(有证书且证书无效的设备必选)
             */
            private Integer errCode;

            /**
             * 证书终止有效期(有证书的设备必选)
             */
            @JsonFormat(pattern = SipConstant.DATETIME_FORMAT, timezone = SipConstant.TIME_ZONE)
            private Date endTime;

            /**
             * 保密属性(必选)缺省为0;0:不涉密,1:涉密
             */
            private Integer secrecy;

            /**
             * 设备/区域/系统IP地址(可选)
             */
            @JacksonXmlProperty(localName = "IPAddress")
            private String ipAddress;

            /**
             * 设备/区域/系统端口(可选)
             */
            private Integer port;

            /**
             * 设备口令(可选)
             */
            private String password;

            /**
             * 设备状态(必选)
             */
            private String status;

            /**
             * 经度(可选)
             */
            private String longitude;

            /**
             * 纬度(可选)
             */
            private String latitude;

            /**
             * 在线状态 (调用设备状态自行查询)
             */
            private String online;

            /**
             * 设备录制状态 (调用设备状态自行查询)
             */
            private String record;

            private ItemInfo info;
        }

        @Data
        public static class ItemInfo {
            // 摄像机结构类型， 标识摄像机类型： 1-球机； 2-半球； 3-固定枪机； 4-遥控枪机；5-遥控半球；6-多目设备的全景/拼接通道；7-多目设备的分割通道
            @JacksonXmlProperty(localName = "PTZType")
            private Integer PTZType;
            // 摄像机光电成像类型。1-可见光成像；2-热成像；3-雷达成像；4-X光成像；5-深度光场成像；9-其他。可多值，用英文半角“/”分割
            private String photoelectricImagingType;
        }
    }


}
