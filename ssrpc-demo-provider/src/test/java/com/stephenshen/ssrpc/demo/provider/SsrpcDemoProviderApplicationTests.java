package com.stephenshen.ssrpc.demo.provider;

import com.stephenshen.ssrpc.core.test.TestZKServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SsrpcDemoProviderApplicationTests {

    static TestZKServer zkServer = new TestZKServer();

    @BeforeAll
    static void init() {
        zkServer.start();
    }

    @Test
    void contextLoads() {
        System.out.println(" ===> SsrpcDemoProviderApplicationTests  .... ");
    }

    @AfterAll
    static void destroy() {
        zkServer.stop();
    }
}
