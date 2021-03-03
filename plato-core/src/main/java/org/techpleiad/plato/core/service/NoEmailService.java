package org.techpleiad.plato.core.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.techpleiad.plato.core.port.in.IEmailServiceUseCase;

import java.util.List;

@Service
@Slf4j
@ConditionalOnProperty(value = "plato.email.enabled", havingValue = "false", matchIfMissing = true)
public class NoEmailService implements IEmailServiceUseCase {

    @Override
    public void sendEmail(final String mailBody, final List<String> recipient, final String subject, final String from) {
        log.info("Send email : {} to : {}", mailBody, recipient);
    }
}
