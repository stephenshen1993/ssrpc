package com.stephenshen.ssrpc.core.annotation;

import com.stephenshen.ssrpc.core.consumer.ConsumerConfig;
import com.stephenshen.ssrpc.core.provider.ProviderConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 快捷启用入口
 *
 * @author stephenshen
 * @date 2024/4/13 17:03:41
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Import({ProviderConfig.class, ConsumerConfig.class})
public @interface EnableSsrpc {
}
