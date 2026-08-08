package com.example.commonservice.services;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import freemarker.template.Configuration;
import java.util.Map;
import java.io.File;
import java.io.IOException;

@Service
@Slf4j
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private Configuration config;

    public void sendEmail(String to,String subject, String text, boolean isHtml, File attachment){
        try {
            MimeMessage message=javaMailSender.createMimeMessage();
            MimeMessageHelper helper=new MimeMessageHelper(message,true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text,isHtml);

            if(attachment !=null){
                FileSystemResource fileSystemResource=new FileSystemResource(attachment);
                helper.addAttachment(fileSystemResource.getFilename(),fileSystemResource);
            }
            javaMailSender.send(message);
            log.info("Email sent successfully to {}"+to);
        }catch (MessagingException e){
            log.error("Failed to send email to {}"+to,e);
        }
    }
    public void sendEmailWithTemplate(String to, String subject, String templateName, Map<String,Object> placeholder,File attachment){
        try{
            Template t=config.getTemplate(templateName);
            String html = FreeMarkerTemplateUtils.processTemplateIntoString(t,placeholder);
            MimeMessage message=javaMailSender.createMimeMessage();
            MimeMessageHelper helper=new MimeMessageHelper(message,true);
            helper.setTo(to);
            helper.setText(html,true);
            helper.setSubject(subject);

            if (attachment !=null){
                FileSystemResource fileSystemResource=new FileSystemResource(attachment);
                helper.addAttachment(fileSystemResource.getFilename(),fileSystemResource);
            }
            javaMailSender.send(message);
            log.info("Email sent successfully to "+to);
        }catch (MessagingException | IOException | TemplateException e){
            log.error("Failed to send email to "+to, e);
        }
    }
}
