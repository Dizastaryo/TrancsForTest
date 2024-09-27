package com.example.trancs.config;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import javax.sql.DataSource;
@Configuration
@EnableScheduling
public class AppConfig {
    @Bean
    public DataSource dataSource() {
        return DataSourceBuilder.create()
                .url("jdbc:postgresql://localhost:5432/trancat")
                .username("postgres")
                .password("3721")
                .driverClassName("org.postgresql.Driver")
                .build();
    }
}

