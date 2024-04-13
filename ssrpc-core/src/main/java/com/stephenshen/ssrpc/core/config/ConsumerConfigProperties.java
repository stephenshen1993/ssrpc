package com.stephenshen.ssrpc.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * config consumer properties.
 *
 * @author stephenshen
 * @date 2024/4/13 17:27:27
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "ssrpc.consumer")
public class ConsumerConfigProperties {

    // for ha and governance
    private int retries;

    private int timeout;

    private int faultLimit;

    private int halfOpenInitialDelay;

    private int halfOpenDelay;

    private int grayRatio;
}
