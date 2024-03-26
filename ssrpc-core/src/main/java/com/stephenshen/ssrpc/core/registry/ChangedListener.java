package com.stephenshen.ssrpc.core.registry;

/**
 * @author stephenshen
 * @version 1.0
 * @date 2024/3/27 06:41
 */
public interface ChangedListener {
    void fire(Event event);
}
