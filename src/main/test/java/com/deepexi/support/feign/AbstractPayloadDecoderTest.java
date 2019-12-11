package com.deepexi.support.feign;

import feign.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class AbstractPayloadDecoderTest {

    @Autowired
    private ObjectFactory<HttpMessageConverters> messageConverters;

    private TestPayloadDecoder testPayloadDecoder;

    @Before
    public void setup() {
        testPayloadDecoder = new TestPayloadDecoder(messageConverters);
    }


    @Test
    public void decode() throws IOException {
        Class clazz = MockData.class;
        Response response = Response.create(200, "hh", null, "{}".getBytes());
        Object decode = testPayloadDecoder.decode(response, clazz);
        System.out.println("decode = " + decode);
    }


    static class TestPayloadDecoder extends AbstractPayloadDecoder<OtherPayload> {

        public TestPayloadDecoder(ObjectFactory<HttpMessageConverters> messageConverters) {
            super(messageConverters);
        }
    }

    static class OtherPayload<T> implements PayloadHandler {

        private T data;

        @Override
        public T getPayload() {
            return data;
        }
    }

    static class MockData {

    }
}