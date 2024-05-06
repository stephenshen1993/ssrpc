package com.stephenshen.ssrpc.core.config;

import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import lombok.Data;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * config provider properties.
 *
 * @author stephenshen
 * @date 2024/4/13 17:29:43
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "ssrpc.provider")
@EnableApolloConfig
@RefreshScope
@ImportAutoConfiguration(RefreshAutoConfiguration.class)
public class ProviderConfigProperties {

    // for provider
    Map<String, String> metas = new HashMap<>();

    String test;
}
