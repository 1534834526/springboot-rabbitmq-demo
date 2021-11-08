package com.send.demo.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author RickyCharles
 * @apiNote Swagger2配置类
 */
@Configuration
@EnableSwagger2
@ConditionalOnExpression("${swagger.enable:true}")
public class Swagger2Config {
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.send.demo.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("后端接口文档")
                .description("rabbitmq发送的接口文档")
                //.termsOfServiceUrl("http://wl.sinovac.com.cn/kxsw/admin-sys/login_toAdmin")
                //.contact()
                .version("1.0")
                .build();
    }
}
