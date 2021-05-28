package org.techpleiad.plato.adapter.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.techpleiad.plato.core.port.out.ISuppressPropertyPersistencePort;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RefreshScope
@Slf4j
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = SuppressedPropertiesConfig.PREFIX)
@Data
public class SuppressedPropertiesConfig implements ISuppressPropertyPersistencePort {

    public static final String PREFIX = "plato.properties.suppressed";

    public Map<String, Map<String, List<String>>> values;

    public Map<String, List<String>> getSuppressedProperties(final String serviceName) {
        return values
                .getOrDefault(serviceName, Collections.emptyMap());
    }
}
