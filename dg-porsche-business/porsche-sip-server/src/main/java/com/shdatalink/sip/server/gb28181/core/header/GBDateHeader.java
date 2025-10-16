package com.shdatalink.sip.server.gb28181.core.header;

import com.shdatalink.sip.server.gb28181.core.bean.constants.SipConstant;
import gov.nist.javax.sip.header.SIPHeader;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.sip.header.Header;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@EqualsAndHashCode(callSuper = true)
public class GBDateHeader extends SIPHeader implements Header {
    public static final String NAME = "Date";
    @Getter
    @Setter
    private LocalDateTime localDateTime;

    public GBDateHeader() {
        super(NAME);
    }

    public GBDateHeader(LocalDateTime localDateTime) {
        super(NAME);
        this.localDateTime = localDateTime;
    }

    @Override
    protected StringBuilder encodeBody(StringBuilder buffer) {
        return buffer.append(localDateTime.format(DateTimeFormatter.ofPattern(SipConstant.DATETIME_FORMAT_MS)));
    }
}
