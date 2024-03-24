package com.stephenshen.ssrpc.core.api;

import lombok.Data;

import java.util.List;

/**
 * @author stephenshen
 * @version 1.0
 * @date 2024/3/24 18:08
 */
@Data
public class RpcContext {

    List<Filter> filters; // TODO

    Router router;

    LoadBalancer loadBalancer;
}
