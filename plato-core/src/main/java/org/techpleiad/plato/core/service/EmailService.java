package org.techpleiad.plato.core.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.techpleiad.plato.core.exceptions.EmailException;
import org.techpleiad.plato.core.port.in.IEmailServiceUseCase;
import org.techpleiad.plato.core.port.out.IEmailConfigurationPort;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.Properties;

@Service
@Slf4j
public class EmailService implements IEmailServiceUseCase {

    @Autowired
    private IEmailConfigurationPort emailConfiguration;

    @Override
    public void sendEmail(String mailBody, List<String> recipient, String subject) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", emailConfiguration.getHost());
        props.put("mail.smtp.port", emailConfiguration.getPort());

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(emailConfiguration.getUserName(), emailConfiguration.getPassword());
                    }
                });

        Message message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress("Plato"));
            message.setSubject("Testing Subject");
            message.setContent(mailBody, "text/html");
            for (String emailId:recipient) {
                message.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse(emailId));
                Transport.send(message);
            }
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new EmailException(e.getMessage());
        }
    }
}
