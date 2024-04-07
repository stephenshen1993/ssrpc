package com.stephenshen.ssrpc.core.api;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author stephenshen
 * @create 2024/04/08 06:44:55
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SsrpcException extends RuntimeException {

    private String errCode;

    public SsrpcException() {
    }

    public SsrpcException(String message) {
        super(message);
    }

    public SsrpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public SsrpcException(Throwable cause) {
        super(cause);
    }

    public SsrpcException(Throwable cause, String errCode) {
        super(cause);
        this.errCode = errCode;
    }

    // X ==> 技术类异常
    // Y ==> 业务类异常
    // Z ==> unknown，搞不清楚，再归类到X或Y
    public static final String SocketTimeoutEx = "X001" + "-" + "http_invoke_timeout";
    public static final String NoSuchMethodEx = "X002" + "-" + "method_not_exists";
    public static final String UnknownEx = "Z001" + "-" + "unknown";
}
