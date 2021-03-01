package org.techpleiad.plato.adapter.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.techpleiad.plato.core.port.out.IGetGitCredentialsPort;

import javax.annotation.PostConstruct;

@RefreshScope
@Configuration
@EnableConfigurationProperties
public class CredentialsConfig implements IGetGitCredentialsPort {

    @Value("${encryptionKey: default}")
    public String encryptionKey;

    @Value("${password: default}")
    public String password;

    @PostConstruct
    public void print() {
        System.out.println(encryptionKey);
        System.out.println(password);

    }

    @Override
    public String getPassword() {
        return encryptionKey;
    }
}
