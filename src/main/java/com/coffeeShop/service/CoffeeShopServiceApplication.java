package com.coffeeShop.service;

import com.coffeeShop.service.interceptor.PerformanceInterceptor;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@SpringBootApplication
@EnableJpaRepositories
public class CoffeeShopServiceApplication implements WebMvcConfigurer {

    public static void main(String[] args) {
        SpringApplication.run(CoffeeShopServiceApplication.class, args);
    }

    PerformanceInterceptor performanceInterceptor() {
        return new PerformanceInterceptor();
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new PerformanceInterceptor())
                .addPathPatterns("/order/**");
    }

    @Bean
    public Hibernate5Module hibernate5Module() {
        return new Hibernate5Module();
    }



}
