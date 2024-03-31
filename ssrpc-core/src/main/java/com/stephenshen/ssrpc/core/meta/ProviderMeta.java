package com.stephenshen.ssrpc.core.meta;

import lombok.Data;

import java.lang.reflect.Method;

/**
 * 描述Provider的映射关系
 *
 * @author stephenshen
 * @date 2024/3/21 07:01
 */
@Data
public class ProviderMeta {

    private Method method;
    private String methodSign;
    private Object serviceImpl;
}
