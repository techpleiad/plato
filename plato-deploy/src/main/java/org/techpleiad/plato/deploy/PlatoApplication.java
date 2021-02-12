package org.techpleiad.plato.deploy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableMongoRepositories(basePackages = {"org.techpleiad.plato.*"})
@EnableTransactionManagement
@ComponentScan(basePackages = {"org.techpleiad.plato.*"})
@EnableAsync
@SpringBootApplication
public class PlatoApplication {

    public static void main(final String[] args) {

        SpringApplication.run(PlatoApplication.class, args);
    }
}
