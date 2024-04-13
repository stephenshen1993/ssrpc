package com.stephenshen.ssrpc.demo.consumer;

import com.stephenshen.ssrpc.core.test.TestZKServer;
import com.stephenshen.ssrpc.demo.provider.SsrpcDemoProviderApplication;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootTest
class SsrpcDemoConsumerApplicationTests {

    static ApplicationContext context1;
    static ApplicationContext context2;

    static TestZKServer zkServer = new TestZKServer();

    @BeforeAll
    static void init() {
        System.out.println(" ====================================== ");
        System.out.println(" ====================================== ");
        System.out.println(" =============     ZK2182    ========== ");
        System.out.println(" ====================================== ");
        System.out.println(" ====================================== ");
        zkServer.start();
        System.out.println(" ====================================== ");
        System.out.println(" ====================================== ");
        System.out.println(" =============      P8094    ========== ");
        System.out.println(" ====================================== ");
        System.out.println(" ====================================== ");
        context1 = SpringApplication.run(SsrpcDemoProviderApplication.class,
                "--server.port=8094", "--ssrpc.zkServer=localhost:2182",
                "--loggging.level.com.stephenshen.ssrpc=info", "--app.metas={dc:'bj',gray:'false',unit:'B001'}");
        System.out.println(" ====================================== ");
        System.out.println(" ====================================== ");
        System.out.println(" =============      P8095    ========== ");
        System.out.println(" ====================================== ");
        System.out.println(" ====================================== ");
        context2 = SpringApplication.run(SsrpcDemoProviderApplication.class,
                "--server.port=8095", "--ssrpc.zkServer=localhost:2182",
                "--loggging.level.com.stephenshen.ssrpc=info", "--app.metas={dc:'bj',gray:'false',unit:'B001'}");
    }
    @Test
    void contextLoads() {
        System.out.println(" ===> SsrpcDemoConsumerApplicationTests  .... ");
    }

    @AfterAll
    static void destroy() {
        SpringApplication.exit(context1, () -> 1);
        SpringApplication.exit(context2, () -> 1);
        zkServer.stop();
    }
}
