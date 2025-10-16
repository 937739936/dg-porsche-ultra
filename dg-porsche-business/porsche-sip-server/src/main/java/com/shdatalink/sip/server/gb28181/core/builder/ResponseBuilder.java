package com.shdatalink.sip.server.gb28181.core.builder;

import com.shdatalink.sip.server.gb28181.core.date.GbSipDate;
import com.shdatalink.sip.server.gb28181.core.header.impl.XGBVerHeaderImpl;
import com.shdatalink.sip.server.utils.SipUtil;
import gov.nist.javax.sip.header.Expires;
import gov.nist.javax.sip.header.SIPDateHeader;
import gov.nist.javax.sip.message.SIPRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.sip.RequestEvent;
import javax.sip.ServerTransaction;
import javax.sip.header.ExpiresHeader;
import javax.sip.header.Header;
import javax.sip.header.ViaHeader;
import javax.sip.message.Request;
import javax.sip.message.Response;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ResponseBuilder {

    private final Request request;
    private final ServerTransaction serverTransaction;
    private int statusCode;
    private Integer expires;
    private String phrase;
    private final List<Header> headers = new ArrayList<Header>();

    public static ResponseBuilder of(RequestEvent event) {
        return new ResponseBuilder(event);
    }

    protected ResponseBuilder(RequestEvent event) {
        this.request = event.getRequest();
        this.serverTransaction = event.getServerTransaction();
    }

    public ResponseBuilder addHeader(Header header) {
        this.headers.add(header);
        return this;
    }

    @SneakyThrows
    public ResponseBuilder addReasonPhrase(String phrase) {
        this.phrase = phrase;
        return this;
    }

    @SneakyThrows
    public ResponseBuilder addExpires(int expires) {
        this.expires = expires;
        return this;
    }

    public ResponseBuilder ok() {
        statusCode = Response.OK;
        return this;
    }

    public ResponseBuilder forbidden() {
        statusCode = Response.FORBIDDEN;
        return this;
    }

    public ResponseBuilder unauthorized() {
        statusCode = Response.UNAUTHORIZED;
        return this;
    }

    public ResponseBuilder buildRegisterOfResponse() {
        SIPRequest sipRequest = (SIPRequest) request;
        ExpiresHeader expires = sipRequest.getExpires();
        if (expires == null) {
            return unauthorized();
        }
        // 添加date头
        SIPDateHeader dateHeader = new SIPDateHeader();
        // GB28181 日期
        GbSipDate gbSipDate = new GbSipDate(LocalDateTime.now());
        dateHeader.setDate(gbSipDate);

        return ok().addHeader(dateHeader)
                .addHeader(sipRequest.getContactHeader())
                .addHeader(expires);
    }

    @SneakyThrows
    public void execute() {
        ViaHeader viaHeader = (ViaHeader) request.getHeader(ViaHeader.NAME);
        String transport = "UDP";
        if (viaHeader == null) {
            log.warn("[消息头缺失]： ViaHeader， 使用默认的UDP方式处理数据");
        } else {
            transport = viaHeader.getTransport();
        }

        Response response = SipUtil.getMessageFactory().createResponse(statusCode, request);
        if (this.expires != null) {
            Expires expires = new Expires();
            expires.setExpires(this.expires);
            response.setExpires(expires);
        }
        if (this.phrase != null) {
            response.setReasonPhrase(phrase);
        }
        for (Header header : this.headers) {
            response.addHeader(header);
        }
        response.addHeader(XGBVerHeaderImpl.GB28181_2016);;
        if (serverTransaction != null) {
            serverTransaction.sendResponse(response);
        } else {
            SipUtil.getSipProvider(transport).sendResponse(response);
        }
    }

}
