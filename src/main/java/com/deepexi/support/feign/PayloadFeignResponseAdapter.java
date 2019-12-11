package com.deepexi.support.feign;

import feign.Response;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class PayloadFeignResponseAdapter implements ClientHttpResponse {

    private final Response response;

    public PayloadFeignResponseAdapter(Response response) {
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

    static HttpHeaders getHttpHeaders(Map<String, Collection<String>> headers) {
        HttpHeaders httpHeaders = new HttpHeaders();
        for (Map.Entry<String, Collection<String>> entry : headers.entrySet()) {
            httpHeaders.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        return httpHeaders;
    }
}
