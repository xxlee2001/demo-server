package com.yonyou.iuap.demo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.yonyou.cloud.auth.sdk.service.filter.SignVerifyFilter;
import com.yonyou.diwork.filter.DiworkRequestListener;
import com.yonyou.iuap.ucf.dao.mybatis.UcfMapperFactoryBean;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * 模块启动器
 *
 * @author leon
 * @date 2019/4/3
 * @since UCF1.0
 */
@ComponentScan(basePackages = {"com.yonyou"})
@MapperScan(basePackages = "com.yonyou", annotationClass = Repository.class, factoryBean = UcfMapperFactoryBean.class)
@EnableTransactionManagement
@SpringBootApplication
@EnableSwagger2
public class DemoApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    /**
     * 配置diwork会话过滤器
     * @author haojh@yonyou.com 
     */
    @Bean
    public FilterRegistrationBean RequestListener() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        //diwork接入
        registrationBean.setFilter(new DiworkRequestListener());
        registrationBean.addUrlPatterns("/*");
        registrationBean.addInitParameter("excludedPages","/bpmCallback/,/contract/contractwarning,/contract/checkCreateTime,/datainit/init");
        registrationBean.setOrder(1);
        return registrationBean;
    }
    
    @Value("${accesscenter_sign_url}")
    private String accessServer;
    
    /**
     * registAuthFilter:服务验签. <br/> 
     * @author haojh@yonyou.com 
     * @return
     */
    @Bean
    public FilterRegistrationBean registAuthFilter(){
    	FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        //diwork接入
        registrationBean.setFilter(new SignVerifyFilter());
        registrationBean.addUrlPatterns("/contract/contractwarning","/contract/checkCreateTime","/datainit/init");
        registrationBean.addInitParameter("accesscenter_sign_url", accessServer);
        registrationBean.addInitParameter("max_time_error", Long.toString(60L * 60 * 24));
        registrationBean.setOrder(2);
        return registrationBean;
    }
    
}
