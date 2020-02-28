package com.willishz.util.conn;

import com.google.gson.Gson;
import com.willishz.util.StringUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.util.*;

/**
 * 发送http请求
 *
 * 
 *
 */
public class HttpRequestSimple {

    private static Log log = LogFactory.getLog(HttpRequestSimple.class);
    public static final String APPLICATION_JSON = "application/json";
    public static void main(String[] args) {
        String url = "http://localhost:9090/mgmt/listUserFinancePlanRecords.action?crm_token=TEST_1234567890";
        Map<String, Object> param = new HashMap();
        param.put("userId", 3);
        param.put("page", 1);
        param.put("rows", 10);
        try {
            HttpResult result = HttpRequestSimple.postSendHttpParam(url, param);
            String message = result.getMessage();
            System.out.println("message:" + message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String httpPostWithJSON(String url, String json) throws Exception {
        // 将JSON进行UTF-8编码,以便传输中文
//        String encoderJson = URLEncoder.encode(json, HTTP.UTF_8);

        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON);

        StringEntity se = new StringEntity(json.toString(), Charset.forName("utf-8"));
        se.setContentType("application/json");
//        se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON));
        se.setContentEncoding("UTF-8");
        httpPost.setEntity(se);
        HttpResponse response = httpClient.execute(httpPost);
        HttpEntity entity = response.getEntity();
        InputStream is = entity.getContent();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line = null;
        StringBuffer sb = new StringBuffer();
        while ((line = reader.readLine()) != null){
            sb.append(line + "\n");
        }
        String result = sb.toString();
        return result;
    }

    public static HttpResult postSendHttpBody(String url, String body) {
        return postSendHttp(url, body, null, null);
    }

    public static HttpResult postSendHttpJson(String url, Gson postData) {
        return postSendHttp(url, null, postData, null);
    }

    public static HttpResult postSendHttpParam(String url, Map param) {
        return postSendHttp(url, null, null, param);
    }

    private static HttpResult postSendHttp(String url, String body, Gson postData, Map<String, Object> param) {
        if (StringUtil.isEmpty(url)) {
            return null;
        }
        long timer = 0L;
        try {
            String result = null;
            int ret = 0;
          HttpClient httpClient = null;
            if (url.indexOf("http://") >= 0) {
                httpClient = CustomHttpClient.getHttpClient();
            } else if (url.indexOf("https://") >= 0) {
                httpClient = CustomHttpClient.getHttpsClient();
            }
//            HttpClient httpClient = HttpUtils.getHttpClient();
            HttpPost post = new HttpPost(url);
            setHeader(post, new HashMap<String, String>());
//            post.setHeader("Accept", "application/html");
//            post.setHeader("Accept", "application/json");
//            post.setHeader("Content-Type", "text/html;charset=UTF-8");
//            post.setHeader("Authorization", auth);
            if (body != null) {
                StringEntity stringEntity = new StringEntity(body, HTTP.UTF_8);
                stringEntity.setContentEncoding(new BasicHeader(HTTP.CONTENT_ENCODING, HTTP.UTF_8));
                // 设置请求主体
                post.setEntity(stringEntity);
            } else if (postData != null) {
                // 设置参数
                StringEntity stringEntity = new StringEntity(postData.toString(), HTTP.UTF_8);
                stringEntity.setContentEncoding(new BasicHeader(HTTP.CONTENT_ENCODING, HTTP.UTF_8));
                post.setEntity(stringEntity);
            } else if (param != null) {
                setParam(post, param);
            }
            // 执行客户端请求
            timer = System.currentTimeMillis();
            HttpResponse response = httpClient.execute(post);
            ret = response.getStatusLine().getStatusCode();
            // 获取响应实体信息
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                result = EntityUtils.toString(entity, HTTP.UTF_8);
            }
            // 确保HTTP响应内容全部被读出或者内容流被关闭
            EntityUtils.consume(entity);
            if (ret != HttpStatus.SC_OK) {
                System.err.println("post url:" + url + " param:" + (param != null ? StringUtil.toString(param) : null) + " StatusCode:" + ret);
            }
            HttpResult httpResult = new HttpResult();
            httpResult.setStatus(ret);
            httpResult.setMessage(result);
            return httpResult;
        } catch (ConnectTimeoutException e) {
            System.err.println("post url:" + url + " param:" + (param != null ? StringUtil.toString(param) : null) + " " + e.getClass().getName() + " cost " + ((System.currentTimeMillis() - timer) / 1000D) + "s");
            e.printStackTrace();
        } catch (SocketTimeoutException e) {
            System.err.println("post url:" + url + " param:" + (param != null ? StringUtil.toString(param) : null) + " " + e.getClass().getName() + " cost " + ((System.currentTimeMillis() - timer) / 1000D) + "s");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("post url:" + url + " param:" + (param != null ? StringUtil.toString(param) : null) + " " + e.getClass().getName() + " cost " + ((System.currentTimeMillis() - timer) / 1000D) + "s");
            e.printStackTrace();
        }
        return null;
    }
    public static HttpResult postSendHttpWithHeader(String url,Map<String,String> header, Map<String, Object> param) {
        if (StringUtil.isEmpty(url)) {
            return null;
        }
        long timer = 0L;
        try {
            String result = null;
            int ret = 0;
            HttpClient httpClient = null;
            if (url.indexOf("http://") >= 0) {
                httpClient = CustomHttpClient.getHttpClient();
            } else if (url.indexOf("https://") >= 0) {
                httpClient = CustomHttpClient.getHttpsClient();
            }
//            HttpClient httpClient = HttpUtils.getHttpClient();
            HttpPost post = new HttpPost(url);
            setHeader(post, header);

            if (param != null) {
                setParam(post, param);
            }
            // 执行客户端请求
            timer = System.currentTimeMillis();
            HttpResponse response = httpClient.execute(post);
            ret = response.getStatusLine().getStatusCode();
            // 获取响应实体信息
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                result = EntityUtils.toString(entity, HTTP.UTF_8);
            }
            // 确保HTTP响应内容全部被读出或者内容流被关闭
            EntityUtils.consume(entity);
            if (ret != HttpStatus.SC_OK) {
                System.err.println("post url:" + url + " param:" + (param != null ? StringUtil.toString(param) : null) + " StatusCode:" + ret);
            }
            HttpResult httpResult = new HttpResult();
            httpResult.setStatus(ret);
            httpResult.setMessage(result);
            return httpResult;
        } catch (ConnectTimeoutException e) {
            System.err.println("post url:" + url + " param:" + (param != null ? StringUtil.toString(param) : null) + " " + e.getClass().getName() + " cost " + ((System.currentTimeMillis() - timer) / 1000D) + "s");
            e.printStackTrace();
        } catch (SocketTimeoutException e) {
            System.err.println("post url:" + url + " param:" + (param != null ? StringUtil.toString(param) : null) + " " + e.getClass().getName() + " cost " + ((System.currentTimeMillis() - timer) / 1000D) + "s");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("post url:" + url + " param:" + (param != null ? StringUtil.toString(param) : null) + " " + e.getClass().getName() + " cost " + ((System.currentTimeMillis() - timer) / 1000D) + "s");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 设置请求头
     *
     * @param base
     * @param header
     */
    private static void setHeader(HttpRequestBase base, Map<String, String> header) {
        if (header == null) {
            return;
        }
        Iterator<Map.Entry<String, String>> it = header.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            base.setHeader(entry.getKey(), entry.getValue());
        }
    }


    /**
     * 设置请求头
     *
     * @param base
     * @param param
     */
    private static void setParam(HttpEntityEnclosingRequestBase base, Map<String, Object> param) throws UnsupportedEncodingException {
        if (param == null) {
            return;
        }
        List<NameValuePair> pairs = new ArrayList<NameValuePair>(param.size());
        for (Map.Entry<String, Object> entry : param.entrySet()) {
            String value = StringUtil.toString(entry.getValue());
            if (value != null) {
                pairs.add(new BasicNameValuePair(entry.getKey(), value));
            }
        }
        if (pairs != null && pairs.size() > 0) {
            base.setEntity(new UrlEncodedFormEntity(pairs, "UTF-8"));
        }
    }

    /**
     * 功能描述：发送序列化对象
     *
     * @param url
     * @param inputObj
     * @return
     */
    public Object postSendHttp(String url, Object inputObj) {
        long start = System.currentTimeMillis();
        if (url == null || "".equals(url)) {
            System.out.println("request url is empty.");
            return null;
        }
        HttpClient httpClient = CustomHttpClient.getHttpClient();

        HttpPost post = new HttpPost(url);
        post.setHeader("Content-Type", "application/octet-stream");
        ByteArrayOutputStream bOut = new ByteArrayOutputStream(1024);
        InputStream bInput = null;
        ObjectOutputStream out = null;
        Serializable returnObj = null;
        try {
            out = new ObjectOutputStream(bOut);
            out.writeObject(inputObj);
            out.flush();
            out.close();
            out = null;
            bInput = new ByteArrayInputStream(bOut.toByteArray());
            InputStreamEntity inputStreamEntity = new InputStreamEntity(bInput, bOut.size(), null);
            inputStreamEntity.setContentEncoding(new BasicHeader(HTTP.CONTENT_ENCODING, HTTP.UTF_8));
            // 设置请求主体
            post.setEntity(inputStreamEntity);
            // 发起交易
            HttpResponse resp = httpClient.execute(post);
            System.out.println("请求[" + url + "] " + resp.getStatusLine());
            int ret = resp.getStatusLine().getStatusCode();
            if (ret == HttpStatus.SC_OK) {
                // // // 响应分析
                // HttpEntity entity = resp.getEntity();
                // returnObj = (Serializable)
                // SerializationUtils.deserialize(entity.getContent());
                // return returnObj;

                // 响应分析
                HttpEntity entity = resp.getEntity();

                InputStream in = entity.getContent();
                ObjectInputStream oInput = new ObjectInputStream(in);
                returnObj = (Serializable) oInput.readObject();
                oInput.close();
                oInput = null;
                long end = System.currentTimeMillis();
                System.out.println("请求[" + url + "]消耗时间 " + (end - start) + "毫秒");
                return returnObj;
            }
            return null;
        } catch (ConnectTimeoutException cte) {
            System.out.println(cte.getMessage());
            return null;
        } catch (SocketTimeoutException cte) {
            System.out.println(cte.getMessage());
            return null;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public static String getSendHttp(String url) {
        if (url == null || "".equals(url)) {
            System.out.println("request url is empty.");
            return null;
        }
        HttpClient httpClient = CustomHttpClient.getHttpClient();
        HttpGet get = new HttpGet(url);
        get.setHeader("Content-Type", "text/html;charset=UTF-8");
        try {
            // 发起交易
            HttpResponse resp = httpClient.execute(get);
            System.out.println("请求[" + url + "] " + resp.getStatusLine());
            int ret = resp.getStatusLine().getStatusCode();
            if (ret == HttpStatus.SC_OK) {
                // 响应分析
                HttpEntity entity = resp.getEntity();

                BufferedReader br = new BufferedReader(new InputStreamReader(entity.getContent()));
                StringBuffer responseString = new StringBuffer();
                String result = br.readLine();
                while (result != null) {
                    responseString.append(result);
                    result = br.readLine();
                }

                return responseString.toString();
            }
            return null;
        } catch (ConnectTimeoutException cte) {
            System.out.println(cte.getMessage());
            return null;
        } catch (SocketTimeoutException cte) {
            System.out.println(cte.getMessage());
            return null;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public String postPramaList(String url, NameValuePair[] list) {
        List<NameValuePair> nvList = new ArrayList<NameValuePair>();
        for (NameValuePair nameValue : list) {
            nvList.add(nameValue);
        }
        return postPramaList(nvList, url);
    }

    public String postPramaList(List<NameValuePair> list, String url) {
        HttpClient httpClient = CustomHttpClient.getHttpClient();
        HttpPost post = new HttpPost(url);
        post.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
        BufferedReader br = null;
        try {
            UrlEncodedFormEntity formEntiry = new UrlEncodedFormEntity(list, HTTP.UTF_8);
            // 设置请求参数
            post.setEntity(formEntiry);
            // 发起交易
            HttpResponse resp = httpClient.execute(post);
            System.out.println("请求[" + url + "] " + resp.getStatusLine());
            int ret = resp.getStatusLine().getStatusCode();
            if (ret == HttpStatus.SC_OK) {
                // 响应分析
                HttpEntity entity = resp.getEntity();
                br = new BufferedReader(new InputStreamReader(entity.getContent(), HTTP.UTF_8));
                StringBuffer responseString = new StringBuffer();
                String result = br.readLine();
                while (result != null) {
                    responseString.append(result);
                    result = br.readLine();
                }
                return responseString.toString();
            } else {
                System.out.println("retcode:" + ret);
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    // do nothing
                }
            }
        }
    }

}
