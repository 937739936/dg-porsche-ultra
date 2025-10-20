package com.shdatalink.framework.mail.utils;

import com.shdatalink.framework.common.utils.ArgUtil;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * Quarkus邮件工具类
 */
@Slf4j
@ApplicationScoped
public class MailUtil {

    @Inject
    Mailer mailer;


    /**
     * 发送简单文本邮件
     *
     * @param to      收件人邮箱
     * @param subject 邮件主题
     * @param content 邮件内容
     */
    public void sendText(List<String> to, String subject, String content) {
        ArgUtil.judgeEmpty(to, "收件人邮箱不能为空");
        ArgUtil.judgeBlank(subject, "邮件主题不能为空");

        Mail mail = Mail.withText(to.getFirst(), subject, content);
        if (to.size() > 1) {
            to.stream().skip(1).forEach(mail::addTo);
        }
        mailer.send(mail);

    }

    /**
     * 发送HTML邮件
     *
     * @param to      收件人邮箱
     * @param subject 邮件主题
     * @param html    HTML内容
     */
    public void sendHtml(List<String> to, String subject, String html) {
        ArgUtil.judgeEmpty(to, "收件人邮箱不能为空");
        ArgUtil.judgeBlank(subject, "邮件主题不能为空");
        ArgUtil.judgeBlank(html, "HTML内容不能为空");

        Mail mail = Mail.withHtml(to.getFirst(), subject, html);
        if (to.size() > 1) {
            to.stream().skip(1).forEach(mail::addTo);
        }
        mailer.send(mail);
    }

    /**
     * 发送模板邮件
     *
     * @param to       收件人邮箱
     * @param subject  邮件主题
     * @param template Qute模板
     * @param data     模板数据
     */
    public void sendTemplate(List<String> to, String subject, Template template, Map<String, Object> data) {
        ArgUtil.judgeNull(template, "模板不能为空");

        TemplateInstance instance = template.data(data);
        String htmlContent = instance.render();
        sendHtml(to, subject, htmlContent);
    }

}
