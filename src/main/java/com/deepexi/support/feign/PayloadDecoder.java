package com.deepexi.support.feign;

import feign.FeignException;
import feign.Response;
import feign.codec.DecodeException;
import lombok.Data;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.HttpMessageConverterExtractor;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * @deprecated use {@link AbstractPayloadDecoder} instead.
 */
public class PayloadDecoder extends SpringDecoder {
    private ObjectFactory<HttpMessageConverters> messageConverters;

    public PayloadDecoder(ObjectFactory<HttpMessageConverters> messageConverters) {
        super(messageConverters);
        this.messageConverters = messageConverters;
    }

    public Object decode(Response response, Type type) throws IOException, FeignException {
        if (!(type instanceof Class) && !(type instanceof ParameterizedType) && !(type instanceof WildcardType)) {
            throw new DecodeException("type is not an instance of Class or ParameterizedType: " + type);
        } else {
            HttpMessageConverterExtractor<?> extractor = null;
            if (!Payload.class.isAssignableFrom(getRawClass(type))) {
                ParameterizedTypeImpl wrapperType = ParameterizedTypeImpl.make(Payload.class, new Type[]{type}, null);
                extractor = new HttpMessageConverterExtractor(wrapperType, this.messageConverters.getObject().getConverters());
                Payload r = (Payload) extractor.extractData(new DuplicatedFeignResponseAdapter(response));
                this.check(r);
                return r.getPayload();
            } else {
                extractor = new HttpMessageConverterExtractor(type, this.messageConverters.getObject().getConverters());
                return extractor.extractData(new DuplicatedFeignResponseAdapter(response));
            }
        }
    }

    Class getRawClass(Type type) {
        Class clazz = null;
        if (type instanceof Class) {
            clazz = (Class) type;
        } else if (type instanceof ParameterizedType) {
            clazz = (Class) ((ParameterizedType) type).getRawType();
        }
        return clazz;
    }

    private void check(Payload payload) {
        // TODO:: do something
    }

    /**
     * this class is copied from {@link FeignResponseAdapter}
     * why? because it is private...
     */
    private class DuplicatedFeignResponseAdapter implements ClientHttpResponse {

        private final Response response;

        private DuplicatedFeignResponseAdapter(Response response) {
            this.response = response;
        }

        @Override
        public HttpStatus getStatusCode() throws IOException {
            return HttpStatus.valueOf(this.response.status());
        }

        @Override
        public int getRawStatusCode() throws IOException {
            return this.response.status();
        }

        @Override
        public String getStatusText() throws IOException {
            return this.response.reason();
        }

        @Override
        public void close() {
            try {
                this.response.body().close();
            } catch (IOException ex) {
                // Ignore exception on close...
            }
        }

        @Override
        public InputStream getBody() throws IOException {
            return this.response.body().asInputStream();
        }

        @Override
        public HttpHeaders getHeaders() {
            return getHttpHeaders(this.response.headers());
        }
    }

    static HttpHeaders getHttpHeaders(Map<String, Collection<String>> headers) {
        HttpHeaders httpHeaders = new HttpHeaders();
        for (Map.Entry<String, Collection<String>> entry : headers.entrySet()) {
            httpHeaders.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        return httpHeaders;
    }

    @Data
    static class Payload<T> {
        private Boolean success;
        private String code;
        private T payload;
    }
}
