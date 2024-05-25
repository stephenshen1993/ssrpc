package com.stephenshen.ssrpc.core.registry.ss;

import com.stephenshen.ssrpc.core.api.RegistryCenter;
import com.stephenshen.ssrpc.core.meta.InstanceMeta;
import com.stephenshen.ssrpc.core.meta.ServiceMeta;
import com.stephenshen.ssrpc.core.registry.ChangedListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

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
    }

    @Override
    public void stop() {
        log.info(" ====>>> [SSRegistry] : stop with server : {}", servers);
    }

    @Override
    public void register(ServiceMeta service, InstanceMeta instance) {
        log.info(" ====>>> [SSRegistry] : registry instance {} for {}", instance, service);

    }

    @Override
    public void unregister(ServiceMeta service, InstanceMeta instance) {

    }

    @Override
    public List<InstanceMeta> fetchAll(ServiceMeta service) {
        return List.of();
    }

    @Override
    public void subscribe(ServiceMeta service, ChangedListener listener) {

    }
}
