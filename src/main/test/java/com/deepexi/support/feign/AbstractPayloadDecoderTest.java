package com.deepexi.support.feign;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import feign.Response;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class AbstractPayloadDecoderTest {
    @Autowired
    private ObjectFactory<HttpMessageConverters> messageConverters;

    private TestPayloadDecoder decoder;
    private String data = "{\"payload\":{\"totalElements\":27,\"content\":\"hhhhhhhh\",\"number\":1,\"size\":27,\"totalPages\":1,\"numberOfElements\":27},\"code\":\"0\",\"msg\":\"ok\"}";

    @Before
    public void setup() {
        decoder = new TestPayloadDecoder(messageConverters);
    }

    @Test
    public void decode() throws IOException {
        OtherPayload<MockData> payload = new OtherPayload<>();
        Type clazz = payload.getClass();
        Map<String, Collection<String>> headers = Maps.newLinkedHashMap();
        headers.put("Content-Type", Lists.newArrayList(APPLICATION_JSON_UTF8_VALUE));
        Response response = Response.create(200, "OK", headers, data.getBytes());
        MockData data = (MockData) decoder.decode(response, clazz);
        log.info("Decoder data: {}", payload);
        log.info("Payload data type: {}", data.getClass());
//        log.info("Payload data type: {}", payload.parseData().getClass());
//      java.lang.ClassCastException: java.util.LinkedHashMap cannot be cast to com.deepexi.support.feign.AbstractPayloadDecoderTest$MockData
        assertThat(payload.parseData()).isNotNull();
        assertThat(payload.getCode()).isEqualTo("0");
    }

    private static class TestPayloadDecoder extends AbstractPayloadDecoder<OtherPayload> {
        public TestPayloadDecoder(ObjectFactory<HttpMessageConverters> messageConverters) {
            super(messageConverters);
        }

        @Override
        protected void check(OtherPayload otherPayload) {
            // do nothing
        }
    }

    @Data
    private static class OtherPayload<T> implements Payload<T> {
        private String msg;
        private T payload;
        private String code;

        @Override
        public T parseData() {
            return this.payload;
        }
    }

    @Data
    private static class MockData {
        private String data;
        private int totalElements;
        private int number;
        private int size;
        private int totalPages;
        private int numberOfElements;
        private String content;
    }
}