package com.shdatalink.sip.server.gb28181.core.parser;

import com.shdatalink.sip.server.gb28181.core.sdp.ssrc.FormatField;
import com.shdatalink.sip.server.gb28181.core.sdp.ssrc.SsrcField;
import com.shdatalink.sip.server.gb28181.core.sdp.ssrc.parser.FormatFieldParser;
import com.shdatalink.sip.server.gb28181.core.sdp.ssrc.parser.SsrcFieldParser;
import gov.nist.javax.sdp.parser.Lexer;
import gov.nist.javax.sdp.parser.ParserFactory;
import gov.nist.javax.sdp.parser.SDPParser;

import java.text.ParseException;

public class GB28181DescriptionParserFactory {
    public static SDPParser createParser(String field) throws ParseException {
        String fieldName = Lexer.getFieldName(field);
        if(fieldName.equalsIgnoreCase(SsrcField.SSRC_FIELD_NAME)){
            return new SsrcFieldParser(field);
        }
        if(fieldName.equalsIgnoreCase(FormatField.FORMAT_FIELD_NAME)){
            return new FormatFieldParser(field);
        }
        return ParserFactory.createParser(field);
    }
}
