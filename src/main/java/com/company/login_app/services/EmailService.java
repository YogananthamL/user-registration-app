package com.company.login_app.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final SpringTemplateEngine templateEngine;
    private final JavaMailSender mailSender;

    @Async
    public void send(
            String to, String username,
            String templateName, String url
    )throws MessagingException {
        if(!StringUtils.hasLength(templateName)){
            templateName="confirm-email";
        }

        MimeMessage mimeMessage= mailSender.createMimeMessage();
        MimeMessageHelper helper= new MimeMessageHelper(mimeMessage,MimeMessageHelper.MULTIPART_MODE_MIXED, StandardCharsets.UTF_8.name());

        Map<String, Object> properties= new HashMap<>();
        properties.put("username", username);
        properties.put("confirmationUrl", url);

        Context context= new Context();
        context.setVariables(properties);

        helper.setFrom("yogananthaml76@gmail.com");
        helper.setTo(to);
        helper.setSubject("Account Activation Mail");

        String template= templateEngine.process(templateName, context);
        helper.setText(template,true);
        mailSender.send(mimeMessage);
    }
}
