package com.stephenshen.ssrpc.demo.provider;

import com.stephenshen.ssrpc.core.annotation.SSProvider;
import com.stephenshen.ssrpc.demo.api.Order;
import com.stephenshen.ssrpc.demo.api.OrderService;
import org.springframework.stereotype.Component;

/**
 * <p>
 *
 * </p>
 *
 * @author stephenshen
 * @version 1.0
 * @date 2024/3/15 07:04
 */
@Component
@SSProvider
public class OrderServiceImpl implements OrderService {
    @Override
    public Order findById(Integer id) {
        if (id == 404) {
            throw new RuntimeException("404 exception");
        }
        return new Order(id.longValue(), 15.6f);
    }
}
