package org.techpleiad.plato.adapter.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.techpleiad.plato.core.port.out.IAlteredPropertyPersistencePort;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RefreshScope
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = AlteredPropertiesConfig.PREFIX)
@Data
public class AlteredPropertiesConfig implements IAlteredPropertyPersistencePort {

    public static final String PREFIX = "plato.properties.altered";

    private Map<String, List<String>> values;

    public List<String> getAlteredProperties(final String serviceName) {
        return values
                .getOrDefault(serviceName, Collections.emptyList());
    }
}
