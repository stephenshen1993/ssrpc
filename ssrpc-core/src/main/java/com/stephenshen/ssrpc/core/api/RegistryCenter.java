package com.stephenshen.ssrpc.core.api;

import java.util.List;

/**
 * @author stephenshen
 * @version 1.0
 * @date 2024/3/24 18:45
 */
public interface RegistryCenter {

    void start();
    void stop();

    // provider
    void register(String service, String instance);
    void unregister(String service, String instance);

    // consumer
    List<String> fetchAll(String service);
    // void subscribe();

    class StaticRegistryCenter implements RegistryCenter {

        List<String> providers;

        public StaticRegistryCenter(List<String> providers) {
            this.providers = providers;
        }

        @Override
        public void start() {

        }

        @Override
        public void stop() {

        }

        @Override
        public void register(String service, String instance) {

        }

        @Override
        public void unregister(String service, String instance) {

        }

        @Override
        public List<String> fetchAll(String service) {
            return providers;
        }
    }
}