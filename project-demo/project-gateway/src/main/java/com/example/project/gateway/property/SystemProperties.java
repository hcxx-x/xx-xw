package com.example.project.gateway.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Setter
@Getter
@ConfigurationProperties(prefix = "system")
public class SystemProperties {

    /**
     * 仅代理(不做任何校验)
     */
    private List<String> onlyProxyUrls;

    /**
     * 白名单地址(不校验token、sign)
     */
    private List<String> whiteUrls;

    /**
     * 白名单地址(不校验token)
     */
    private List<String> tokenWhiteUrls;

    /**
     * 白名单地址(不校验nonce)
     */
    private List<String> replayAttackWhiteUrls;

    /**
     * 白名单地址(不校验sign)
     */
    private List<String> signWhiteUrls;

    /**
     * 时间戳过期时间(ms)
     */
    private long timestampTimeout = 5*60 * 1000;
}
