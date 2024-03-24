package com.stephenshen.ssrpc.core.cluster;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import com.stephenshen.ssrpc.core.api.LoadBalancer;

/**
 * @author stephenshen
 * @version 1.0
 * @date 2024/3/24 17:22
 */
public class RoundRibonLoadBalancer<T> implements LoadBalancer<T> {

    AtomicInteger index = new AtomicInteger(0);

    @Override
    public T choose(List<T> providers) {
        if (providers == null || providers.isEmpty()) return null;
        if (providers.size() == 1) return providers.get(0);
        return providers.get((index.getAndIncrement()&0x7fffffff) % providers.size());
    }
}
