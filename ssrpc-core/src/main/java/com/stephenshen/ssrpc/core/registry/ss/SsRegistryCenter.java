package com.stephenshen.ssrpc.core.registry.ss;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.stephenshen.ssrpc.core.api.RegistryCenter;
import com.stephenshen.ssrpc.core.consumer.HttpInvoker;
import com.stephenshen.ssrpc.core.meta.InstanceMeta;
import com.stephenshen.ssrpc.core.meta.ServiceMeta;
import com.stephenshen.ssrpc.core.registry.ChangedListener;
import com.stephenshen.ssrpc.core.registry.Event;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * implementation of ss registry center
 * @author stephenshen
 * @date 2024/5/25 14:54:22
 */
@Slf4j
public class SsRegistryCenter implements RegistryCenter {

    @Value("${ssregistry.servers}")
    private String servers;

    @Override
    public void start() {
      log.info(" ====>>> [SSRegistry] : start with server : {}", servers);
      executor = Executors.newScheduledThreadPool(1);
    }

    @Override
    public void stop() {
        log.info(" ====>>> [SSRegistry] : stop with server : {}", servers);
        executor.shutdown();
        try {
            executor.awaitTermination(1000, TimeUnit.MILLISECONDS);
            if (executor.isTerminated()) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            // ignore
        }
    }

    @Override
    public void register(ServiceMeta service, InstanceMeta instance) {
        log.info(" ====>>> [SSRegistry] : register instance {} for {}", instance, service);
        HttpInvoker.httpPost(JSON.toJSONString(instance), servers + "/reg?service=" + service.toPath(), InstanceMeta.class);
        log.info(" ====>>> [SSRegistry] : registered {}", instance);
    }

    @Override
    public void unregister(ServiceMeta service, InstanceMeta instance) {
        log.info(" ====>>> [SSRegistry] : unregister instance {} for {}", instance, service);
        HttpInvoker.httpPost(JSON.toJSONString(instance), servers + "/unreg?service=" + service.toPath(), InstanceMeta.class);
        log.info(" ====>>> [SSRegistry] : unregistered {}", instance);
    }

    @Override
    public List<InstanceMeta> fetchAll(ServiceMeta service) {
        log.info(" ====>>> [SSRegistry] : find all instance for {}", service);
        List<InstanceMeta> instances = HttpInvoker.httpGet(servers + "/findAll?service=" + service.toPath(), new TypeReference<>() {});
        log.info(" ====>>> [SSRegistry] : findAll = {}", instances);
        return instances;
    }

    Map<String, Long> VERSIONS = new HashMap<>();
    ScheduledExecutorService executor;

    @Override
    public void subscribe(ServiceMeta service, ChangedListener listener) {
        executor.scheduleWithFixedDelay( () -> {
            Long version = VERSIONS.getOrDefault(service.toPath(), -1L);
            Long newVersion = HttpInvoker.httpGet(servers + "/version?service=" + service.toPath(), Long.class);
            log.info(" ====>>> [SSRegistry] : version = {}, newVersion = {}", version, newVersion);
            if (newVersion > version) {
                List<InstanceMeta> instances = fetchAll(service);
                listener.fire(new Event(instances));
                VERSIONS.put(service.toPath(), newVersion);
            }
        }, 1000, 5000, TimeUnit.MILLISECONDS);
    }
}
