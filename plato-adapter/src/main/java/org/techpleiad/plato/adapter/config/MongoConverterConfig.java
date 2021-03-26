package org.techpleiad.plato.adapter.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.techpleiad.plato.adapter.converter.ValidationRuleReadConverter;
import org.techpleiad.plato.adapter.converter.ValidationRuleWriteConverter;

import java.util.ArrayList;
import java.util.List;


@Configuration
public class MongoConverterConfig {

    @Autowired
    ValidationRuleReadConverter validationRuleReadConverter;
    @Autowired
    ValidationRuleWriteConverter validationRuleWriteConverter;

    @Bean
    public MongoCustomConversions customConversions() {
        final List<Converter<?, ?>> converters = new ArrayList<>();
        converters.add(validationRuleReadConverter);
        converters.add(validationRuleWriteConverter);
        return new MongoCustomConversions(converters);
    }
}

