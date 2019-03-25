package com.houbank.utils;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.log4j.Log4j2;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.net.URI;
import java.util.*;
import java.util.Map.Entry;

/**
 * 使用HttpClient发送请求、接收响应很简单，一般需要如下几步即可： 1:创建CloseableHttpClient对象。
 * 2:创建请求方法的实例，并指定请求URL。如果需要发送GET请求，创建HttpGet对象；如果需要发送POST请求，创建HttpPost对象。
 * 3:如果需要发送请求参数，可可调用setEntity(HttpEntity
 * entity)方法来设置请求参数。setParams方法已过时（4.4.1版本）。
 * 4:调用HttpGet、HttpPost对象的setHeader(String name, String value)方法设置header信息，
 * 或者调用setHeaders(Header[] headers)设置一组header信息。
 * 5:调用CloseableHttpClient对象的execute(HttpUriRequest
 * request)发送请求，该方法返回一个CloseableHttpResponse。
 * 6:调用HttpResponse的getEntity()方法可获取HttpEntity对象，该对象包装了服务器的响应内容。
 * 程序可通过该对象获取服务器的响应内容；调用CloseableHttpResponse的getAllHeaders()、getHeaders(String
 * name)等方法可获取服务器的响应头。 7:释放连接。无论执行方法是否成功，都必须释放连接
 **/

@Log4j2
public class SendRequestHeader {
    static CloseableHttpClient httpClient = HttpClients.createDefault();
    static CloseableHttpResponse response = null;
    static HttpGet httpGet = new HttpGet();
    static HttpPost httpPost = new HttpPost();


