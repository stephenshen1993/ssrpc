package com.stephenshen.ssrpc.core.consumer;

import com.stephenshen.ssrpc.core.annotation.SSConsumer;
import com.stephenshen.ssrpc.core.api.LoadBalancer;
import com.stephenshen.ssrpc.core.api.RegistryCenter;
import com.stephenshen.ssrpc.core.api.Router;
import com.stephenshen.ssrpc.core.api.RpcContext;
import com.stephenshen.ssrpc.core.meta.InstanceMeta;
import com.stephenshen.ssrpc.core.meta.ServiceMeta;
import com.stephenshen.ssrpc.core.util.MethodUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 消费者启动类
 * </p>
 *
 * @author stephenshen
 * @version 1.0
 * @date 2024/3/18 07:12
 */
@Data
@Slf4j
public class ConsumerBootstrap implements ApplicationContextAware, EnvironmentAware {

    private ApplicationContext applicationContext;
    private Environment environment;

    @Value("${app.id}")
    private String app;

    @Value("${app.namespace}")
    private String namespace;

    @Value("${app.env}")
    private String env;

    private Map<String, Object> stub = new HashMap<>();

    public void start() {

        Router<InstanceMeta> router = applicationContext.getBean(Router.class);
        LoadBalancer<InstanceMeta> loadBalancer = applicationContext.getBean(LoadBalancer.class);
        RegistryCenter rc = applicationContext.getBean(RegistryCenter.class);

        RpcContext context = new RpcContext();
        context.setRouter(router);
        context.setLoadBalancer(loadBalancer);

        String[] names = applicationContext.getBeanDefinitionNames();
        for (String name : names) {
            Object bean = applicationContext.getBean(name);

            List<Field> fields = MethodUtils.findAnnotationField(bean.getClass(), SSConsumer.class);

            fields.stream().forEach(field -> {
                log.info("===> " + field.getName());
                try {
                    Class<?> service = field.getType();
                    String serviceName = service.getCanonicalName();
                    Object consumer = stub.get(serviceName);
                    if (consumer == null) {
                        consumer = createFromRegistry(service, context, rc);
                        stub.put(serviceName, consumer);
                    }
                    field.setAccessible(true);
                    field.set(bean, consumer);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    private Object createFromRegistry(Class<?> service, RpcContext context, RegistryCenter rc) {
        ServiceMeta serviceMeta = ServiceMeta.builder()
                .app(app).namespace(namespace).env(env).name(service.getCanonicalName()).build();
        List<InstanceMeta> providers = rc.fetchAll(serviceMeta);
        log.info(" ===> map to provider: ");
        providers.forEach(System.out::println);

        rc.subscribe(serviceMeta, event -> {
            providers.clear();
            providers.addAll(event.getData());
        });

        return createConsumer(service, context, providers);
    }

    private Object createConsumer(Class<?> service, RpcContext context, List<InstanceMeta> providers) {
        return Proxy.newProxyInstance(service.getClassLoader(), new Class[]{service}, new SSInvocationHandler(service, context, providers));
    }
}
