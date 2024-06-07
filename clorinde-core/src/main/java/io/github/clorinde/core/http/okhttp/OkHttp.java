package io.github.clorinde.core.http.okhttp;

import cn.hutool.core.util.URLUtil;
import com.google.common.base.Joiner;
import com.google.common.base.Stopwatch;
import com.google.common.base.Ticker;
import io.github.clorinde.core.http.Http;
import io.github.clorinde.core.wrapper.ReqWrapper;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author: laoshiren
 * @date: 2024/05/10 16:09
 **/
public class OkHttp implements Http {

    private static final int READ_TIMEOUT = 100;
    private static final int CONNECT_TIMEOUT = 60;
    private static final int WRITE_TIMEOUT = 60;

    private final OkHttpClient okHttpClient;

    private static OkHttp okHttp;

    private OkHttp(Integer readTimeout, Integer connectTimeout, Integer writeTimeout) {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        // 读取超时
        clientBuilder.readTimeout(readTimeout, TimeUnit.SECONDS);
        // 连接超时
        clientBuilder.connectTimeout(connectTimeout, TimeUnit.SECONDS);
        //写入超时
        clientBuilder.writeTimeout(writeTimeout, TimeUnit.SECONDS);
        okHttpClient = clientBuilder.build();
    }

    public static OkHttp instance(Integer readTimeout, Integer connectTimeout, Integer writeTimeout) {
        synchronized (OkHttp.class) {
            if (okHttp == null) {
                okHttp = new OkHttp(
                        readTimeout == null ? READ_TIMEOUT : readTimeout,
                        connectTimeout  == null ? CONNECT_TIMEOUT : connectTimeout,
                        writeTimeout == null ? WRITE_TIMEOUT : writeTimeout);
            }
        }
        return okHttp;
    }

    public static OkHttp instance() {
        return instance(READ_TIMEOUT, CONNECT_TIMEOUT, WRITE_TIMEOUT);
    }

    private String urlParam(String requestUrl, List<Pair<String, String>> param) {
        List<String> kvString = param.stream()
                .map(it -> String.format("%s=%s", it.getKey(), URLUtil.encode(it.getValue())))
                .collect(Collectors.toList());
        return requestUrl + "?" + Joiner.on("&").join(kvString);
    }

    @Override
    public String get(String requestUrl, Map<String, String> header, List<Pair<String, String>> param, String json, List<Object> otherParam) {
        Request.Builder builder = new Request.Builder();
        header.forEach(builder::addHeader);

        requestUrl = urlParam(requestUrl, param);

        Request request = builder
                .url(requestUrl)
                .get()
                .build();
        Call call = okHttpClient.newCall(request);
        Response response;
        try {
            response = call.execute();
            return response.body().string();
        } catch (IOException e) {

        }
        return null;
    }

    @Override
    public String post(String requestUrl, Map<String, String> header, List<Pair<String, String>> param, String json, List<Object> otherParam) {
        Request.Builder builder = new Request.Builder();
        header.forEach(builder::addHeader);
        requestUrl = urlParam(requestUrl, param);
        RequestBody body = RequestBody.create(json, MediaType.parse("*/*"));
        Request request = builder
                .url(requestUrl)
                .post(body)
                .build();
        Call call = okHttpClient.newCall(request);
        Response response = null;
        try {
            response = call.execute();
            return response.body().string();
        } catch (IOException e) {

        }
        return null;
    }

    @Override
    public String delete(String requestUrl, Map<String, String> header, List<Pair<String, String>> param, String json, List<Object> otherParam) {
        Request.Builder builder = new Request.Builder();
        header.forEach(builder::addHeader);
        requestUrl = urlParam(requestUrl, param);
        RequestBody body = StringUtils.isBlank(json) ? null : RequestBody.create(json, MediaType.parse("*/*"));
        Request request = builder
                .url(requestUrl)
                .delete(body)
                .build();
        Call call = okHttpClient.newCall(request);
        Response response = null;
        try {
            response = call.execute();
            return response.body().string();
        } catch (IOException e) {

        }
        return null;
    }

    @Override
    public String put(String requestUrl, Map<String, String> header, List<Pair<String, String>> param, String json, List<Object> otherParam) {
        Request.Builder builder = new Request.Builder();
        header.forEach(builder::addHeader);
        requestUrl = urlParam(requestUrl, param);
        RequestBody body = RequestBody.create(json, MediaType.parse("*/*"));
        Request request = builder
                .url(requestUrl)
                .put(body)
                .build();
        Call call = okHttpClient.newCall(request);
        Response response = null;
        try {
            response = call.execute();
            return response.body().string();
        } catch (IOException e) {

        }
        return null;
    }


    @Override
    public String request(ReqWrapper wrapper) {
        switch (wrapper.getDuelType()) {
            case GET:
                return this.get(wrapper);
            case POST:
                return this.post(wrapper);
            case PUT:
                return this.put(wrapper);
            case DELETE:
                return this.delete(wrapper);
        }
        return null;
    }


    @Override
    public String get(ReqWrapper wrapper) {
        return this.get(wrapper.requestUrl(), wrapper.getHeader(), wrapper.getParam(), wrapper.getJsonBody(), null);
    }

    @Override
    public String post(ReqWrapper wrapper) {
        return this.post(wrapper.requestUrl(), wrapper.getHeader(), wrapper.getParam(), wrapper.getJsonBody(), null);
    }

    @Override
    public String delete(ReqWrapper wrapper) {
        return this.delete(wrapper.requestUrl(), wrapper.getHeader(), wrapper.getParam(), wrapper.getJsonBody(), null);
    }

    @Override
    public String put(ReqWrapper wrapper) {
        return this.put(wrapper.requestUrl(), wrapper.getHeader(), wrapper.getParam(), wrapper.getJsonBody(), null);
    }
}
