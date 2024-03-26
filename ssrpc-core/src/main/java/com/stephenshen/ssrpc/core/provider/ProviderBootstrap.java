package com.stephenshen.ssrpc.core.provider;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.stephenshen.ssrpc.core.api.RegistryCenter;
import com.stephenshen.ssrpc.core.meta.ProviderMeta;
import com.stephenshen.ssrpc.core.util.MethodUtils;
import com.stephenshen.ssrpc.core.util.TypeUtils;
import jakarta.annotation.PreDestroy;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.stephenshen.ssrpc.core.annotation.SSProvider;
import com.stephenshen.ssrpc.core.api.RpcRequest;
import com.stephenshen.ssrpc.core.api.RpcResponse;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

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
public class ProviderBootstrap implements ApplicationContextAware {

    private ApplicationContext applicationContext;
    private RegistryCenter rc;

    private MultiValueMap<String, ProviderMeta> skeleton = new LinkedMultiValueMap<>();
    private String instance;

    @Value("${server.port}")
    private String port;

    @PostConstruct  // init-method
    public void init() {
        Map<String, Object> providers = applicationContext.getBeansWithAnnotation(SSProvider.class);
        rc = applicationContext.getBean(RegistryCenter.class);
        providers.forEach((k, v) -> System.out.println(k));
        providers.values().forEach(x -> genInterface(x));
    }

    @SneakyThrows
    public void start() {
        String ip = InetAddress.getLocalHost().getHostAddress();
        this.instance = ip + "_" + port;
        rc.start();
        skeleton.keySet().forEach(this::registerService);
    }

    @PreDestroy
    public void stop() {
        skeleton.keySet().forEach(this::unregisterService);
        rc.stop();
    }

    private void registerService(String service) {
        rc.register(service, instance);
    }

    private void unregisterService(String service) {
        rc.unregister(service, instance);
    }

    private void genInterface(Object x) {
        Class<?>[] interfaces = x.getClass().getInterfaces();
        for (Class<?> itfer : interfaces) {
            Method[] methods = itfer.getMethods();
            for (Method method : methods) {
                if (MethodUtils.checkLocalMethod(method)) {
                    continue;
                }
                createProvider(itfer, x, method);
            }
        }
    }

    private void createProvider(Class<?> itfer, Object x, Method method) {
        ProviderMeta meta = new ProviderMeta();
        meta.setMethod(method);
        meta.setServiceImpl(x);
        meta.setMethodSign(MethodUtils.methodSign(method));
        System.out.println("create a provider: " + meta);
        skeleton.add(itfer.getCanonicalName(), meta);
    }
}
