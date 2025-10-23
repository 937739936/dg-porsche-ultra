package com.shdatalink.resource;

import com.shdatalink.framework.mail.utils.MailUtil;
import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

import java.util.List;
import java.util.Map;

/**
 * 邮件
 */
@Path("/mail")
public class MailResource {

    @Inject
    MailUtil mailUtil;

    @Inject
    @Location("email/hello")
    Template hello;

    @Path("/send")
    @GET
    public boolean send() {
        mailUtil.sendText(List.of("huyulong@shdatalink.com", "809204304@qq.com"), "测试邮件主题", "测试邮件内容");
        return true;
    }

    @Path("/send/template")
    @GET
    public boolean sendTemplate() {
        Map<String, Object> data = Map.of("name", "huyulong");
        mailUtil.sendTemplate(List.of("huyulong@shdatalink.com", "809204304@qq.com"), "测试邮件主题", hello, data);
        return true;
    }


}
