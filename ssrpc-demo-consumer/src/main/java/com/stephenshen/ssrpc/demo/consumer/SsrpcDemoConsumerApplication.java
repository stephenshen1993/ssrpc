package com.stephenshen.ssrpc.demo.consumer;

import com.stephenshen.ssrpc.core.annotation.SSConsumer;
import com.stephenshen.ssrpc.core.consumer.ConsumerConfig;
import com.stephenshen.ssrpc.demo.api.Order;
import com.stephenshen.ssrpc.demo.api.OrderService;
import com.stephenshen.ssrpc.demo.api.User;
import com.stephenshen.ssrpc.demo.api.UserService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.util.Arrays;

@SpringBootApplication
@Import({ConsumerConfig.class})
public class SsrpcDemoConsumerApplication {

    @SSConsumer
    private UserService userService;

    @SSConsumer
    private OrderService orderService;

    public static void main(String[] args) {
        SpringApplication.run(SsrpcDemoConsumerApplication.class, args);
    }


    @Bean
    public ApplicationRunner consumer_runner() {
        return x -> {

            System.out.println("userService.getId(10) = " + userService.getId(10));

            System.out.println("userService.getId(10f) = " +
                userService.getId(10f));

            System.out.println("userService.getId(new User(100, \"SS\")) = " +
                userService.getId(new User(100, "SS")));

            User user = userService.findById(1);
            System.out.println("RPC result userService.findById(1) = " + user);

            User user1 = userService.findById(1, "hubao");
            System.out.println("RPC result userService.findById(1, \"hubao\") = " + user1);

            System.out.println(userService.getName());

            System.out.println(userService.getName(123));

            System.out.println(userService.toString());

            System.out.println(userService.getId(11));

            System.out.println(userService.getName());

            System.out.println("===> userService.getLongIds()");
            for (long id : userService.getLongIds()) {
                System.out.println(id);
            }

            System.out.println("===> userService.getIds()");
            for (long id : userService.getIds(new int[]{4,5,6})) {
                System.out.println(id);
            }

            // Order order = orderService.findById(2);
            // System.out.println("RPC result orderService.findById(2) = " + order);

//            Order order404 = orderService.findById(404);
//            System.out.println("RPC result orderService.findById(404) = " + order404);
        };
    }

}