    // 普通get
    public static String httpCommonGet(String url, HashMap<String, String> headers) throws Exception {
        httpGet.setURI(new URI(url));
        Iterator<Entry<String, String>> iter = headers.entrySet().iterator();
        while (iter.hasNext()) {
            Entry entry = (Entry) iter.next();
            Object key = entry.getKey();
            Object val = entry.getValue();
            httpGet.setHeader(key.toString(), val.toString());
        }
        response = httpClient.execute(httpGet);
        // 如果server端返回http 200
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity.getContentLength() == 0) {
                throw new Exception(url + "返回为空");
            }
            return EntityUtils.toString(responseEntity, "UTF-8");
        } else if (response.getStatusLine().getStatusCode() == 302) {
            return response.getFirstHeader("Location").toString();
        } else {
            log.error("请求失败 : " + response.getStatusLine().getStatusCode());
            throw new Exception(url + "状态码:" + String.valueOf(response.getStatusLine().getStatusCode()));

        }

    }


    // post---form data
    public static String httpCommonPost(String url, HashMap<String, String> requsetParams,
                                        HashMap<String, String> headers) throws Exception {
        httpPost.setURI(new URI(url));
        // headers装填
        Iterator<Entry<String, String>> iter = headers.entrySet().iterator();
        while (iter.hasNext()) {
            Entry entry = (Entry) iter.next();
            Object key = entry.getKey();
            Object val = entry.getValue();
            httpPost.setHeader(key.toString(), val.toString());
        }

        // 装填参数
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        if (null != requsetParams) {
            // 对request中的value转换
            Iterator<Entry<String, String>> requestIte = requsetParams.entrySet().iterator();
            while (requestIte.hasNext()) {
                Entry<String, String> requestEntry = requestIte.next();
                requestEntry.setValue(requestEntry.getValue());
            }
            Set<String> keys = requsetParams.keySet();
            for (String key : keys) {
                params.add(new BasicNameValuePair(key, requsetParams.get(key)));
            }
        }
        if (null != requsetParams) {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "utf-8");
            httpPost.setEntity(entity);
        }
        // 执行请求操作，并拿到结果（同步阻塞）
        response = httpClient.execute(httpPost);
        // 如果server端返回http 200
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity.getContentLength() == 0) {
                throw new Exception(url + "返回为空");
            }
            return EntityUtils.toString(responseEntity, "UTF-8");
        } else if (response.getStatusLine().getStatusCode() == 302) {
            return response.getFirstHeader("Location").toString();
        } else {
            log.error("请求失败 : " + response.getStatusLine().getStatusCode());
            throw new Exception(url + "状态码:" + String.valueOf(response.getStatusLine().getStatusCode()));
        }
    }

    // post x-www-form-urlencoded
    public static String httpXwwwPost(String url, HashMap<String, String> requsetParams,
    		HashMap<String, String> headers) throws Exception {
        httpPost.setURI(new URI(url));
        // headers装填
        Iterator<Entry<String, String>> iter = headers.entrySet().iterator();
        while (iter.hasNext()) {
            Entry entry = (Entry) iter.next();
            Object key = entry.getKey();
            Object val = entry.getValue();
            httpPost.setHeader(key.toString(), val.toString());
        }
        // 装填参数
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        if (requsetParams != null) {
            for (Entry<String, String> entry : requsetParams.entrySet()) {
                nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
        }
        // 设置参数到请求对象中
        httpPost.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));
        System.out.println("请求地址：" + url);
        System.out.println("请求参数：" + nvps.toString());

        // 执行请求操作，并拿到结果（同步阻塞）
        response = httpClient.execute(httpPost);
        // 如果server端返回http 200
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity.getContentLength() == 0) {
                throw new Exception(url + "返回为空");
            }
            return EntityUtils.toString(responseEntity, "UTF-8");
        } else if (response.getStatusLine().getStatusCode() == 302) {
            return response.getFirstHeader("Location").toString();

        } else {
            log.error("请求失败 : " + response.getStatusLine().getStatusCode());
            throw new Exception(url + "状态码:" + String.valueOf(response.getStatusLine().getStatusCode()));
        }
    }

    // post json数据
    public static String httpJsonPost(String url, JSONObject jsonObj, HashMap<String,String> headers)
            throws Exception {
        httpPost.setURI(new URI(url));
        Iterator<Entry<String, String>> iter = headers.entrySet().iterator();
        while (iter.hasNext()) {
            Entry entry = (Entry) iter.next();
            Object key = entry.getKey();
            Object val = entry.getValue();
            httpPost.setHeader(key.toString(), val.toString());
        }
        StringEntity strEntity = new StringEntity(JSON.toJSONString(jsonObj), "utf-8");
        strEntity.setContentType("application/json");
        strEntity.setContentEncoding("UTF-8");
        httpPost.setEntity(strEntity);

        response = httpClient.execute(httpPost);

        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity.getContentLength() == 0) {
                throw new Exception(url + "返回为空");
            }
            return EntityUtils.toString(responseEntity, "UTF-8");
        } else if (response.getStatusLine().getStatusCode() == 302) {
            return response.getFirstHeader("Location").toString();

        } else {
            log.error("请求失败 : " + response.getStatusLine().getStatusCode());
            throw new Exception(url + "状态码:" + String.valueOf(response.getStatusLine().getStatusCode()));
        }
    }
 // post json数据
    public static String httpJsonPostForKeepAliveDuration(String url, JSONObject jsonObj, HashMap<String, String> headers)
            throws Exception {
        httpPost.setURI(new URI(url));
        Iterator<Entry<String, String>> iter = headers.entrySet().iterator();
        while (iter.hasNext()) {
            Entry entry = (Entry) iter.next();
            Object key = entry.getKey();
            Object val = entry.getValue();
            httpPost.setHeader(key.toString(), val.toString());
        }
        StringEntity strEntity = new StringEntity(JSON.toJSONString(jsonObj), "utf-8");
        strEntity.setContentEncoding("UTF-8");
//        strEntity.setContentType("application/json");
        httpPost.setEntity(strEntity);

        response = httpClient.execute(httpPost);

        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity.getContentLength() == 0) {
                throw new Exception(url + "返回为空");
            }
            return EntityUtils.toString(responseEntity, "UTF-8");
        } else if (response.getStatusLine().getStatusCode() == 302) {
            return response.getFirstHeader("Location").toString();

        } else {
            log.error("请求失败 : " + response.getStatusLine().getStatusCode());
            throw new Exception(url + "状态码:" + String.valueOf(response.getStatusLine().getStatusCode()));
        }

    }
    // post json数据
    public static String httpJsonPostString(String url,JSONObject jsonObj,HashMap<String, String> headers)
            throws Exception {
       ResponseHandler<String> responseHandler = new BasicResponseHandler();

        httpPost.setURI(new URI(url));
        Iterator<Entry<String, String>> iter = headers.entrySet().iterator();
        while (iter.hasNext()) {
            Entry entry = (Entry) iter.next();
            Object key = entry.getKey();
            Object val = entry.getValue();
            httpPost.setHeader(key.toString(), val.toString());
        }
        StringEntity strEntity = new StringEntity(jsonObj.toJSONString(), "utf-8");

        strEntity.setContentType("application/json");
        httpPost.setEntity(strEntity);

        String response2 =  httpClient.execute(httpPost, responseHandler);
        return response2;

//        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
//            HttpEntity responseEntity = response.getEntity();
//            if (responseEntity.getContentLength() == 0) {
//                throw new Exception(url + "返回为空");
//            }
//            return EntityUtils.toString(responseEntity, "UTF-8");
//        } else if (response.getStatusLine().getStatusCode() == 302) {
//            return response.getFirstHeader("Location").toString();
//
//        } else {
//            throw new Exception(url + "状态码:" + String.valueOf(response.getStatusLine().getStatusCode()));
//        }

    }

    // post json数据
    public static String httpJsonPostA(String url, JSONObject jsonObj, HashMap<String, String> headers)
            throws Exception {
        httpPost.setURI(new URI(url));
        Iterator<Entry<String, String>> iter = headers.entrySet().iterator();
        while (iter.hasNext()) {
            Entry entry = (Entry) iter.next();
            Object key = entry.getKey();
            Object val = entry.getValue();
            httpPost.setHeader(key.toString(), val.toString());
        }
        Header headers1[] = httpPost.getAllHeaders();
        for (Header header : headers1) {
            System.out.println(header.getName() + "  " + header.getValue());
        }
        StringEntity strEntity = new StringEntity(jsonObj.toString(), "utf-8");
        strEntity.setContentType("application/json;charset=UTF-8");
        httpPost.setEntity(strEntity);
        response = httpClient.execute(httpPost);

        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity.getContentLength() == 0) {
                throw new Exception(url + "返回为空");
            }
            return EntityUtils.toString(responseEntity, "UTF-8");
        } else if (response.getStatusLine().getStatusCode() == 302) {
            return response.getFirstHeader("Location").toString();

        } else {
            log.error("请求失败 : " + response.getStatusLine().getStatusCode());
            throw new Exception(url + "状态码:" + String.valueOf(response.getStatusLine().getStatusCode()));
        }

    }


}

