package com.stephenshen.ssrpc.core.meta;

import com.alibaba.fastjson.JSON;
import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 描述服务元数据
 *
 * @author stephenshen
 * @create 2024/03/31 18:12:18
 */
@Data
@Builder
public class ServiceMeta {

    private String app;
    private String namespace;
    private String env;
    private String name;

    private Map<String, String> parameters = new HashMap<>(); // version: 0.0.1

    public String toPath() {
        return String.format("%s_%s_%s_%s", app, namespace, env, name);
    }

    public String toMetas() {
        return JSON.toJSONString(parameters);
    }
}
