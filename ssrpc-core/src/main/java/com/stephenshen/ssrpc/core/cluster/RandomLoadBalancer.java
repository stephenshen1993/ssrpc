package com.stephenshen.ssrpc.core.cluster;

import com.stephenshen.ssrpc.core.api.LoadBalancer;

import java.util.List;
import java.util.Random;

/**
 * @author stephenshen
 * @version 1.0
 * @date 2024/3/24 17:22
 */
public class RandomLoadBalancer<T> implements LoadBalancer<T> {

    Random random = new Random();

    @Override
    public T choose(List<T> providers) {
        if (providers == null || providers.isEmpty()) return null;
        if (providers.size() == 1) return providers.get(0);
        return providers.get(random.nextInt(providers.size()));
    }
}
