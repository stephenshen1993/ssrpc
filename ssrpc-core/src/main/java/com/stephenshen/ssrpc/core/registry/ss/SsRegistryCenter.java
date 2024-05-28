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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * implementation of ss registry center
 * @author stephenshen
 * @date 2024/5/25 14:54:22
 */
@Slf4j
public class SsRegistryCenter implements RegistryCenter {

    private static final String REG_PATH = "/reg";
    private static final String UNREG_PATH = "/unreg";
    private static final String FIND_ALL_PATH = "/findAll";
    private static final String VERSION_PATH = "/version";
    private static final String RENEWS_PATH = "/renews";

    @Value("${ssregistry.servers}")
    private String servers;
    Map<String, Long> VERSIONS = new HashMap<>();
    MultiValueMap<InstanceMeta, ServiceMeta> RENEWS = new LinkedMultiValueMap<>();
    SsHealthChecker healthChecker = new SsHealthChecker();


    @Override
    public void start() {
      log.info(" ====>>> [SSRegistry] : start with server : {}", servers);
      healthChecker.start();
      providerCheck();
    }

    public void providerCheck() {
        healthChecker.providerCheck(() -> {
            RENEWS.keySet().forEach(instance -> {
                Long timestamp = HttpInvoker.httpPost(JSON.toJSONString(instance), renewPath(RENEWS.get(instance)), Long.class);
                log.info(" ====>>> [SSRegistry] : renew instance {} at {}", instance, timestamp);
            });
        });
    }

    @Override
    public void stop() {
        log.info(" ====>>> [SSRegistry] : stop with server : {}", servers);
        healthChecker.stop();
    }

    @Override
    public void register(ServiceMeta service, InstanceMeta instance) {
        log.info(" ====>>> [SSRegistry] : register instance {} for {}", instance, service);
        HttpInvoker.httpPost(JSON.toJSONString(instance), regPath(service), InstanceMeta.class);
        log.info(" ====>>> [SSRegistry] : registered {}", instance);
        RENEWS.add(instance, service);
    }

    @Override
    public void unregister(ServiceMeta service, InstanceMeta instance) {
        log.info(" ====>>> [SSRegistry] : unregister instance {} for {}", instance, service);
        HttpInvoker.httpPost(JSON.toJSONString(instance), unregPath(service), InstanceMeta.class);
        log.info(" ====>>> [SSRegistry] : unregistered {}", instance);
        RENEWS.remove(instance, service);
    }

    @Override
    public List<InstanceMeta> fetchAll(ServiceMeta service) {
        log.info(" ====>>> [SSRegistry] : find all instance for {}", service);
        List<InstanceMeta> instances = HttpInvoker.httpGet(findAllPath(service), new TypeReference<>() {});
        log.info(" ====>>> [SSRegistry] : findAll = {}", instances);
        return instances;
    }

    @Override
    public void subscribe(ServiceMeta service, ChangedListener listener) {
        healthChecker.consumerCheck( () -> {
            Long version = VERSIONS.getOrDefault(service.toPath(), -1L);
            Long newVersion = HttpInvoker.httpGet(versionPath(service), Long.class);
            log.info(" ====>>> [SSRegistry] : version = {}, newVersion = {}", version, newVersion);
            if (newVersion > version) {
                List<InstanceMeta> instances = fetchAll(service);
                listener.fire(new Event(instances));
                VERSIONS.put(service.toPath(), newVersion);
            }
        });
    }

    private String regPath(ServiceMeta service) {
        return path(REG_PATH, service);
    }
    private String unregPath(ServiceMeta service) {
        return path(UNREG_PATH, service);
    }
    private String findAllPath(ServiceMeta service) {
        return path(FIND_ALL_PATH, service);
    }
    private String versionPath(ServiceMeta service) {
        return path(VERSION_PATH, service);
    }
    private String path(String context, ServiceMeta service) {
        return servers + context + "?service=" + service.toPath();
    }

    private String renewPath(List<ServiceMeta> services) {
        return path(RENEWS_PATH, services);
    }

    private String path(String context, List<ServiceMeta> serviceList) {
        String services = serviceList.stream().map(ServiceMeta::toPath).collect(Collectors.joining(","));
        log.info(" ====>>> [SSRegistry] : renew instance for {}", services);
        return servers + context + "?services=" + services;
    }
}
