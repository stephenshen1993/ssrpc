package com.stephenshen.ssrpc.core.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * response data for RPC call.
 *
 * @author stephenshen
 * @version 1.0
 * @date 2024/3/12 06:51
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RpcResponse<T> {

    private boolean status; // 状态: true
    private T data; // new User()
    private RpcException ex;
}
