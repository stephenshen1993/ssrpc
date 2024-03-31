package com.stephenshen.ssrpc.core.api;

import com.stephenshen.ssrpc.core.meta.InstanceMeta;
import com.stephenshen.ssrpc.core.meta.ServiceMeta;
import com.stephenshen.ssrpc.core.registry.ChangedListener;

import java.util.List;

/**
 * @author stephenshen
 * @version 1.0
 * @date 2024/3/24 18:45
 */
public interface RegistryCenter {

    void start();
    void stop();

    // provider侧
    void register(ServiceMeta service, InstanceMeta instance);
    void unregister(ServiceMeta service, InstanceMeta instance);

    // consumer侧
    List<InstanceMeta> fetchAll(ServiceMeta service);

    void subscribe(ServiceMeta service, ChangedListener listener);
    // void heartbeat();

    class StaticRegistryCenter implements RegistryCenter {

        List<InstanceMeta> providers;

        public StaticRegistryCenter(List<InstanceMeta> providers) {
            this.providers = providers;
        }

        @Override
        public void start() {

        }

        @Override
        public void stop() {

        }

        @Override
        public void register(ServiceMeta service, InstanceMeta instance) {

        }

        @Override
        public void unregister(ServiceMeta service, InstanceMeta instance) {

        }

        @Override
        public List<InstanceMeta> fetchAll(ServiceMeta service) {
            return providers;
        }

        @Override
        public void subscribe(ServiceMeta service, ChangedListener listener) {

        }
    }
}