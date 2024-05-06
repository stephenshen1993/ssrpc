package com.stephenshen.ssrpc.core.filter;

import com.stephenshen.ssrpc.core.api.Filter;
import com.stephenshen.ssrpc.core.api.RpcContext;
import com.stephenshen.ssrpc.core.api.RpcRequest;
import com.stephenshen.ssrpc.core.api.RpcResponse;

import java.util.Map;

/**
 * 处理上下文参数
 *
 * @author stephenshen
 * @date 2024/4/13 14:52:35
 */
public class ContextParameterFilter implements Filter {
    @Override
    public Object prefilter(RpcRequest request) {
        Map<String, String> params = RpcContext.ContextParameters.get();
        if (!params.isEmpty()) {
            request.getParams().putAll(params);
        }
        return null;
    }

    @Override
    public Object postfilter(RpcRequest request, RpcResponse response, Object result) {
         RpcContext.ContextParameters.get().clear();
        return null;
    }
}
