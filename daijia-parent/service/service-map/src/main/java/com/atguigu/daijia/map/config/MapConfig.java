package com.atguigu.daijia.map.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @author lonelykkk
 * @email 2765314967@qq.com
 * @date 2024/9/21 20:48
 * @Version V1.0
 */
@Configuration
public class MapConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
