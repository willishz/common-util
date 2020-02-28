package com.willishz.util.conn;

/**
 *
 */

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

/**
 * 支持多线程的httpclient
 */
public class CustomHttpClient {

    /**
     * 设置请求超时
     */
    private static final int CONNECTION_TIMEOUT = 60 * 1000;
    /**
     * 设置等待数据超时
     */
    private static final int SO_TIMEOUT = 60 * 1000;
    /**
     * 客户端总并行链接最大数
     */
    private static final int MAX_CONNECTIONS_TOTAL = 1000;
    /**
     * 每个主机的最大并行链接数
     */
    private static final int MAX_CONNECTIONS_PER_ROUTE = MAX_CONNECTIONS_TOTAL;
    /**
     * 连接不够用的时候等待超时时间
     */
    private static final long CONN_MANAGER_TIMEOUT = CONNECTION_TIMEOUT;

    public static HttpClient getHttpClient() {

        HttpClient httpClient = new DefaultHttpClient();

        // 设置组件参数, HTTP协议的版本,1.1/1.0/0.9
        HttpParams params = httpClient.getParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
        HttpProtocolParams.setUseExpectContinue(params, true);

        //设置连接超时时间
        //HttpConnectionParams.setConnectionTimeout(params, REQUEST_TIMEOUT);
        //HttpConnectionParams.setSoTimeout(params, SO_TIMEOUT);
        params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, CONNECTION_TIMEOUT);
        params.setParameter(CoreConnectionPNames.SO_TIMEOUT, SO_TIMEOUT);
        params.setLongParameter(ClientPNames.CONN_MANAGER_TIMEOUT, CONN_MANAGER_TIMEOUT);

        return httpClient;
    }

    public static HttpClient getHttpsClient() {

        KeyStore trustStore;
        SSLSocketFactory sf = null;
        try {
            trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);
            sf = new MySSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        } catch (KeyManagementException e) {
            System.out.println(e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            System.out.println(e.getMessage());
        } catch (UnrecoverableKeyException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (CertificateException e) {
            System.out.println(e.getMessage());
        } catch (KeyStoreException e) {
            System.out.println(e.getMessage());
        }

        //设置访问协议
        SchemeRegistry schReg = new SchemeRegistry();
        schReg.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
        schReg.register(new Scheme("https", 443, sf));

        PoolingClientConnectionManager pccm = new PoolingClientConnectionManager(schReg);

        pccm.setMaxTotal(MAX_CONNECTIONS_TOTAL);
        pccm.setDefaultMaxPerRoute(MAX_CONNECTIONS_PER_ROUTE);

        HttpClient httpClient = new DefaultHttpClient(pccm);

        // 设置组件参数, HTTP协议的版本,1.1/1.0/0.9
        HttpParams params = httpClient.getParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
        HttpProtocolParams.setUseExpectContinue(params, true);

        //设置连接超时时间
        //HttpConnectionParams.setConnectionTimeout(params, REQUEST_TIMEOUT);
        //HttpConnectionParams.setSoTimeout(params, SO_TIMEOUT);
        params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, CONNECTION_TIMEOUT);
        params.setParameter(CoreConnectionPNames.SO_TIMEOUT, SO_TIMEOUT);
        params.setLongParameter(ClientPNames.CONN_MANAGER_TIMEOUT, CONN_MANAGER_TIMEOUT);

        return httpClient;
    }
}
