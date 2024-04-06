package com.stephenshen.ssrpc.demo.consumer;

import com.stephenshen.ssrpc.demo.provider.SsrpcDemoProviderApplication;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootTest
class SsrpcDemoConsumerApplicationTests {

    static  ConfigurableApplicationContext context;

    @BeforeAll
    static void init() {
        context = SpringApplication.run(SsrpcDemoProviderApplication.class, "--server.port=8084", "loggging.level.com.stephenshen.ssrpc=debugg");
    }
    @Test
    void contextLoads() {
        System.out.println(" ===> aaa .... ");
    }

    @AfterAll
    static void destroy() {
        SpringApplication.exit(context, () -> 1);
    }
}
