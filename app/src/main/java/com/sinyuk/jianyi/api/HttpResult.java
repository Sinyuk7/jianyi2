package com.sinyuk.jianyi.api;

/**
 * Created by Sinyuk on 16/9/10.
 */
public class HttpResult<T> {
    private int status;
    private int code;
    private T data;

    public int getStatus() {
        return status;
    }

    public int getCode() {
        return code;
    }

    public T getData() {
        return data;
    }

    @Override
    public String toString() {
        return "HttpResult{" +
                "status=" + status +
                ", code=" + code +
                ", data=" + data +
                '}';
    }
}
