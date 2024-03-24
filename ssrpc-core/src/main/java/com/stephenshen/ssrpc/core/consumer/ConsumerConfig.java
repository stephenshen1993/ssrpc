package com.stephenshen.ssrpc.core.consumer;

import com.stephenshen.ssrpc.core.api.LoadBalancer;
import com.stephenshen.ssrpc.core.api.Router;
import com.stephenshen.ssrpc.core.cluster.RandomLoadBalancer;
import com.stephenshen.ssrpc.core.cluster.RoundRibonLoadBalancer;
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
 * @date 2024/3/18 07:14
 */
@Configuration
public class ConsumerConfig {

    @Bean
    public ConsumerBootstrap createConsumerBootstrap() {
        return new ConsumerBootstrap();
    }

    @Bean
    @Order(Integer.MIN_VALUE)
    public ApplicationRunner consumerBootstrap_runner(@Autowired ConsumerBootstrap consumerBootstrap) {
        return x -> {
            System.out.println("consumerBootstrap starting ...");
            consumerBootstrap.start();
            System.out.println("consumerBootstrap started ...");
        };
    }

    @Bean
    public LoadBalancer loadBalancer() {
        // return LoadBalancer.Default;
        return new RoundRibonLoadBalancer();
    }

    @Bean
    public Router router() {
        return Router.Default;
    }
}
