package com.willishz.util.conn;

import org.apache.http.client.HttpClient;

/**
 * 线程安全的HttpCLient,单例模式，支持http、https协议
 */
public class CustomHttpClientSingleton {
    private static HttpClient customHttpClient = CustomHttpClient.getHttpClient();

    private CustomHttpClientSingleton() {
    }

    public static HttpClient getHttpClient() {
        return customHttpClient;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

}
