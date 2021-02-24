package org.techpleiad.plato.adapter.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.techpleiad.plato.adapter.converter.GitRepositoryReadConverter;
import org.techpleiad.plato.adapter.converter.GitRepositoryWriteConverter;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class MongoConfig{

    @Autowired
    GitRepositoryWriteConverter gitRepositoryWriteConverter;

    @Autowired
    GitRepositoryReadConverter gitRepositoryReadConverter;

    @Bean
    public MongoCustomConversions customConversions(){
        List<Converter<?,?>> converters = new ArrayList<>();
        converters.add(gitRepositoryReadConverter);
        converters.add(gitRepositoryWriteConverter);
        return new MongoCustomConversions(converters);
    }
}
