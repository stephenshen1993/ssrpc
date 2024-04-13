package com.stephenshen.ssrpc.core.consumer;

import com.stephenshen.ssrpc.core.api.*;
import com.stephenshen.ssrpc.core.consumer.http.OkHttpInvoker;
import com.stephenshen.ssrpc.core.governance.SlidingTimeWindow;
import com.stephenshen.ssrpc.core.meta.InstanceMeta;
import com.stephenshen.ssrpc.core.util.MethodUtils;
import com.stephenshen.ssrpc.core.util.TypeUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.*;
import java.net.SocketTimeoutException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 消费者消费端动态代理
 * </p>
 *
 * @author stephenshen
 * @version 1.0
 * @date 2024/3/18 07:30
 */
@Slf4j
public class SSInvocationHandler implements InvocationHandler {

    Class<?> service;
    RpcContext context;
    final List<InstanceMeta> providers;

    final List<InstanceMeta> isolatedProviders = new ArrayList<>();

    final List<InstanceMeta> halfOpenProviders = new ArrayList<>();

    final Map<String, SlidingTimeWindow> windows = new HashMap<>();

    HttpInvoker httpInvoker;

    ScheduledExecutorService executor;

    public SSInvocationHandler(Class<?> service, RpcContext context, List<InstanceMeta> providers) {
        this.service = service;
        this.context = context;
        this.providers = providers;
        int timeout = Integer.parseInt(context.getParameters().getOrDefault("app.timeout", "1000"));
        this.httpInvoker = new OkHttpInvoker(timeout);
        this.executor = Executors.newScheduledThreadPool(1);
        int halfOpenInitialDelay = Integer.parseInt(context.getParameters()
                .getOrDefault("app.halfOpenInitialDelay", "10000"));
        int halfOpenDelay = Integer.parseInt(context.getParameters()
                .getOrDefault("app.halfOpenDelay", "60000"));
        this.executor.scheduleWithFixedDelay(this::halfOpen, halfOpenInitialDelay, halfOpenDelay, TimeUnit.MILLISECONDS);
    }

    private void halfOpen() {
        log.debug(" ===> half opem isolatedProviders: " + isolatedProviders);
        halfOpenProviders.clear();
        halfOpenProviders.addAll(isolatedProviders);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 本地方法不处理
        if (MethodUtils.checkLocalMethod(method.getName())) {
            return null;
        }

        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setService(service.getCanonicalName());
        rpcRequest.setMethodSign(MethodUtils.methodSign(method));
        rpcRequest.setArgs(args);

        int retries = Integer.parseInt(context.getParameters().getOrDefault("app.retries", "1"));
        int faultLimit = Integer.parseInt(context.getParameters().getOrDefault("app.faultLimit", "10"));

        while (retries -- > 0) {
            log.debug(" ===> retries: " + retries);
            try {
                for (Filter filter : this.context.getFilters()) {
                    Object preResult = filter.prefilter(rpcRequest);
                    if (preResult != null) {
                        log.debug(filter.getClass().getName() + " ==> prefilter: " + preResult);
                        return preResult;
                    }
                }

                InstanceMeta instance;
                synchronized (halfOpenProviders) {
                    if (halfOpenProviders.isEmpty()) {
                        List<InstanceMeta> instances = context.getRouter().route(providers);
                        instance = context.getLoadBalancer().choose(instances);
                        log.debug("loadBalancer.choose(instances) ==> " + instance);
                    } else {
                        instance = halfOpenProviders.remove(0);
                        log.debug(" check alive instance ==> {}", instance);
                    }
                }

                RpcResponse<?> rpcResponse;
                Object result;

                String url = instance.toUrl();
                try {
                    rpcResponse = httpInvoker.post(rpcRequest, url);
                    result = castReturnResult(method, rpcResponse);
                } catch (Exception e) {
                    // 故障的规则统计和隔离
                    // 每一次异常，记录一次，统计30s的异常
                    synchronized (windows) {
                        SlidingTimeWindow window = windows.computeIfAbsent(url, k -> new SlidingTimeWindow());
                        window.record(System.currentTimeMillis());
                        log.debug("instance {} in window with {}", url, window.getSum());
                        // 发生了10次，就做故障隔离
                        if (window.getSum() >= faultLimit) {
                            isolate(instance);
                        }
                    }

                    throw e;
                }

                synchronized (providers) {
                    if (!providers.contains(instance)) {
                        isolatedProviders.remove(instance);
                        providers.add(instance);
                        log.debug("instance {} is recovered, isolatedProviders={}, providers={}",
                                instance, isolatedProviders, providers);
                    }
                }

                // 这里拿到的可能不是最终值，需要再设计一下
                for (Filter filter : this.context.getFilters()) {
                    Object filterResult = filter.postfilter(rpcRequest, rpcResponse, result);
                    if (filterResult != null) {
                        return filterResult;
                    }
                }
                return result;
            } catch (Exception ex) {
                if (!(ex.getCause() instanceof SocketTimeoutException)) {
                    throw ex;
                }
            }
        }
        return null;
    }

    private void isolate(InstanceMeta instance) {
        log.debug(" ===> isolate insctance " + instance);
        providers.remove(instance);
        log.debug(" ===> providers " + providers);
        isolatedProviders.add(instance);
        log.debug(" ===> isolatedProviders " + isolatedProviders);
    }

    private static Object castReturnResult(Method method, RpcResponse<?> rpcResponse) {
        if (rpcResponse.isStatus()) {
            return TypeUtils.castMethodResult(method, rpcResponse.getData());
        } else {
            Exception exception = rpcResponse.getEx();
            if (exception instanceof RpcException ex) {
                throw ex;
            } else {
                throw new RpcException(exception, RpcException.UnknownEx);
            }
        }
    }
}
