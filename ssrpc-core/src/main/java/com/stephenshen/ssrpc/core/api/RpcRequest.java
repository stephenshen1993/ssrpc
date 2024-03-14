package com.stephenshen.ssrpc.core.api;

import lombok.Data;

/**
 * <p>
 *
 * </p>
 *
 * @author stephenshen
 * @version 1.0
 * @date 2024/3/12 06:51
 */
@Data
public class RpcRequest {

    private String service; // 接口: com.stephenshen.ssrpc.demo.api.UserService
    private String method; // 方法: findById
    private Object[] args; // 参数: 100
}
