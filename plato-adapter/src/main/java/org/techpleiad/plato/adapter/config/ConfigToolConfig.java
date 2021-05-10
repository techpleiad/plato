package org.techpleiad.plato.adapter.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.techpleiad.plato.core.port.out.IGetWorkingDirectoryPort;

@RefreshScope
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = ConfigToolConfig.PREFIX)
@Data
public class ConfigToolConfig implements IGetWorkingDirectoryPort {

    public static final String PREFIX = "plato.config";

    private String workingDirectory;
    private String branchConsistencyEmailSubject;
    private String profileConsistencyEmailSubject;
    private String emailFrom;
    private String customValidationEmailSubject;


    @Override
    public String getRootWorkingDirectory() {
        return workingDirectory;
    }
}
