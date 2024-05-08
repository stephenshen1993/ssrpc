package com.stephenshen.ssrpc.demo.provider;

import com.ctrip.framework.apollo.core.ApolloClientSystemConsts;
import com.stephenshen.ssrpc.core.test.TestZKServer;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SsrpcDemoProviderApplicationTests {

    static TestZKServer zkServer = new TestZKServer();
    //static ApolloTestingServer apollo = new ApolloTestingServer();

    @SneakyThrows
    @BeforeAll
    static void init() {
        System.out.println(" ====================================== ");
        System.out.println(" ====================================== ");
        System.out.println(" =============     ZK2182    ========== ");
        System.out.println(" ====================================== ");
        System.out.println(" ====================================== ");
        zkServer.start();
//        System.out.println(" ====================================== ");
//        System.out.println(" ====================================== ");
//        System.out.println(" ===========     mock apollo    ======= ");
//        System.out.println(" ====================================== ");
//        System.out.println(" ====================================== ");
//        apollo.start();
    }

    @Test
    void contextLoads() {
        System.out.println(" ===> SsrpcDemoProviderApplicationTests  .... ");
        System.out.println("....  ApolloClientSystemConsts.APOLLO_CONFIG_SERVICE  .....");
        System.out.println(System.getProperty(ApolloClientSystemConsts.APOLLO_CONFIG_SERVICE));
        System.out.println("....  ApolloClientSystemConsts.APOLLO_CONFIG_SERVICE  .....");
    }

    @AfterAll
    static void destroy() {
        System.out.println(" ===========     stop zookeeper server    ======= ");
        zkServer.stop();
//        System.out.println(" ===========     stop apollo mockserver   ======= ");
//        apollo.close();
        System.out.println(" ===========     destroy in after all     ======= ");
    }
}
