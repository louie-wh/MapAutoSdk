package com.baidu.mapautosdk.http;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsyncHttpClient {

    private static AsyncHttpClient asyncHttpClient;

    private static ExecutorService executorService;

    public AsyncHttpClient() {
    }

    public static synchronized AsyncHttpClient getInstance() {
        if (null == asyncHttpClient) {
            asyncHttpClient = new AsyncHttpClient();
        }

        if (null == executorService) {
            executorService = Executors.newCachedThreadPool();
        }
        return asyncHttpClient;
    }

    public void get(String url, HttpClientUtil.HttpCallbackListener httpCallbackListener) {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                HttpClientUtil httpClientUtil = new HttpClientUtil("GET", httpCallbackListener);
                httpClientUtil.request(url);
            }
        });
    }
}
