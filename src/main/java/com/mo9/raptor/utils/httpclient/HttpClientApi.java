package com.mo9.raptor.utils.httpclient;

import com.mo9.libracredit.util.httpclient.bean.HttpResult;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by jyou on 2017/8/17.
 *
 * httpclient封装通用类
 */
@Component("httpClientApi")
public class HttpClientApi implements BeanFactoryAware {

    private Logger logger = LoggerFactory.getLogger(HttpClientApi.class);

    @Autowired(required = false)
    private RequestConfig requestConfig;

    private BeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    /**
     * 执行GET请求，如果响应200返回响应的内容，如果非200返回null
     */
    public String doGet(String url) throws ClientProtocolException, IOException {
        // 创建http GET请求
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(requestConfig);
        CloseableHttpResponse response = null;
        try {
            // 执行请求
            response = getHttpClient().execute(httpGet);
            // 判断返回状态是否为200
            if (response.getStatusLine().getStatusCode() == 200) {
                return EntityUtils.toString(response.getEntity(), "UTF-8");
            }
        }catch (Exception e){
            logger.error("httpClientApi发送请求出现异常response={}",response, e);
            throw e;
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return null;
    }

    /**
     * 执行GET请求，可添加头信息，如果响应200返回响应的内容，如果非200返回null
     */
    public String doGetByHeader(String url, Map<String, String> headers) throws ClientProtocolException, IOException {
        // 创建http GET请求
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(requestConfig);
        if(headers != null){
            Set<String> set = headers.keySet();
            for(String key : set){
                httpGet.addHeader(key, headers.get(key));
            }
        }
        CloseableHttpResponse response = null;
        try {
            // 执行请求
            response = getHttpClient().execute(httpGet);
            // 判断返回状态是否为200
            if (response.getStatusLine().getStatusCode() == 200) {
                return EntityUtils.toString(response.getEntity(), "UTF-8");
            }
        } catch (Exception e){
            logger.error("httpClientApi发送请求出现异常response={}",response, e);
            throw e;
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return null;
    }

    /**
     * 执行GET请求，可添加参数和头信息，如果响应200返回响应的内容，如果非200返回null
     *
     * @param url
     * @param params
     * @param headers 头信息
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     * @throws URISyntaxException
     */
    public String doGet(String url, Map<String, String> params, Map<String, String> headers) throws ClientProtocolException, IOException,
            URISyntaxException {
        // 定义请求的参数
        URIBuilder builder = new URIBuilder(url);
        for (Map.Entry<String, String> entry : params.entrySet()) {
            builder.setParameter(entry.getKey(), entry.getValue());
        }
        return this.doGetByHeader(builder.build().toString(), headers);
    }

    /**
     * 执行GET请求，可添加参数，如果响应200返回响应的内容，如果非200返回null
     * @param url
     * @param params
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     * @throws URISyntaxException
     */
    public String doGet(String url, Map<String, String> params) throws ClientProtocolException, IOException,
            URISyntaxException {
        // 定义请求的参数
        URIBuilder builder = new URIBuilder(url);
        for (Map.Entry<String, String> entry : params.entrySet()) {
            builder.setParameter(entry.getKey(), entry.getValue());
        }
        return this.doGet(builder.build().toString());
    }

    /**
     * 带有参数的PSOT请求
     *
     * @param url
     * @param params
     * @return
     * @throws IOException
     */
    public HttpResult doPost(String url, Map<String, String> params) throws IOException {
        // 创建http POST请求
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);
        if (null != params) {
            // 设置post参数
            List<NameValuePair> parameters = new ArrayList<NameValuePair>(0);
            for (Map.Entry<String, String> entry : params.entrySet()) {
                parameters.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            // 构造一个form表单式的实体
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(parameters);
            // 将请求实体设置到httpPost对象中
            httpPost.setEntity(formEntity);
        }
        CloseableHttpResponse response = null;
        try {
            // 执行请求
            response = getHttpClient().execute(httpPost);
            HttpEntity httpEntity = response.getEntity();
            String data = null;
            if (null != httpEntity) {
                data = EntityUtils.toString(httpEntity, "UTF-8");
            }
            return new HttpResult(response.getStatusLine().getStatusCode(), data);
        } catch (Exception e){
            logger.error("httpClientApi发送请求出现异常response={}",response, e);
            throw e;
        }finally {
            if (response != null) {
                response.close();
            }
        }
    }

    /**
     * POST提交json数据
     *
     * @param url
     * @param json
     * @return
     * @throws IOException
     */
    public HttpResult doPostJson(String url, String json) throws IOException {
        // 创建http POST请求
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);
        if (null != json) {
            StringEntity stringEntity = new StringEntity(json, ContentType.APPLICATION_JSON);
            // 将请求实体设置到httpPost对象中
            httpPost.setEntity(stringEntity);
        }
        CloseableHttpResponse response = null;
        try {
            // 执行请求
            response = getHttpClient().execute(httpPost);
            HttpEntity httpEntity = response.getEntity();
            String data = null;
            if (null != httpEntity) {
                data = EntityUtils.toString(httpEntity, "UTF-8");
            }
            return new HttpResult(response.getStatusLine().getStatusCode(), data);
        }catch (Exception e){
            logger.error("httpClientApi发送请求出现异常response={}",response, e);
            throw e;
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    public HttpResult doPostJson(String url, String json, Map<String,String> headers) throws IOException {
        // 创建http POST请求
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);

        if(headers != null){
            Set<String> set = headers.keySet();
            for(String key : set){
                httpPost.addHeader(key, headers.get(key));
            }
        }

        if (null != json) {
            StringEntity stringEntity = new StringEntity(json, ContentType.APPLICATION_JSON);
            // 将请求实体设置到httpPost对象中
            httpPost.setEntity(stringEntity);
        }
        CloseableHttpResponse response = null;
        try {
            // 执行请求
            response = getHttpClient().execute(httpPost);
            HttpEntity httpEntity = response.getEntity();
            String data = null;
            if (null != httpEntity) {
                data = EntityUtils.toString(httpEntity, "UTF-8");
            }
            return new HttpResult(response.getStatusLine().getStatusCode(), data);
        }catch (Exception e){
            logger.error("httpClientApi发送请求出现异常response={}",response, e);
            throw e;
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    /**
     * 没有参数的post请求
     *
     * @param url
     * @return
     * @throws IOException
     */
    public HttpResult doPost(String url) throws IOException {
        return this.doPost(url, null);
    }


    /**
     * 下载文件
     *
     * @param url
     * @param filePath
     *
     * 建议调用该方法时异步调用
     */
    public boolean download(final String url, final String filePath) throws IOException {
        httpDownloadFile(url, filePath);
        return true;
    }

    /**
     * 下载文件
     *
     * @param url
     * @param filePath 下载到的地址路径,包含文件名
     */
    private void httpDownloadFile(String url, String filePath) throws IOException {
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(requestConfig);
        CloseableHttpResponse response = null;
        try {
            response = getHttpClient().execute(httpGet);
            HttpEntity httpEntity = response.getEntity();
            InputStream is = httpEntity.getContent();
            // 根据InputStream 下载文件
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int r = 0;
            while ((r = is.read(buffer)) > 0) {
                output.write(buffer, 0, r);
            }
            FileOutputStream fos = new FileOutputStream(filePath);
            output.writeTo(fos);
            output.flush();
            output.close();
            fos.close();
        } catch (Exception e) {
            logger.error("httpClientApi发送请求出现异常response={}",response, e);
            throw e;
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }
    private CloseableHttpClient getHttpClient() {
        return this.beanFactory.getBean(CloseableHttpClient.class);
    }
}