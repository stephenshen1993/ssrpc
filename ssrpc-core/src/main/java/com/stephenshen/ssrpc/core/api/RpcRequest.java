package com.stephenshen.ssrpc.core.api;

import lombok.Data;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

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
@ToString
public class RpcRequest {

    private String service; // 接口: com.stephenshen.ssrpc.demo.api.UserService
    private String methodSign; // 方法: findById
    private Object[] args; // 参数: 100

    // 跨调用方需要传递的参数
    private Map<String,String> params = new HashMap<>();
}
