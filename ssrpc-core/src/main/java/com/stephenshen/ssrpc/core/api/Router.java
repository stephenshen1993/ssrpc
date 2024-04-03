package com.stephenshen.ssrpc.core.api;

import java.util.List;

/**
 * 路由器
 *
 * @author stephenshen
 * @date 2024/3/24 16:28
 */
public interface Router<T> {

    List<T> route(List<T> providers);

    Router Default = p -> p;
}