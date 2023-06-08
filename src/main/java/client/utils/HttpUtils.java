package client.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.*;

public class HttpUtils {


    /**
     * get
     *
     * @return
     */
    public static String doGet(String url) {
        try {
            HttpClient client = HttpClients.createDefault();
            HttpGet request = new HttpGet(url);
            HttpResponse response = client.execute(request);

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String strResult = EntityUtils.toString(response.getEntity());

                return strResult;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * post request (for parameters in key-value format)
     *
     * @param url
     * @param params
     * @return
     */
    public static String doPost(String url, Map params) {

        BufferedReader in = null;
        try {
            HttpClient client = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost();
            httpPost.setHeader("Accept", "text/html, application/xhtml+xml, */*");
            httpPost.setHeader("Content-Type", "text/xml");
            httpPost.setURI(new URI(url));

            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            for (Iterator iter = params.keySet().iterator(); iter.hasNext(); ) {
                String name = (String) iter.next();
                String value = String.valueOf(params.get(name));
                nvps.add(new BasicNameValuePair(name, value));

                //System.out.println(name +"-"+value);
            }
            httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

            HttpResponse response = client.execute(httpPost);
            int code = response.getStatusLine().getStatusCode();
            if (code == 200) {
                in = new BufferedReader(new InputStreamReader(response.getEntity()
                        .getContent(), "utf-8"));
                StringBuffer sb = new StringBuffer("");
                String line = "";
                String NL = System.getProperty("line.separator");
                while ((line = in.readLine()) != null) {
                    sb.append(line + NL);
                }

                in.close();

                return sb.toString();
            } else {    //
                System.out.println("状态码：" + code);
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    /**
     * post request (for requesting parameters in json format)
     *
     * @param url
     * @param params
     * @return
     */
    public static String doPostWithJson(String url, String params, String source) {
        try{
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost();
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("cli-os", source);
            httpPost.addHeader("app-version", "10");
            httpPost.addHeader("api-version", "1.3.10");
            String requestTime = String.valueOf(new Date().getTime() / 1000);
            httpPost.addHeader("request-Time", requestTime);


            String signature = hasValidAndroidSignature(url, requestTime);
            httpPost.setURI(new URI(url+"?signature="+signature));
            String charSet = "UTF-8";
            StringEntity entity = new StringEntity(params, charSet);
            httpPost.setEntity(entity);
            CloseableHttpResponse response = null;

            response = httpclient.execute(httpPost);
            StatusLine status = response.getStatusLine();
            int state = status.getStatusCode();
            if (state == HttpStatus.SC_OK) {
                HttpEntity responseEntity = response.getEntity();
                String jsonString = EntityUtils.toString(responseEntity);
                return jsonString;
            } else {
                System.out.println("request return:" + state + "(" + url + ")");
            }
            if (response != null) {
                response.close();
            }
            httpclient.close();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

        return null;
    }


    /**
     * post request (for requesting parameters in json format)
     *
     * @param url
     * @param params
     * @return
     */
    @ResponseBody
    public static Object doLeXinPost(String url, Map params, HttpServletRequest request) throws Exception {
        CloseableHttpResponse response = post(url, params, request);
        StatusLine status = response.getStatusLine();
        int state = status.getStatusCode();
        HttpEntity responseEntity = response.getEntity();
        JSONObject json = JSONObject.parseObject(EntityUtils.toString(responseEntity, "UTF-8"));
        if (state == HttpStatus.SC_OK) {
            json.put("lgi_token", params.get("password"));
            return  json;
        } else {
            System.out.println("request return:" + state + "(" + url + ")");
            throw new HttpException( json.toJSONString());
        }
    }


    /**
     * post请求（用于请求json格式的参数）
     *
     * @param url
     * @param params
     * @return
     */
    @ResponseBody
    public static Object doLeXinPost(String url, Map params) throws Exception {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        return doLeXinPost(url, params, request);
    }




    /**
     * Description: post
     * @Author: Claire
     * @param url
     * @param params
     * @param request
     * @date: 2019-06-11
     * @return: org.apache.http.client.methods.CloseableHttpResponse
     */
    public static CloseableHttpResponse post(String url, Map params, HttpServletRequest request) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);

        Enumeration<String> headerNames = request.getHeaderNames();

        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerVal = request.getHeader(headerName);
            httpPost.setHeader(headerName, headerVal);
        }
        httpPost.removeHeaders("Content-Length");
        httpPost.setHeader("content-type", "application/json");
        if(httpPost.getURI().getPort()>0) {
            httpPost.setHeader("host", httpPost.getURI().getHost() + ":" + httpPost.getURI().getPort());
        }else {
            httpPost.setHeader("host", httpPost.getURI().getHost());
        }
        params.put("signature", SignUtils.createOldSign(url, params, request));

        String stringParam = JSONObject.toJSONString(params);
        StringEntity entity = new StringEntity(stringParam, "UTF-8");
        httpPost.setEntity(entity);
        CloseableHttpResponse response = null;

        response = httpclient.execute(httpPost);
        return response;
    }


    /**
     * Description: post
     * @Author: Claire
     * @param url
     * @param params
     * @param request
     * @date: 2019-06-11
     * @return: org.apache.http.client.methods.CloseableHttpResponse
     */
    public static CloseableHttpResponse postNotType(String url, Map params, HttpServletRequest request) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);

        Enumeration<String> headerNames = request.getHeaderNames();

        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerVal = request.getHeader(headerName);
            httpPost.setHeader(headerName, headerVal);
        }
        httpPost.removeHeaders("Content-Length");
        httpPost.setHeader("content-type", "application/json");
        if(httpPost.getURI().getPort()>0) {
            httpPost.setHeader("host", httpPost.getURI().getHost() + ":" + httpPost.getURI().getPort());
        }else {
            httpPost.setHeader("host", httpPost.getURI().getHost());
        }
        params.put("signature", SignUtils.createOldSignNotType(url, params, request));

        String stringParam = JSONObject.toJSONString(params);
        StringEntity entity = new StringEntity(stringParam, "UTF-8");
        httpPost.setEntity(entity);
        CloseableHttpResponse response = null;

        response = httpclient.execute(httpPost);
        return response;
    }


