package com.almaszy.inzynierka.services;

import com.almaszy.inzynierka.AppProperties;
import com.almaszy.inzynierka.entities.User;
import freemarker.template.Configuration;
import freemarker.template.Template;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.util.HashMap;
import java.util.Map;

@Service
public class MailServiceImpl implements MailService {

    private final Logger logger = LogManager.getLogger(getClass());
    private static final String SUPPORT_EMAIL = "support.email";
    public static final String LINE_BREAK = "<br>";
    public final static String BASE_URL = "baseUrl";

    @Autowired
    private MessageService messageService;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private Environment env;

    @Autowired
    Configuration freemarkerConfiguration;

    @Autowired
    AppProperties appProperties;

    @Async
    @Override
    public void sendVerificationToken(String token, User user)
    {
        //final String confirmationUrl = appProperties.getClient().getBaseUrl() + "verify?token=" + token;
        final String confirmationUrl = "http://localhost:4200/token/verify?token=" + token;
        final String message = messageService.getMessage("message.mail.verification");
        sendHtmlEmail("Registration Confirmation", message + LINE_BREAK + confirmationUrl, user);
    }

    private String getFreeMarkerTemplateContent(Map<String, Object> model)
    {
        StringBuffer content = new StringBuffer();
        try
        {
            Template template = freemarkerConfiguration.getTemplate("verification.ftl");
            content.append(FreeMarkerTemplateUtils.processTemplateIntoString(template, model));
            return content.toString();
        }
        catch (Exception e)
        {
            System.out.println("Exception occured while processing fmtemplate:" + e.getMessage());
        }
        return "";
    }

    private void sendHtmlEmail(String subject, String msg, User user)
    {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("name", user.getUsername());
        model.put("msg", msg);
        model.put("title", subject);
        model.put(BASE_URL, appProperties.getClient().getBaseUrl());
        try
        {
            sendHtmlMail(env.getProperty(SUPPORT_EMAIL), user.getEmail(), subject, getFreeMarkerTemplateContent(model));
        }
        catch (MessagingException e)
        {
            logger.error("Failed to send mail", e);
        }
    }

    public void sendHtmlMail(String from, String to, String subject, String body) throws MessagingException
    {
        MimeMessage mail = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mail, true, "UTF-8");
        helper.setFrom(from);
        if (to.contains(","))
        {
            helper.setTo(to.split(","));
        }
        else
        {
            helper.setTo(to);
        }
        helper.setSubject(subject);
        helper.setText(body, true);
        mailSender.send(mail);
        logger.info("Sent mail: {0}", subject);
    }
}
