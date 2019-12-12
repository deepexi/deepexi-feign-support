package com.deepexi.support.feign;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import feign.Response;
import lombok.Data;
import lombok.ToString;
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
import java.util.Collection;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class AbstractPayloadDecoderTest {
    @Autowired
    private ObjectFactory<HttpMessageConverters> messageConverters;

    private TestPayloadDecoder decoder;
    private String data = "{\"payload\":{\"foo\": \"bar\"},\"code\":\"0\",\"msg\":\"ok\"}";

    @Before
    public void setup() {
        decoder = new TestPayloadDecoder(messageConverters);
    }

    @Test
    public void decode() throws IOException {
        Map<String, Collection<String>> headers = Maps.newLinkedHashMap();
        headers.put("Content-Type", Lists.newArrayList(APPLICATION_JSON_UTF8_VALUE));
        Response response = Response.create(200, "OK", headers, data.getBytes());
        MockData data = (MockData) decoder.decode(response, MockData.class);
        log.info("Payload data type: {}", data.getClass());
        log.info("Payload data: {}", data);
        assertThat(data.getFoo()).isEqualTo("bar");
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
    @ToString
    private static class MockData {
        private String foo;
    }
}