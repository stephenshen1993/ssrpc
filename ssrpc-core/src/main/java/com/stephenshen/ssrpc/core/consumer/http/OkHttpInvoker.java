package com.stephenshen.ssrpc.core.consumer.http;

import com.alibaba.fastjson.JSON;
import com.stephenshen.ssrpc.core.api.RpcRequest;
import com.stephenshen.ssrpc.core.api.RpcResponse;
import com.stephenshen.ssrpc.core.consumer.HttpInvoker;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.util.concurrent.TimeUnit;

/**
 * OKHttp invoker.
 * @author stephenshen
 * @version 1.0
 * @date 2024/3/27 08:00
 */
@Slf4j
public class OkHttpInvoker implements HttpInvoker {

    static final MediaType JSON_TYPE = MediaType.get("application/json; charset=utf-8");

    OkHttpClient client;

    public OkHttpInvoker(int timeout) {
        client = new OkHttpClient.Builder()
            .connectionPool(new ConnectionPool(16, 60, TimeUnit.SECONDS))
            .readTimeout(timeout, TimeUnit.MILLISECONDS)
            .writeTimeout(timeout, TimeUnit.MILLISECONDS)
            .connectTimeout(timeout, TimeUnit.MILLISECONDS)
            .retryOnConnectionFailure(true)
            .build();
    }

    @Override
    public RpcResponse<?> post(RpcRequest rpcRequest, String url) {
        String reqJson = JSON.toJSONString(rpcRequest);
        log.debug("===> reqJson = " + reqJson);
        Request request = new Request.Builder()
            .url(url)
            .post(RequestBody.create(reqJson, JSON_TYPE))
            .build();
        try {
            String respJson = client.newCall(request).execute().body().string();
            log.debug("===> respJson = " + respJson);
            RpcResponse<Object> rpcResponse = JSON.parseObject(respJson, RpcResponse.class);
            return rpcResponse;
        } catch (Exception e) {
            // e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public String post(String requestString, String url) {
        log.debug(" ===> post  url = {}, requestString = {}", requestString, url);
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(requestString, JSON_TYPE))
                .build();
        try {
            String respJson = client.newCall(request).execute().body().string();
            log.debug(" ===> respJson = " + respJson);
            return respJson;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String get(String url) {
        log.debug(" ===> get url = " + url);
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        try {
            String respJson = client.newCall(request).execute().body().string();
            log.debug(" ===> respJson = " + respJson);
            return respJson;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
