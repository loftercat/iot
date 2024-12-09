package com.fujica.config;
import cn.hutool.core.convert.Convert;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fujica.util.RedisSingleUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@Slf4j
public class RedisConfig {

    @Autowired
    private Environment environment;

    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
    private String port;
    @Value("${spring.redis.password}")
    private String password;
    @Value("${spring.redis.jedis.pool.max-active}")
    private String max_active;
    @Value("${spring.redis.jedis.pool.max-idle}")
    private String max_idle;
    @Value("${spring.redis.jedis.pool.max-wait}")
    private String max_wait;
    @Value("${spring.redis.jedis.pool.min-idle}")
    private String min_idle;


    @Bean("redisSinglePool")
    public GenericObjectPoolConfig redisSinglePool() {
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMinIdle(Convert.toInt(min_idle));
        config.setMaxIdle(Convert.toInt(max_idle));
        config.setMaxTotal(Convert.toInt(max_active));
        config.setMaxWaitMillis(Convert.toInt(max_wait));
        return config;
    }

    @Bean("redisSingleConfig")
    public RedisStandaloneConfiguration redisSingleConfig() {
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration(host,Convert.toInt(port));
        redisConfig.setPassword(password);
        return redisConfig;
    }

    @Bean("redisFactorySingle")
    public LettuceConnectionFactory redisFactorySingle(@Qualifier("redisSinglePool") GenericObjectPoolConfig config,
                                                       @Qualifier("redisSingleConfig") RedisStandaloneConfiguration redisConfig) {//注意传入的对象名和类型RedisStandaloneConfiguration
        LettuceClientConfiguration clientConfiguration = LettucePoolingClientConfiguration.builder().poolConfig(config).build();
        return new LettuceConnectionFactory(redisConfig, clientConfiguration);
    }


    /**
     * 单实例redis数据源
     *
     * @param connectionFactory
     * @return
     */
    @Bean("redisSingleTemplate")
    public RedisTemplate<String, Object> redisSingleTemplate(
            @Qualifier("redisFactorySingle")LettuceConnectionFactory connectionFactory) {
        return redisTemplateSerializer(connectionFactory);
    }

    @Bean
    public RedisSingleUtil redisSingleUtil(){
        return new RedisSingleUtil();
    }

    private RedisTemplate<String, Object> redisTemplateSerializer(RedisConnectionFactory connectionFactory){
        RedisTemplate<String,Object> template = new RedisTemplate();
        template.setConnectionFactory(connectionFactory);
        // 序列化工具
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer
                = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);

        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringRedisSerializer);
        template.setValueSerializer(jackson2JsonRedisSerializer);

        template.setHashKeySerializer(jackson2JsonRedisSerializer);
        template.setHashValueSerializer(jackson2JsonRedisSerializer);

        template.afterPropertiesSet();
        return template;
    }

}
