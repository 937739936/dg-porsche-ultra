package com.shdatalink.sip.server.gb28181.core.bean.model.device.message.notify;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.shdatalink.sip.server.gb28181.core.bean.model.device.message.query.enums.CmdType;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
public class AlarmSubscribe extends SubscribeMessage {
    @Builder.Default
    private CmdType cmdType = CmdType.Alarm;
    /**
     * 报警起始级别（可选），0为全部；1-一级警情；2-二级警情；3-三级警情；4-四级警情
     */
    @JacksonXmlProperty(localName = "StartAlarmPriority")
    private String startAlarmPriority;
    /**
     * 报警终止级别（可选），0为全部，1-一级警情；2-二级警情；3-三级警情；4-四级警情
     */
    @JacksonXmlProperty(localName = "EndAlarmPriority")
    private String endAlarmPriority;
    /**
     * 报警方式条件（可选），取值0-全部；1-电话报警；2-设备报警；3-短信报警；4-GPS报警；5-视频报警；6-设备故障报警；7-其他报警；可以为直接组合如1/2为电话报警或设备报警
     */
    @JacksonXmlProperty(localName = "AlarmMethod")
    private String alarmMethod;
    /**
     * 报警类型 （可选） 。 报警方式为2时， 不携带AlarmType为默认的报警设备报警， 携带AlarmType 取值及对应报警类型如下：1-视频丢失报警；2-设备防拆报警；3-存储设备磁盘满报警；4-设备高温报警；5-设备低温报警。报警方式为5时，取值如下：1-人工视频报警；2-运动目标检测报警；3-遗留物检测报警；4-物体移除检测报警；5-绊线检测报警；6-入侵检测报警；7-逆行检测报警；8-徘徊检测报警；9-流量统计报警；10-密度检测报警；11-视频异常检测报警；12-快速移动报警；13-图像遮挡报警。报警方式为6时，取值如下：1-存储设备磁盘故障报警；2-存储设备风扇故障报警
     */
    @JacksonXmlProperty(localName = "AlarmType")
    private String alarmType;
    /**
     * 报警发生起止时间（可选）
     */
    @JacksonXmlProperty(localName = "StartAlarmTime")
    private LocalDateTime startAlarmTime;
    @JacksonXmlProperty(localName = "EndAlarmTime")
    private LocalDateTime endAlarmTime;
}
