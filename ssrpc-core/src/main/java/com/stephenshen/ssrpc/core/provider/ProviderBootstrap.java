package com.stephenshen.ssrpc.core.provider;

import com.stephenshen.ssrpc.core.annotation.SSProvider;
import com.stephenshen.ssrpc.core.api.RegistryCenter;
import com.stephenshen.ssrpc.core.config.AppConfigProperties;
import com.stephenshen.ssrpc.core.config.ProviderConfigProperties;
import com.stephenshen.ssrpc.core.meta.InstanceMeta;
import com.stephenshen.ssrpc.core.meta.ProviderMeta;
import com.stephenshen.ssrpc.core.meta.ServiceMeta;
import com.stephenshen.ssrpc.core.util.MethodUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Map;

/**
 * <p>
 * 服务提供者的启动类
 * </p>
 *
 * @author stephenshen
 * @version 1.0
 * @date 2024/3/12 07:36
 */
@Data
@Slf4j
public class ProviderBootstrap implements ApplicationContextAware {

    private ApplicationContext applicationContext;
    private RegistryCenter rc;

    private String port;
    private AppConfigProperties appConfigProperties;
    private ProviderConfigProperties providerConfigProperties;

    private MultiValueMap<String, ProviderMeta> skeleton = new LinkedMultiValueMap<>();
    private InstanceMeta instance;

    public ProviderBootstrap(String port, AppConfigProperties appConfigProperties, ProviderConfigProperties providerConfigProperties) {
        this.port = port;
        this.appConfigProperties = appConfigProperties;
        this.providerConfigProperties = providerConfigProperties;
    }

    @PostConstruct  // init-method
    public void init() {
        Map<String, Object> providers = applicationContext.getBeansWithAnnotation(SSProvider.class);
        rc = applicationContext.getBean(RegistryCenter.class);
        providers.keySet().forEach(System.out::println);
        providers.values().forEach(this::genInterface);
    }

    @SneakyThrows
    public void start() {
        String ip = InetAddress.getLocalHost().getHostAddress();
        instance = InstanceMeta.http(ip, Integer.valueOf(port)).addParams(providerConfigProperties.getMetas());
        rc.start();
        skeleton.keySet().forEach(this::registerService);
    }

    @PreDestroy
    public void stop() {
        skeleton.keySet().forEach(this::unregisterService);
        rc.stop();
    }

    private void registerService(String service) {
        ServiceMeta serviceMeta = ServiceMeta.builder()
                .app(appConfigProperties.getId()).namespace(appConfigProperties.getNamespace())
                .env(appConfigProperties.getEnv()).name(service).build();
        rc.register(serviceMeta, instance);
    }

    private void unregisterService(String service) {
        ServiceMeta serviceMeta = ServiceMeta.builder()
                .app(appConfigProperties.getId()).namespace(appConfigProperties.getNamespace())
                .env(appConfigProperties.getEnv()).name(service).build();
        rc.unregister(serviceMeta, instance);
    }

    private void genInterface(Object impl) {
        Arrays.stream(impl.getClass().getInterfaces()).forEach(
                service -> {
                    Arrays.stream(service.getMethods())
                            .filter(method -> !MethodUtils.checkLocalMethod(method))
                            .forEach(method -> createProvider(service, impl, method));
                });
    }

    private void createProvider(Class<?> service, Object impl, Method method) {
        ProviderMeta providerMeta = ProviderMeta.builder().method(method)
                .serviceImpl(impl).methodSign(MethodUtils.methodSign(method)).build();
        log.info("create a provider: " + providerMeta);
        skeleton.add(service.getCanonicalName(), providerMeta);
    }
}
