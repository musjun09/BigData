package cn.tongdun.rule.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "hugegraph.datasource")
public class HugegraphProperties {
    private String url;
    private String username;
    private String password;
    private String graph;
}
