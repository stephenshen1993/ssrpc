package com.stephenshen.ssrpc.core.consumer;

import com.stephenshen.ssrpc.core.api.RpcRequest;
import com.stephenshen.ssrpc.core.api.RpcResponse;

/**
 * @author stephenshen
 * @version 1.0
 * @date 2024/3/27 07:59
 */
public interface HttpInvoker {

    RpcResponse<?> post(RpcRequest rpcRequest, String url);
}
