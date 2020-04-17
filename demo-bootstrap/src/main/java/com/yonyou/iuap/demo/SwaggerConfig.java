package com.yonyou.iuap.demo;

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
 * Swagger 配置类,解决多basePackage下api扫描的问题
 *
 * @author  
 * @date 2019-6-28 13:19:00
 * @since v5.0.0
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket createCustomRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.yonyou.iuap.demo"))
                .paths(PathSelectors.any())
                .build().groupName("custom")
                .apiInfo(customApiInfo());
    }
    private ApiInfo customApiInfo(){
        return new ApiInfoBuilder()
                .title("API")
                .description("iuap5")
                .termsOfServiceUrl(" API terms of service")
                .version("1.0.0")
                .build();
    }

}
