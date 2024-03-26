package com.stephenshen.ssrpc.core.consumer;

import com.alibaba.fastjson.JSON;
import com.stephenshen.ssrpc.core.api.*;
import com.stephenshen.ssrpc.core.util.MethodUtils;
import com.stephenshen.ssrpc.core.util.TypeUtils;
import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *
 * </p>
 *
 * @author stephenshen
 * @version 1.0
 * @date 2024/3/18 07:30
 */
public class SSInvocationHandler implements InvocationHandler {

    static final MediaType JSON_TYPE = MediaType.get("application/json; charset=utf-8");

    Class<?> service;
    RpcContext context;
    List<String> providers;

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
        RpcResponse<Object> rpcResponse = post(rpcRequest, url);

        if (rpcResponse.isStatus()) {
            Object data = rpcResponse.getData();
            return TypeUtils.castMethodResult(method, data);
        } else {
            Exception ex = rpcResponse.getEx();
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }


    OkHttpClient client = new OkHttpClient.Builder()
        .connectionPool(new ConnectionPool(16, 60, TimeUnit.SECONDS))
        .readTimeout(1, TimeUnit.SECONDS)
        .writeTimeout(1, TimeUnit.SECONDS)
        .connectTimeout(1, TimeUnit.SECONDS)
        .build();

    private RpcResponse<Object> post(RpcRequest rpcRequest, String url) {
        String reqJson = JSON.toJSONString(rpcRequest);
        System.out.println("===> reqJson = " + reqJson);
        Request request = new Request.Builder()
            .url(url)
            .post(RequestBody.create(reqJson, JSON_TYPE))
            .build();
        try {
            String respJson = client.newCall(request).execute().body().string();
            System.out.println("===> respJson = " + respJson);
            RpcResponse<Object> rpcResponse = JSON.parseObject(respJson, RpcResponse.class);
            return rpcResponse;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
