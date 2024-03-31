package com.stephenshen.ssrpc.core.registry;

import com.stephenshen.ssrpc.core.meta.InstanceMeta;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * @author stephenshen
 * @version 1.0
 * @date 2024/3/27 06:47
 */
@Data
@AllArgsConstructor
public class Event {
    List<InstanceMeta> data;
}
