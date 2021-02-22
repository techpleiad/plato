package org.techpleiad.plato.core.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.techpleiad.plato.core.port.in.IEmailServiceUseCase;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.List;


@Service
@Slf4j
public class EmailService implements IEmailServiceUseCase {

    @Autowired
    private JavaMailSender javaMailSender;

    @Override
    public void sendEmail(String mailBody, List<String> recipient, String subject) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom("Plato");
            helper.setSubject(subject);
            helper.setTo(recipient.toArray(new String[0]));
            helper.setText(mailBody, true);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            log.info("Exception in Email Service");
        }
    }
}
