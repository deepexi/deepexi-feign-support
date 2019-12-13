package com.deepexi.support.feign;

public interface Payload<T> {
    T parseData();
}
