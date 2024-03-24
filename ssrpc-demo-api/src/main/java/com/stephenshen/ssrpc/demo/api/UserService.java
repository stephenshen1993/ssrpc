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

    long getId(long id);

    long getId(User user);

    long getId(float id);

    int[] getIds();

    long[] getLongIds();

    int[] getIds(int[] ids);

    String getName();

    String getName(int id);
}