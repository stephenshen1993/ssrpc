package com.stephenshen.ssrpc.core.api;

/**
 * 过滤器
 *
 * @author stephenshen
 * @date 2024/3/24 16:29
 */
public interface Filter {

    Object prefilter(RpcRequest request);

    Object postfilter(RpcRequest request, RpcResponse response, Object result);

    Filter Default = new Filter() {
        @Override
        public Object prefilter(RpcRequest request) {
            return null;
        }

        @Override
        public Object postfilter(RpcRequest request, RpcResponse response, Object result) {
            return null;
        }
    };
}