package com.stephenshen.ssrpc.core.provider;

import com.stephenshen.ssrpc.core.api.RegistryCenter;
import com.stephenshen.ssrpc.core.registry.zk.ZkRegistryCenter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * <p>
 *
 * </p>
 *
 * @author stephenshen
 * @version 1.0
 * @date 2024/3/13 06:47
 */
@Configuration
public class ProviderConfig {

    @Bean
    ProviderBootstrap providerBootstrap() {
        return new ProviderBootstrap();
    }

    @Bean
    ProviderInvoker providerInvoker(@Autowired ProviderBootstrap providerBootstrap) {
        return new ProviderInvoker(providerBootstrap);
    }

    @Bean
    @Order(Integer.MIN_VALUE)
    public ApplicationRunner providerBootstrap_runner(@Autowired ProviderBootstrap providerBootstrap) {
        return x -> {
            System.out.println("providerBootstrap starting ...");
            providerBootstrap.start();
            System.out.println("providerBootstrap started ...");
        };
    }

    @Bean //(initMethod = "start", destroyMethod = "stop")
    public RegistryCenter provider_rc(){
        return new ZkRegistryCenter();
    }
}
