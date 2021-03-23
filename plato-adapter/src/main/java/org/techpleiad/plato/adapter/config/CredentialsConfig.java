package org.techpleiad.plato.adapter.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.techpleiad.plato.core.port.out.IGetGitCredentialsPort;

@RefreshScope
@Configuration
@EnableConfigurationProperties
@Data
@ConfigurationProperties(prefix = CredentialsConfig.PREFIX)
public class CredentialsConfig implements IGetGitCredentialsPort {

    public static final String PREFIX = "git.default";

    private String password;
    private String username;

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }
}
