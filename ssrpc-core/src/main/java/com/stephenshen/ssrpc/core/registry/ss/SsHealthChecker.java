package com.stephenshen.ssrpc.core.registry.ss;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * check healthh for registry center.
 * @author stephenshen
 * @date 2024/5/29 07:31:45
 */
@Slf4j
public class SsHealthChecker {

    ScheduledExecutorService consumerExecutor = null;
    ScheduledExecutorService providerExecutor = null;


    public void start() {
        log.info(" ====>>> [SSRegistry] : start with health checker.");
        consumerExecutor = Executors.newScheduledThreadPool(1);
        providerExecutor = Executors.newScheduledThreadPool(1);
    }

    public void stop() {
        log.info(" ====>>> [SSRegistry] : stop with health checker.");
        gracefulShutdown(consumerExecutor);
        gracefulShutdown(providerExecutor);
    }

    public void consumerCheck(Callback callback) {
        consumerExecutor.scheduleWithFixedDelay(() -> {
            try {
                callback.call();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }, 1, 5, TimeUnit.SECONDS);
    }

    public void providerCheck(Callback callback) {
        providerExecutor.scheduleWithFixedDelay(() -> {
            try {
                callback.call();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }, 5, 5, TimeUnit.SECONDS);
    }

    private void gracefulShutdown(ScheduledExecutorService executorService) {
        executorService.shutdown();
        try {
            executorService.awaitTermination(1000, TimeUnit.MILLISECONDS);
            if (executorService.isTerminated()) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            // ignore
        }
    }

    public interface Callback {
        void call() throws Exception;
    }
}
