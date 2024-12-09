package com.fujica.config;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
@Data
public class ConstantConfig {
    @Value("${spring.env}")
    private String env;
}
