package com.stephenshen.ssrpc.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * config app properties.
 *
 * @author stephenshen
 * @date 2024/4/13 17:25:04
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "ssrpc.app")
public class AppConfigProperties {

    // for app instance
    private String id = "app1";

    private String namespace = "public";

    private String env = "dev";
}
