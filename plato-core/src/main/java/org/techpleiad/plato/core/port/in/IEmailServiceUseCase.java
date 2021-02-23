package org.techpleiad.plato.core.port.in;

import java.util.List;

public interface IEmailServiceUseCase {
    void sendEmail(String mailBody, List<String> recipient, String subject, String from);
}
