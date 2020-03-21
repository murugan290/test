package com.rabobank.customer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


/**
 * @author - Murugan Rajendran
 *
 */

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    private static final Set<String> DEFAULT_PRODUCES = new HashSet<>( Arrays.asList("application/json"));
    private static final Set<String> DEFAULT_CONSUMES = new HashSet<>(Arrays.asList("application/json","multipart/form-data"));

    // Bean - Docket
    @Bean
    public Docket api(){
        return new Docket( DocumentationType.SWAGGER_2)
                .select().apis( RequestHandlerSelectors.basePackage("com.rabobank.customer.controller"))
                .paths( PathSelectors.any())
                .build().apiInfo(metaData())
                .consumes(DEFAULT_CONSUMES)
                .produces(DEFAULT_PRODUCES)
                .useDefaultResponseMessages(false);
    }

    private ApiInfo metaData() {
        return new ApiInfoBuilder()
                .title("Spring Boot RESTful API")
                .description("RESTful API for customer statement validation")
                .version("version 1.0")
                .contact(new Contact("Murugan Rajendran", "",
                        "murugan290@gmail.com")).build();
    }
}
