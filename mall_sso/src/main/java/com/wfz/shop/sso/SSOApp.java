package com.wfz.shop.sso;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@MapperScan(basePackages="com/wfz/shop/sso/mapper")
@SpringBootApplication
public class SSOApp {

    public static void main(String[] args) {
        SpringApplication.run(SSOApp.class,args);
    }
}