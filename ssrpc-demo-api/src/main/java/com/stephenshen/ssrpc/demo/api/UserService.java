package com.stephenshen.ssrpc.demo.api;

/**
 * <p>
 *
 * </p>
 *
 * @author stephenshen
 * @version 1.0
 * @date 2024/3/8 07:44
 */
public interface UserService {
    User findById(int id);

    User findById(int id, String name);

    int getId(int id);

    String getName();

    String getName(int id);
}