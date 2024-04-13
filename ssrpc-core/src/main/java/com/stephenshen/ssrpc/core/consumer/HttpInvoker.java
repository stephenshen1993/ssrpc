package com.stephenshen.ssrpc.core.consumer;

import com.stephenshen.ssrpc.core.api.RpcRequest;
import com.stephenshen.ssrpc.core.api.RpcResponse;

/**
 * Interface for http invoke
 *
 * @author stephenshen
 * @date 2024/3/27 07:59
 */
public interface HttpInvoker {

    RpcResponse<?> post(RpcRequest rpcRequest, String url);
}
