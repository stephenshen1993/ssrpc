package com.stephenshen.ssrpc.core.meta;

import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * 描述服务实例的元数据
 * @author stephenshen
 * @create 2024/03/29 07:08:25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InstanceMeta {
    private String scheme;
    private String host;
    private Integer port;
    private String context;

    private boolean status; // online or offline
    private Map<String, String> parameters = new HashMap<>();  // idc  A B C

    public InstanceMeta(String scheme, String host, Integer port, String context) {
        this.scheme = scheme;
        this.host = host;
        this.port = port;
        this.context = context;
    }

    public String toPath() {
        return String.format("%s_%d", host, port);
    }

    public String toUrl() {
        return String.format("%s://%s:%d/%s", scheme, host, port, context);
    }

    public static InstanceMeta http(String host, Integer port) {
        return new InstanceMeta("http", host, port, "");
    }

    public InstanceMeta addParams(Map<String, String> params) {
        this.parameters.putAll(params);
        return this;
    }

    public String toMetas() {
        return JSON.toJSONString(parameters);
    }
}
