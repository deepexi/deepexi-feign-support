package com.deepexi.support.feign;

public interface PayloadHandler<T> {

    T getPayload();

}
