package com.stephenshen.ssrpc.core.config;

import com.stephenshen.ssrpc.core.api.*;
import com.stephenshen.ssrpc.core.cluster.GrayRouter;
import com.stephenshen.ssrpc.core.cluster.RoundRibonLoadBalancer;
import com.stephenshen.ssrpc.core.consumer.ConsumerBootstrap;
import com.stephenshen.ssrpc.core.filter.ContextParameterFilter;
import com.stephenshen.ssrpc.core.meta.InstanceMeta;
import com.stephenshen.ssrpc.core.registry.zk.ZkRegistryCenter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;

import java.util.List;

/**
 * config for consumer
 *
 * @author stephenshen
 * @date 2024/3/18 07:14
 */
@Slf4j
@Configuration
@Import({AppConfigProperties.class, ConsumerConfigProperties.class})
public class ConsumerConfig {

    @Autowired
    AppConfigProperties appConfigProperties;

    @Autowired
    ConsumerConfigProperties consumerConfigProperties;

    @Bean
    public ConsumerBootstrap createConsumerBootstrap() {
        return new ConsumerBootstrap();
    }

    @Bean
    @Order(Integer.MIN_VALUE + 1)
    public ApplicationRunner consumerBootstrap_runner(@Autowired ConsumerBootstrap consumerBootstrap) {
        return x -> {
            log.info("consumerBootstrap starting ...");
            // System.out.println("ssrpc.providers => " + String.join(",", servers));
            consumerBootstrap.start();
            log.info("consumerBootstrap started ...");
        };
    }

    @Bean
    public LoadBalancer<InstanceMeta> loadBalancer() {
        return new RoundRibonLoadBalancer<>();
    }

    @Bean
    public Router<InstanceMeta> router() {
        return new GrayRouter(consumerConfigProperties.getGrayRatio());
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    @ConditionalOnMissingBean
    public RegistryCenter consumer_rc(){
        return new ZkRegistryCenter();
    }

    @Bean
    public Filter defaultFilter() {
        return new ContextParameterFilter();
    }

    @Bean
    public RpcContext createContext(@Autowired Router router,
                                    @Autowired LoadBalancer loadBalancer,
                                    @Autowired List<Filter> filters) {
        RpcContext context = new RpcContext();
        context.setRouter(router);
        context.setLoadBalancer(loadBalancer);
        context.setFilters(filters);
        context.getParameters().put("app.id", appConfigProperties.getId());
        context.getParameters().put("app.namespace", appConfigProperties.getNamespace());
        context.getParameters().put("app.env", appConfigProperties.getEnv());
        context.getParameters().put("app.retries", String.valueOf(consumerConfigProperties.getRetries()));
        context.getParameters().put("app.timeout", String.valueOf(consumerConfigProperties.getTimeout()));
        context.getParameters().put("app.halfOpenInitialDelay", String.valueOf(consumerConfigProperties.getHalfOpenInitialDelay()));
        context.getParameters().put("app.faultLimit", String.valueOf(consumerConfigProperties.getFaultLimit()));
        context.getParameters().put("app.halfOpenDelay", String.valueOf(consumerConfigProperties.getHalfOpenDelay()));
        return context;
    }
}
