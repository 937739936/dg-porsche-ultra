package com.shdatalink.sip.server.gb28181.core.date;

import com.shdatalink.sip.server.gb28181.core.bean.constants.SipConstant;
import gov.nist.javax.sip.header.SIPDate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * GB28181 SIP 日期
 */
public class GbSipDate extends SIPDate {
    private final LocalDateTime localDateTime;

    public GbSipDate(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    @Override
    public StringBuilder encode(StringBuilder encoding) {
        return encoding.append(this.localDateTime.format(DateTimeFormatter.ofPattern(SipConstant.DATETIME_FORMAT_MS)));
    }
}
