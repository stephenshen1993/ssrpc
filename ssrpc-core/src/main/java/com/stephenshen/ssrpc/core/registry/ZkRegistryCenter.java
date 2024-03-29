package com.stephenshen.ssrpc.core.registry;

import com.stephenshen.ssrpc.core.api.RegistryCenter;
import lombok.SneakyThrows;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.List;

/**
 * @author stephenshen
 * @version 1.0
 * @date 2024/3/24 19:09
 */
public class ZkRegistryCenter implements RegistryCenter {

    private CuratorFramework client = null;

    @Override
    public void start() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.builder()
            .connectString("localhost:2181")
            .namespace("ssrpc")
            .retryPolicy(retryPolicy)
            .build();
        System.out.println(" ===> zk client started.");
        client.start();
    }

    @Override
    public void stop() {
        System.out.println(" ===> zk client stoped.");
        client.close();
    }

    @Override
    public void register(String service, String instance) {
        String servicePath = "/" + service;
        try {
            // 创建服务的持久化节点
            if (client.checkExists().forPath(servicePath) == null) {
                client.create().withMode(CreateMode.PERSISTENT).forPath(servicePath, "service".getBytes());
            }
            // 创建实例的临时性节点
            String instancePath = servicePath + "/" + instance;
            System.out.println(" ===> register to zk: " + instancePath);
            client.create().withMode(CreateMode.EPHEMERAL).forPath(instancePath, "provider".getBytes());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void unregister(String service, String instance) {
        String servicePath = "/" + service;
        try {
            // 判断服务是否存在
            if (client.checkExists().forPath(servicePath) == null) {
                return;
            }
            // 删除实例节点
            String instancePath = servicePath + "/" + instance;
            System.out.println(" ===> unregister from zk: " + instancePath);
            client.delete().quietly().forPath(instancePath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> fetchAll(String service) {
        String servicePath = "/" + service;
        try {
            // 获取所有子节点
            List<String> modes = client.getChildren().forPath(servicePath);
            System.out.println(" ===> fetchAll from zk: " + servicePath);
            modes.forEach(System.out::println);
            return modes;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    @Override
    public void subscribe(String service, ChangedListener listener) {
        final TreeCache cache = TreeCache.newBuilder(client, "/" + service)
            .setCacheData(true).setMaxDepth(2).build();
        cache.getListenable().addListener(
            (curator, event) -> {
                // 有任何节点变动这里会执行
                System.out.println("zk subscribe event: " + event);
                List<String> nodes = fetchAll(service);
                listener.fire(new Event(nodes));
            }
        );
        cache.start();
    }
}
