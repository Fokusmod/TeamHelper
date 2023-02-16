package ru.geekbrains.WowVendorTeamHelper.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import ru.geekbrains.WowVendorTeamHelper.dto.EmailContext;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {

    private final JavaMailSender emailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String username;

    public void sendHtmlMessage(String userEmail, String subject, String template, Map<String, Object> properties) {

        EmailContext emailContext = EmailContext.createBuilder()
                .withEmail(userEmail)
                .withSubject(subject)
                .withTemplate(template)
                .withFrom(username)
                .withTo(userEmail)
                .withProperties(properties)
                .build();

        MimeMessage message = emailSender.createMimeMessage();
        try {
        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
        Context context = new Context();
        context.setVariables(emailContext.getProperties());
        helper.setFrom(emailContext.getFrom());
        helper.setTo(emailContext.getTo());
        helper.setSubject(emailContext.getSubject());
        String html = templateEngine.process(emailContext.getTemplate(), context);
        helper.setText(html, true);
        emailSender.send(message);
        log.info("Send email message");
        } catch (
                MessagingException mailException) {
            log.error("Error while sending out email..{}", mailException.getStackTrace());
        }

    }
}