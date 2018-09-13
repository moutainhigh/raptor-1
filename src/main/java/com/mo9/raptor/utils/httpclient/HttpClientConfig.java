package com.mo9.raptor.utils.httpclient;

import com.mo9.raptor.utils.httpclient.bean.IdleConnectionEvictor;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * Created by jyou on 2017/9/13.
 *
 * httpClient详细配置
 */
@Configuration
public class HttpClientConfig {
    @Autowired
    private HttpClientProperties httpClientProperties;

    /**
     * 定义连接管理器,也就是httpclient的连接池
     */
    @Bean
    public PoolingHttpClientConnectionManager poolingHttpClientConnectionManager(){
        PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager();
        manager.setMaxTotal(httpClientProperties.getMaxTotal());
        manager.setDefaultMaxPerRoute(httpClientProperties.getDefaultMaxPerRoute());
        return manager;
    }

    /**
     * 配置构建器httpClientBuilder,用于构建CloseableHttpClient对象也就是httpclient)
     */
    @Bean
    public HttpClientBuilder httpClientBuilder(PoolingHttpClientConnectionManager manager){
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        httpClientBuilder.setConnectionManager(manager);
        return httpClientBuilder;
    }

    /**
     * 创建httpclient 对象,每一个请求需要对应一个单独的httpclient,因此需要httpclient是多例
     */
    @Bean
    @Scope(value = "prototype")
    public CloseableHttpClient closeableHttpClient(HttpClientBuilder httpClientBuilder){
        CloseableHttpClient closeableHttpClient = httpClientBuilder.build();
        return closeableHttpClient;
    }

    /**
     * 配置请求参数构建器,设置一些连接信息,参数配置到外部配置文件中
     */
    @Bean
    public RequestConfig.Builder getBuilder(){
        RequestConfig.Builder builder = RequestConfig.custom();
        builder.setConnectTimeout(httpClientProperties.getTimeout());
        builder.setConnectionRequestTimeout(httpClientProperties.getRequestTimeout());
        builder.setSocketTimeout(httpClientProperties.getSocketTimeout());
        builder.setStaleConnectionCheckEnabled(httpClientProperties.getStaleConnectionCheckEnabled());
        return builder;
    }

    /**
     * 配置请求参数对象
     * @param builder
     * @return
     */
    @Bean
    public RequestConfig getRequestConfig(RequestConfig.Builder builder){
        return builder.build();
    }

    /**
     * 定期关闭无效链接
     */
    @Bean
    public IdleConnectionEvictor idleConnectionEvictor(PoolingHttpClientConnectionManager manager){
        IdleConnectionEvictor idleConnectionEvictor = new IdleConnectionEvictor(manager);
        return idleConnectionEvictor;
    }
}
