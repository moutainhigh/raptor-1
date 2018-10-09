package com.mo9.raptor.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Created by jyou on 2018/3/22.
 * @author jyou
 */
@Configuration
public class RedisConfig {

    /**
     * Redis操作模板
     */
    @Bean(value = "raptorRedis")
    public RedisTemplate<String, Object> gluttonRedisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        setTemplateParams(template);
        return template;
    }

    private void setTemplateParams(RedisTemplate<String, Object> template){
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashKeySerializer(template.getKeySerializer());
        template.setHashValueSerializer(template.getValueSerializer());
    }

}
