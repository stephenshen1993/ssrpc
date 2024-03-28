package com.stephenshen.ssrpc.core.consumer;

import com.stephenshen.ssrpc.core.annotation.SSConsumer;
import com.stephenshen.ssrpc.core.api.LoadBalancer;
import com.stephenshen.ssrpc.core.api.RegistryCenter;
import com.stephenshen.ssrpc.core.api.Router;
import com.stephenshen.ssrpc.core.api.RpcContext;
import com.stephenshen.ssrpc.core.util.MethodUtils;
import lombok.Data;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
public class ConsumerBootstrap implements ApplicationContextAware, EnvironmentAware {

    private ApplicationContext applicationContext;
    private Environment environment;

    private Map<String, Object> stub = new HashMap<>();

    public void start() {

        Router router = applicationContext.getBean(Router.class);
        LoadBalancer loadBalancer = applicationContext.getBean(LoadBalancer.class);
        RegistryCenter rc = applicationContext.getBean(RegistryCenter.class);

        RpcContext context = new RpcContext();
        context.setRouter(router);
        context.setLoadBalancer(loadBalancer);

        String[] names = applicationContext.getBeanDefinitionNames();
        for (String name : names) {
            Object bean = applicationContext.getBean(name);

            List<Field> fields = MethodUtils.findAnnotationField(bean.getClass(), SSConsumer.class);

            fields.stream().forEach(field -> {
                System.out.println("===> " + field.getName());
                try {
                    Class<?> service = field.getType();
                    String serviceName = service.getCanonicalName();
                    Object consumer = stub.get(serviceName);
                    if (consumer == null) {
                        consumer = createFromRegistry(service, context, rc);
                        // consumer = createConsumer(service, context, List.of(providers));
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
        String serviceName = service.getCanonicalName();
        List<String> providers = mapUrls(rc.fetchAll(serviceName));
        System.out.println(" ===> map to provider: ");
        providers.forEach(System.out::println);

        rc.subscribe(serviceName, event -> {
            providers.clear();
            providers.addAll(mapUrls(event.getData()));
        });

        return createConsumer(service, context, providers);
    }

    private List<String> mapUrls(List<String> nodes) {
        return nodes.stream()
            .map(x -> "http://" + x.replace("_", ":")).collect(Collectors.toList());
    }

    private Object createConsumer(Class<?> service, RpcContext context, List<String> providers) {
        return Proxy.newProxyInstance(service.getClassLoader(), new Class[]{service}, new SSInvocationHandler(service, context, providers));
    }
}
