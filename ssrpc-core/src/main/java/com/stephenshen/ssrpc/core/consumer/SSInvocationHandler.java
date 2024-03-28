package com.stephenshen.ssrpc.core.consumer;

import com.stephenshen.ssrpc.core.api.*;
import com.stephenshen.ssrpc.core.consumer.http.OkHttpInvoker;
import com.stephenshen.ssrpc.core.util.MethodUtils;
import com.stephenshen.ssrpc.core.util.TypeUtils;

import java.lang.reflect.*;
import java.util.*;

/**
 * <p>
 * 消费者消费端动态代理
 * </p>
 *
 * @author stephenshen
 * @version 1.0
 * @date 2024/3/18 07:30
 */
public class SSInvocationHandler implements InvocationHandler {

    Class<?> service;
    RpcContext context;
    List<String> providers;

    HttpInvoker httpInvoker = new OkHttpInvoker();

    public SSInvocationHandler(Class<?> service, RpcContext context, List<String> providers) {
        this.service = service;
        this.context = context;
        this.providers = providers;
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

        List<String> urls = context.getRouter().route(providers);
        String url = (String)context.getLoadBalancer().choose(urls);
        System.out.println("loadBalancer.choose(urls) ==> " + url);
        RpcResponse<?> rpcResponse = httpInvoker.post(rpcRequest, url);

        if (rpcResponse.isStatus()) {
            Object data = rpcResponse.getData();
            return TypeUtils.castMethodResult(method, data);
        } else {
            Exception ex = rpcResponse.getEx();
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }
}
