package com.stephenshen.ssrpc.core.meta;

import lombok.Data;

import java.lang.reflect.Method;

/**
 * <p>
 *
 * </p>
 *
 * @author stephenshen
 * @version 1.0
 * @date 2024/3/21 07:01
 */
@Data
public class ProviderMeta {

    private Method method;
    private String methodSign;
    private Object serviceImpl;
}