    /**
     * Description: patch
     * @Author: Claire
     * @param url
     * @param params
     * @param request
     * @date: 2019-06-11
     * @return: org.apache.http.client.methods.CloseableHttpResponse
     */
    public static CloseableHttpResponse patch(String url, Map params, HttpServletRequest request) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPatch httpPatch = new HttpPatch(url);

        Enumeration<String> headerNames = request.getHeaderNames();

        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerVal = request.getHeader(headerName);
            httpPatch.setHeader(headerName, headerVal);
        }
        httpPatch.removeHeaders("Content-Length");
        httpPatch.setHeader("content-type", "application/json");
        if(httpPatch.getURI().getPort()>0) {
            httpPatch.setHeader("host", httpPatch.getURI().getHost() + ":" + httpPatch.getURI().getPort());
        }else {
            httpPatch.setHeader("host", httpPatch.getURI().getHost());
        }
        params.put("signature", SignUtils.createOldSign(url, params, request));

        String stringParam = JSONObject.toJSONString(params);
        StringEntity entity = new StringEntity(stringParam, "UTF-8");
        httpPatch.setEntity(entity);
        CloseableHttpResponse response = null;

        response = httpclient.execute(httpPatch);
        return response;
    }


    /**
     * Description: delete
     * @Author: Claire
     * @param url
     * @param params
     * @param request
     * @date: 2019-06-11
     * @return: org.apache.http.client.methods.CloseableHttpResponse
     */
    public static CloseableHttpResponse delete(String url, Map params, HttpServletRequest request) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpDeleteWithBody httpDelete = new HttpDeleteWithBody(url);

        Enumeration<String> headerNames = request.getHeaderNames();

        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerVal = request.getHeader(headerName);
            httpDelete.setHeader(headerName, headerVal);
        }
        httpDelete.removeHeaders("Content-Length");
        httpDelete.setHeader("Content-Type", "application/json");
        if(httpDelete.getURI().getPort()>0) {
            httpDelete.setHeader("host", httpDelete.getURI().getHost() + ":" + httpDelete.getURI().getPort());
        }else {
            httpDelete.setHeader("host", httpDelete.getURI().getHost());
        }
        params.put("signature", SignUtils.createOldSign(url, params, request));

        String stringParam = JSONObject.toJSONString(params);
        StringEntity entity = new StringEntity(stringParam, "UTF-8");
        httpDelete.setEntity(entity);
        CloseableHttpResponse response = null;

        response = httpclient.execute(httpDelete);
        return response;
    }


    /**
     * Description: post
     * @Author: Claire
     * @param url
     * @param params
     * @date: 2019-06-11
     * @return: org.apache.http.client.methods.CloseableHttpResponse
     */
    public static CloseableHttpResponse testPost(String url, Map params) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);

        httpPost.addHeader("Content-Type", "application/json;charset=UTF-8");
                //.addHeader("Authorization", "Bearer " + (RetrofitUtil.getMyToken() == null ? "" : RetrofitUtil.getMyToken()))
        httpPost.addHeader("cli-os", "android");
                //.addHeader("cli-os-version", Build.VERSION.RELEASE + "_" + Build.MODEL)
        httpPost.addHeader("app-version", "1.5.0");
        httpPost.addHeader("api-version", "1.3.10");
        String requestTime = String.valueOf(new Date().getTime() / 1000);
        httpPost.addHeader("request-Time", requestTime);


        String signature = hasValidAndroidSignature(url, requestTime);

        httpPost.removeHeaders("Content-Length");
        httpPost.setHeader("content-type", "application/json");
        if(httpPost.getURI().getPort()>0) {
            httpPost.setHeader("host", httpPost.getURI().getHost() + ":" + httpPost.getURI().getPort());
        }else {
            httpPost.setHeader("host", httpPost.getURI().getHost());
        }

        String stringParam = JSONObject.toJSONString(params);
        StringEntity entity = new StringEntity(stringParam, "UTF-8");
        httpPost.setEntity(entity);
        CloseableHttpResponse response = null;

        response = httpclient.execute(httpPost);
        return response;
    }


    public static String hasValidAndroidSignature(String url, String requestTime) {
        //Encryption is a secret
        String original = "Encryption is a secret";
        System.out.println(original);
        String newSignature = hashHmac("HmacSHA256", original, "android");
        return newSignature;
    }


    private static String hashHmac(String macT, String data, String key) {
        try {
            Mac mac = Mac.getInstance(macT);
            SecretKeySpec secret = new SecretKeySpec(key.getBytes("UTF-8"), mac.getAlgorithm());
            mac.init(secret);
            return SignUtils.byteArrayToHexString(mac.doFinal(data.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * post request (for requesting parameters in json format)
     *
     * @param url
     * @param params
     * @return
     */
    @ResponseBody
    public static Object doTestPost(String url, Map params) throws Exception {
        CloseableHttpResponse response = testPost(url, params);
        StatusLine status = response.getStatusLine();
        int state = status.getStatusCode();
        HttpEntity responseEntity = response.getEntity();
        JSONObject json = JSONObject.parseObject(EntityUtils.toString(responseEntity, "UTF-8"));
        if (state == HttpStatus.SC_OK) {
            json.put("lgi_token", params.get("password"));
            return  json;
        } else {
            System.out.println("request return:" + state + "(" + url + ")");
            throw new HttpException( json.toJSONString());
        }
    }

}