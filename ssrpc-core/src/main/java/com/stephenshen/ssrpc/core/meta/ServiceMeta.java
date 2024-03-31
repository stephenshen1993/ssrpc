package com.stephenshen.ssrpc.core.meta;

import lombok.Builder;
import lombok.Data;

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

    public String toPath() {
        return String.format("%s_%s_%s_%s", app, namespace, env, name);
    }
}
