package com.samsungdisplay.student.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;

@Configuration
public class ElasticsearchConfig {

    @Bean
    public ElasticsearchClient elasticsearchClient() {
        JacksonJsonpMapper jacksonJsonpMapper = new JacksonJsonpMapper(
                new ObjectMapper().registerModule(new JavaTimeModule()) // ElasticsearchClient에 JavaTimeModule 적용
        );

        // Elasticsearch client 초기화
        return new ElasticsearchClient(new RestClientTransport(
                RestClient.builder(new HttpHost("localhost", 9200, "http")).build(),
                jacksonJsonpMapper
        ));
    }
}