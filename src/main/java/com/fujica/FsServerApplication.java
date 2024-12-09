package com.fujica;

import com.fujica.util.SpringContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ApplicationContext;

@EnableDiscoveryClient
@SpringBootApplication
@Slf4j
class FsServerApplication {


    public static void main(String[] args) {
        log.info("===================================== start application =====================================");
        ApplicationContext ctx = SpringApplication.run(FsServerApplication.class, args);
        try {
            SpringContextUtils.setMyApplicationContext(ctx);
        } catch(Exception ex) {
            log.info("SpringContextUtils error");
        }
        String[] activeProfiles = ctx.getEnvironment().getActiveProfiles();
        for (String profile : activeProfiles) {
            log.info("Spring Boot 使用profile为:{}", profile);
        }
    }


}
