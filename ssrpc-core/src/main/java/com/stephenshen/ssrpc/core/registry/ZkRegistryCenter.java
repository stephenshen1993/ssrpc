package com.stephenshen.ssrpc.core.registry;

import com.stephenshen.ssrpc.core.api.RegistryCenter;

import java.util.List;

/**
 * @author stephenshen
 * @version 1.0
 * @date 2024/3/24 19:09
 */
public class ZkRegistryCenter implements RegistryCenter {
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
        return null;
    }
}
