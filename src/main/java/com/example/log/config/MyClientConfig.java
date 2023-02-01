package com.example.log.config;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
import org.springframework.http.HttpHeaders;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class MyClientConfig extends AbstractElasticsearchConfiguration {

    @Override
    @Bean
    public RestHighLevelClient elasticsearchClient() {
//客户端配置
        HttpHeaders titleHeaders = new HttpHeaders();
        titleHeaders.add("some-header","hello word");

        final ClientConfiguration config = ClientConfiguration.builder()
                .connectedTo("10.8.18.175:31480")  //连接es集群，多个用逗号分隔
//                .usingSsl()  // 是否启用ssl
                .withConnectTimeout(Duration.ofSeconds(5))
                .withSocketTimeout(Duration.ofSeconds(3))
                .withDefaultHeaders(titleHeaders)
                .withBasicAuth("elastic","es123456")  // 认证
                .withHeaders(()->{
                    HttpHeaders httpHeaders = new HttpHeaders();
                    httpHeaders.add("currentTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                    return httpHeaders;
                })
                .withClientConfigurer(RestClients.RestClientConfigurationCallback.from(httpAsyncClientBuilder ->
                {return httpAsyncClientBuilder;}))  // 设置非反应式客户端功能
                .build();
        return RestClients.create(config).rest();
    }
}
