package com.deepexi.support.feign;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import feign.Response;
import feign.codec.DecodeException;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
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
@SpringBootTest(classes = TestApp.class)
@RunWith(SpringRunner.class)
public class AbstractPayloadDecoderTest {

    @Autowired
    private ObjectFactory<HttpMessageConverters> messageConverters;

    private TestPayloadDecoder decoder;
    private TestNonPayloadDecoder nonPayloadDecoder;
    private String payloadData = "{\"payload\":{\"foo\": \"bar\"},\"code\":\"0\",\"msg\":\"ok\"}";
    private String data = "{\"foo\": \"bar\"}";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setup() {
        decoder = new TestPayloadDecoder(messageConverters);
        nonPayloadDecoder = new TestNonPayloadDecoder(messageConverters);
    }

    @Test
    public void decodePayload() throws IOException {
        Response response = this.getResponse(payloadData, 200);
        MockData data = (MockData) decoder.decode(response, MockData.class);
        log.info("Payload data type: {}", data.getClass());
        log.info("Payload data: {}", data);
        assertThat(data.getFoo()).isEqualTo("bar");
    }

    @Test
    public void decode() throws IOException {
        Response response = this.getResponse(payloadData, 200);
        MockData data = (MockData) decoder.decode(response, MockData.class);
        log.info("Payload data type: {}", data.getClass());
        log.info("Payload data: {}", data);
        assertThat(data.getFoo()).isEqualTo("bar");
    }


    @Test
    public void decodeThrowException() throws IOException {
        thrown.expect(DecodeException.class);
        thrown.expectMessage("type is not an instance of Class or ParameterizedType");
        decoder.decode(this.getResponse(payloadData, 200), null);
    }


    @Test
    public void nonPayloadDecode() throws IOException {
        Response response = this.getResponse(data, 200);
        MockDataNonPayload data = (MockDataNonPayload) nonPayloadDecoder.decode(response, MockDataNonPayload.class);
        log.info("Payload data type: {}", data.getClass());
        log.info("Payload data: {}", data);
        assertThat(data.getFoo()).isEqualTo("bar");
    }

    private Response getResponse(String data, int status) {
        Map<String, Collection<String>> headers = Maps.newLinkedHashMap();
        headers.put("Content-Type", Lists.newArrayList(APPLICATION_JSON_UTF8_VALUE));
        return Response.builder()
                .request(null)
                .headers(headers)
                .status(status)
                .reason("OK")
                .body(data.getBytes())
                .build();
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

    private static class TestNonPayloadDecoder extends AbstractPayloadDecoder<MockDataNonPayload> {
        public TestNonPayloadDecoder(ObjectFactory<HttpMessageConverters> messageConverters) {
            super(messageConverters);
        }

        @Override
        protected void check(MockDataNonPayload mockData) {
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

    @Data
    @ToString
    private static class MockDataNonPayload<T> implements Payload<T> {
        private T foo;

        @Override
        public T parseData() {
            return foo;
        }
    }
}