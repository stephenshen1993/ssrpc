package com.stephenshen.ssrpc.core.filter;

import com.stephenshen.ssrpc.core.api.Filter;
import com.stephenshen.ssrpc.core.api.RpcRequest;
import com.stephenshen.ssrpc.core.api.RpcResponse;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author stephenshen
 * @create 2024/04/03 07:15:31
 */
public class CacheFilter implements Filter {

    // 替换成guava cache，加容量和超时时间 todo 71
    static Map<String, Object> cache = new ConcurrentHashMap<>();

    @Override
    public Object prefilter(RpcRequest request) {
        return cache.get(request.toString());
    }

    @Override
    public Object postfilter(RpcRequest request, RpcResponse response, Object result) {
        cache.putIfAbsent(request.toString(), result);
        return result;
    }
}
