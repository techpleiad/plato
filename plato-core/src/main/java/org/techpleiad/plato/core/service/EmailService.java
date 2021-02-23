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
    public void sendEmail(final String mailBody, final List<String> recipient, final String subject, final String from) {
        try {
            final MimeMessage message = javaMailSender.createMimeMessage();
            final MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setSubject(subject);
            helper.setTo(recipient.toArray(new String[recipient.size()]));
            helper.setText(mailBody, true);
            javaMailSender.send(message);
        } catch (final MessagingException e) {
            log.error("Unable to send email : {}", e);
        }
    }
}
