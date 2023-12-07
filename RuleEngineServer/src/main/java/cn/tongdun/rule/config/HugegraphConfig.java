package cn.tongdun.rule.config;

import org.apache.hugegraph.driver.HugeClient;
import org.apache.hugegraph.driver.HugeClientBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(HugegraphProperties.class)
public class HugegraphConfig {
    private final HugegraphProperties hugegraphProperties;
    public HugegraphConfig(HugegraphProperties hugegraphProperties){
        this.hugegraphProperties = hugegraphProperties;
    }

    @Bean
    public HugeClient hugeClient(){
        return new HugeClientBuilder(hugegraphProperties.getUrl(), hugegraphProperties.getGraph())
                .configUser(hugegraphProperties.getUsername(), hugegraphProperties.getPassword())
                .build();
    }
}
