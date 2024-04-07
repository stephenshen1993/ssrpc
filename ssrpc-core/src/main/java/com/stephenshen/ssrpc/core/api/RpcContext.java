package com.stephenshen.ssrpc.core.api;

import com.stephenshen.ssrpc.core.meta.InstanceMeta;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author stephenshen
 * @version 1.0
 * @date 2024/3/24 18:08
 */
@Data
public class RpcContext {

    List<Filter> filters; // TODO

    Router<InstanceMeta> router;

    LoadBalancer<InstanceMeta> loadBalancer;

    Map<String, String> parameters = new HashMap<>();
}
