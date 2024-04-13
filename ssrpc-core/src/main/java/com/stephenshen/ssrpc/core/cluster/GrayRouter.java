package com.stephenshen.ssrpc.core.cluster;

import com.stephenshen.ssrpc.core.api.Router;
import com.stephenshen.ssrpc.core.meta.InstanceMeta;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 灰度路由
 *
 * // 可以做一些灰度用户，某一次请求上加灰度标记
 *
 * @author stephenshen
 * @date 2024/4/13 10:25:52
 */
@Slf4j
@Data
public class GrayRouter implements Router<InstanceMeta> {

    private int grayRatio;
    private final Random random = new Random();

    public GrayRouter(int grayRatio) {
        this.grayRatio = grayRatio;
    }

    @Override
    public List<InstanceMeta> route(List<InstanceMeta> providers) {

        if (providers == null || providers.isEmpty()) {
            return providers;
        }

        List<InstanceMeta> normalNodes = new ArrayList<>();
        List<InstanceMeta> grayNodes = new ArrayList<>();
        providers.forEach(p -> {
            if ("true".equals(p.getParameters().get("gray"))) {
                grayNodes.add(p);
            } else {
                normalNodes.add(p);
            }
        });

        log.debug(" grayRouter grayNodes/normalNodes, grayRatio ===> {}/{},{}", grayNodes.size(), normalNodes.size(), grayRatio);

        if (normalNodes.isEmpty() || grayNodes.isEmpty()) return providers;
        if (grayRatio <= 0) {
            return normalNodes;
        } else if (grayRatio >= 100) {
            return grayNodes;
        }

        if (random.nextInt(100) < grayRatio) {
            log.debug(" grayRouter grayNodes ===> {}", grayNodes);
            return grayNodes;
        } else {
            log.debug(" grayRouter normalNodes ===> {}", normalNodes);
            return normalNodes;
        }
    }
}
