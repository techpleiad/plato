package org.techpleiad.plato.core.port.out;

public interface IEmailConfigurationPort {
    String getHost();
    String getUserName();
    String getPassword();
    int getPort();
}
