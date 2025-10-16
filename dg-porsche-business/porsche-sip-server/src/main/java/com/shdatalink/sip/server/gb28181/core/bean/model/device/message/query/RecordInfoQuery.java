package com.shdatalink.sip.server.gb28181.core.bean.model.device.message.query;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.shdatalink.sip.server.gb28181.core.bean.model.device.message.query.enums.CmdType;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@SuperBuilder
@Data
public class RecordInfoQuery extends QueryMessage {
    @Builder.Default
    private CmdType cmdType = CmdType.RecordInfo;

    /**
     * 录像检索起始时间（必选）
     */
    @JacksonXmlProperty(localName = "StartTime")
    private LocalDateTime startTime;
    /**
     * 录像检索终止时间（必选）
     */
    @JacksonXmlProperty(localName = "EndTime")
    private LocalDateTime endTime;
    /**
     * 文件路径名 （可选）
     */
    @JacksonXmlProperty(localName = "FilePath")
    private String filePath;
    /**
     * 录像地址（可选 支持不完全查询）
     */
    @JacksonXmlProperty(localName = "Address")
    private String address;
    /**
     * 保密属性（可选）缺省为0；0-不涉密；1-涉密
     */
    @JacksonXmlProperty(localName = "Secrecy")
    private Integer secrecy;
    /**
     * 录像产生类型（可选）time或alarm或manual或all
     */
    @JacksonXmlProperty(localName = "Type")
    private String type;
    /**
     * 录像触发者ID（可选）
     */
    @JacksonXmlProperty(localName = "RecorderID")
    private String recorderId;
    /**
     * 录像模糊查询属性（可选）缺省为0；0-不进行模糊查询，此时根据SIP消息中To头域URI中的
     * ID值确定查询录像位置，若ID值为本域系统ID则进行中心历史记录检索，若为前端设备ID则进行前
     * 端设备历史记录检索；1-进行模糊查询，此时设备所在域应同时进行中心检索和前端检索并将结果
     * 统一返回。
     */
    @JacksonXmlProperty(localName = "IndistinctQuery")
    private Integer indistinctQuery;
    /**
     * 码流编号（可选）：0- 主码流；1- 子码流1；2-子码流2；以此类推
     */
    @JacksonXmlProperty(localName = "StreamNumber")
    private Integer streamNumber;
    /**
     * 报警方式条件（可选）取值0-全部；1-电话报警；2-设备报警；3-短信报警；4-GPS报警；
     * 5-视频报警；6-设备故障报警；7-其他报警；可以为直接组合如1/2为电话报警或设备报警
     */
    @JacksonXmlProperty(localName = "AlarmMethod")
    private String alarmMethod;
    /**
     * 报警类型（可选）。报警类型。报警方式为2时，不携带AlarmType为默认的报警设备报警，
     * 携带AlarmType取值及对应报警类型如下：1-视频丢失报警；2-设备防拆报警；3-存储设备磁盘满
     * 报警；4-设备高温报警；5-设备低温报警。报警方式为5时，取值如下：1-人工视频报警；2-运动
     * 目标检测报警；3-遗留物检测报警；4-物体移除检测报警；5-绊线检测报警；6-入侵检测报警；7-
     * 逆行检测报警；8-徘徊检测报警；9-流量统计报警；10-密度检测报警；11-视频异常检测报警；12-
     * 快速移动报警；13-图像遮挡报警。报警方式为6时，取值如下：1-存储设备磁盘故障报警；2-存储
     * 设备风扇故障报警
     */
    @JacksonXmlProperty(localName = "AlarmType")
    private String alarmType;
}
