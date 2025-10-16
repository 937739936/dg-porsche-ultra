package com.shdatalink.sip.server.gb28181.core.bean.model.device.message.query.response;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.shdatalink.sip.server.gb28181.core.bean.model.base.DeviceBase;
import com.shdatalink.sip.server.gb28181.core.bean.model.device.message.MessageStating;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@JacksonXmlRootElement(localName = "Response")
public class RecordInfo extends DeviceBase implements MessageStating<RecordInfo> {
    /**
     * 设备/区域名称（必选）
     */
    @JacksonXmlProperty(localName = "Name")
    private String name;
    /**
     * 查询结果总数（必选）
     */
    @JacksonXmlProperty(localName = "SumNum")
    private Integer sumNum;

    /**
     * 文件目录项列表
     */
    @JacksonXmlProperty(localName = "RecordList")
    private List<RecordListItem> recordList;

    @Override
    public void append(RecordInfo recordInfo) {
        if (this.recordList == null) {
            this.recordList = new ArrayList<>();
        }
        if (recordInfo.getRecordList() != null) {
            this.recordList.addAll(recordInfo.getRecordList());
        }
    }

    @Data
    public static class RecordListItem {
        /**
         * 目标设备编码（必选）
         */
        @JacksonXmlProperty(localName = "DeviceID")
        private String deviceId;
        /**
         * 目标设备名称（必选）
         */
        @JacksonXmlProperty(localName = "Name")
        private String name;
        /**
         * 文件路径名（可选）
         */
        @JacksonXmlProperty(localName = "FilePath")
        private String filePath;
        /**
         * 录像地址（可选）
         */
        @JacksonXmlProperty(localName = "Address")
        private String address;
        /**
         * 录像开始时间（可选）
         */
        @JacksonXmlProperty(localName = "StartTime")
        private LocalDateTime startTime;
        /**
         * 录像结束时间（可选）
         */
        @JacksonXmlProperty(localName = "EndTime")
        private LocalDateTime endTime;
        /**
         * 保密属性（必选）缺省为0；0-不涉密，1-涉密
         */
        @JacksonXmlProperty(localName = "Secrecy")
        private Integer secrecy;
        /**
         * 录像产生类型（可选）time或alarm或manual
         */
        @JacksonXmlProperty(localName = "Type")
        private String type;
        /**
         * 录像触发者ID（可选）
         */
        @JacksonXmlProperty(localName = "RecorderID")
        private String recorderId;
        /**
         * 录像文件大小，单位：Byte（可选）
         */
        @JacksonXmlProperty(localName = "FileSize")
        private String fileSize;
        /**
         * 存储录像文件的设备/系统编码，（模糊查询时必选）
         */
        @JacksonXmlProperty(localName = "RecordLocation")
        private String recordLocation;
        /**
         * 码流类型：0-主码流；1-子码流1；2-子码流2；以此类推（可选）
         */
        @JacksonXmlProperty(localName = "StreamNumber")
        private Integer streamNumber;
    }
}
