package com.gpnu.ai.config;

import com.gpnu.ai.rag.AICourseDocumentLoader;

import com.gpnu.ai.rag.MyTokenTextSplitter;
import jakarta.annotation.Resource;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.ai.vectorstore.redis.autoconfigure.RedisVectorStoreProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisClientConfig;
import redis.clients.jedis.JedisPooled;


import java.util.List;

@Configuration
public class AICourseAppVectorStoreConfig {


    @Resource
    private AICourseDocumentLoader aiCourseDocumentLoader;

    @Resource(name = "dashscopeEmbeddingModel")
    private EmbeddingModel dashscopeEmbeddingModel;

    @Resource
    private MyTokenTextSplitter myTokenTextSplitter;



    @Bean
    public SimpleVectorStore simpleVectorStore() {
        //这里的AI嵌入模型使用的是springAI官方的模型
        SimpleVectorStore simpleVectorStore = SimpleVectorStore.builder(dashscopeEmbeddingModel).build();
        return simpleVectorStore;
    }


    // 在你的配置类中添加
    @Bean
    public RedisVectorStoreProperties redisVectorStoreProperties() {
        RedisVectorStoreProperties properties = new RedisVectorStoreProperties();
        properties.setIndexName("Intelli-Edu-index");
        properties.setPrefix("Intelli-Edu-prefix");
        properties.setInitializeSchema(true);
        return properties;
    }


    @Bean
    public RedisVectorStore redisVectorStore(
            @Value("${spring.data.redis.host}") String redisHost,
            @Value("${spring.data.redis.port}") int redisPort,
            @Value("${spring.data.redis.password}") String redisPassword,
            @Value("${spring.data.redis.database}") int redisDatabase,
            RedisVectorStoreProperties properties
    ) {
        JedisClientConfig clientConfig = DefaultJedisClientConfig.builder()
                .password(redisPassword)
                .database(redisDatabase)
                .build();

        JedisPooled jedisPooled = new JedisPooled(new HostAndPort(redisHost, redisPort), clientConfig);

        RedisVectorStore redisVectorStore =
                RedisVectorStore.builder(jedisPooled, dashscopeEmbeddingModel)
                .metadataFields(RedisVectorStore.MetadataField.tag("filename"))
                .indexName(properties.getIndexName())
                .prefix(properties.getPrefix())
                .initializeSchema(properties.isInitializeSchema())
                .build();
        return redisVectorStore;
    }



}



//Rag的ETL流程
// 加载文档
//        List<Document> documents = aiCourseDocumentLoader.loadMarkdowns() ;
//        //对文档进行切分
//        List<Document> mySplitDocument = myTokenTextSplitter.splitDocuments(documents);
//        redisVectorStore.add(mySplitDocument);