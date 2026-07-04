package com.docshare.requestservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("com.docshare.common.entity")
@EnableJpaRepositories("com.docshare.common.repository")
public class RequestServiceApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(RequestServiceApplication.class);
        if (System.getenv("AWS_LAMBDA_FUNCTION_NAME") != null) {
            // Forces Spring Boot to bypass physical Tomcat and act as a Serverless Context
            application.setWebApplicationType(WebApplicationType.NONE);
        }
        application.run(args);
    }

}
