package com.stephenshen.ssrpc.core.api;

import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author stephenshen
 * @version 1.0
 * @date 2024/3/24 16:28
 */
public interface Router<T> {

    List<T> route(List<T> providers);

    Router Default = p -> p;
}