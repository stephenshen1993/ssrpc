package com.stephenshen.ssrpc.core.test;

import lombok.SneakyThrows;
import org.apache.curator.test.TestingServer;

/**
 * Testing zk server
 *
 * @author stephenshen
 * @create 2024/04/07 07:45:22
 */
public class TestZKServer {

    TestingServer testingServer;

    @SneakyThrows
    public void start() {
        testingServer = new TestingServer(2182);
        System.out.println("TestingZookeeperServer starting ...");
        testingServer.start();
        System.out.println("TestingZookeeperServer started");
    }

    @SneakyThrows
    public void stop() {
        System.out.println("TestingZookeeperServer stopping ...");
        testingServer.stop();
        System.out.println("TestingZookeeperServer stopped");
    }
}
