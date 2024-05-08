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
public class ConsumerProperties {

    // for ha and governance
    private int retries = 1;

    private int timeout = 1000;

    private int faultLimit = 10;

    private int halfOpenInitialDelay = 10_000;

    private int halfOpenDelay = 60_000;

    private int grayRatio = 0;
}
