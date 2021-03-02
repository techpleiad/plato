package org.techpleiad.plato.core.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.techpleiad.plato.core.port.in.IEmailServiceUseCase;

import java.util.List;

@Service
@Slf4j
@Profile("local")
public class NoEmailClass implements IEmailServiceUseCase {

    @Override
    public void sendEmail(final String mailBody, final List<String> recipient, final String subject, final String from) {
        log.info("Send email : {} to : {}", mailBody, recipient);
    }
}
