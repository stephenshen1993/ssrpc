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

    public String param(String key) {
        return parameters.get(key);
    }

    // ssrpc.color = gray
    // ssrpc.gtrace_id
    // gw -> service1 ->  service2(跨线程传递) ...
    // http headers

    public static ThreadLocal<Map<String, String>> ContextParameters = new ThreadLocal<>() {
        @Override
        protected Map<String, String> initialValue() {
            return new HashMap<>();
        }
    };

    public static void setContextParameter(String key, String value) {
        ContextParameters.get().put(key, value);
    }

    public static String getContextParameter(String key) {
        return ContextParameters.get().get(key);
    }

    public static void removeContextParameter(String key) {
        ContextParameters.get().remove(key);
    }

}
