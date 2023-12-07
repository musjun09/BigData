package cn.tongdun.rule;

import org.springframework.boot.autoconfigure.web.ConditionalOnEnabledResourceChain;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@ConditionalOnEnabledResourceChain
public class Application {
    public static void main(String[] args) {

    }
}
