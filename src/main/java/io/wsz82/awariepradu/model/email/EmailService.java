package io.wsz82.awariepradu.model.email;

import io.wsz82.awariepradu.DisplayKey;
import io.wsz82.awariepradu.model.contact.Contact;
import io.wsz82.awariepradu.view.mail.SubscribeEmailDTO;
import io.wsz82.awariepradu.view.mail.UnsubscribeEmailDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Map;

public class EmailService {
    private final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    JavaMailSender mailSender;

    @Autowired
    SpringTemplateEngine templateEngine;

    public boolean sendSubscribeEmail(Contact contact, String linkToConfirm) {
        String message = makeSubscribeMessage(contact, linkToConfirm);
        return sendEmail(contact.getEmail(), DisplayKey.of("Contact.Subscribe.Email.Subject"), message);
    }

    public boolean sendUnsubscribeEmail(Contact contact, String linkToConfirm) {
        String message = makeUnsubscribeMessage(contact, linkToConfirm);
        return sendEmail(contact.getEmail(), DisplayKey.of("Contact.Unsubscribe.Email.Subject"), message);
    }

    private String makeUnsubscribeMessage(Contact contact, String linkToConfirm) {
        Context thymeleafContext = new Context();
        UnsubscribeEmailDTO subscribeEmailDTO = new UnsubscribeEmailDTO(contact, linkToConfirm);
        Map<String, Object> variables = subscribeEmailDTO.getVariables();
        thymeleafContext.setVariables(variables);
        return templateEngine.process("unsubscribe-email.html", thymeleafContext);
    }

    private String makeSubscribeMessage(Contact contact, String linkToConfirm) {
        Context thymeleafContext = new Context();
        String cityIndicator = contact.getIsCity() ? " - miasto" : "";
        SubscribeEmailDTO subscribeEmailDTO = new SubscribeEmailDTO(contact, cityIndicator, linkToConfirm);
        Map<String, Object> variables = subscribeEmailDTO.getVariables();
        thymeleafContext.setVariables(variables);
        return templateEngine.process("subscribe-email.html", thymeleafContext);
    }

    public void sendNotificationEmail(String email, String message) {
        sendEmail(email, "AwariePradu", message);
    }

    private boolean sendEmail(String email, String subject, String message) {
        MimeMessage mailMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mailMessage, "utf-8");
        try {
            helper.setTo(email);
            mailMessage.setFrom("noreply@awariepradu.com");
            helper.setFrom("noreply@awariepradu.com");

            helper.setSubject(subject);

            helper.setText(message, true);
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
        if (isTest()) {
            logger.debug("Tests: skipping sending email: {}", email);
            return true;
        }
        mailSender.send(mailMessage);
        logger.info("Sent email to: {}, subject: {}", email, subject);
        return true;
    }

    protected boolean isTest() {
        return false;
    }
}
