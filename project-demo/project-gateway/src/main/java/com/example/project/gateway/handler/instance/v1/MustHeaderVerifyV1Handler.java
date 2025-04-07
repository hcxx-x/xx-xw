package com.example.project.gateway.handler.instance.v1;


import com.example.project.gateway.constant.HttpHeaderConstants;
import com.example.project.gateway.exception.GlobalMustHeaderException;
import com.example.project.gateway.handler.IMustHeaderVerifyHandler;
import com.example.project.gateway.property.SystemProperties;
import com.example.project.gateway.utils.BusinessValidateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.web.server.ServerWebExchange;

import java.util.Objects;

import static com.example.project.gateway.constant.ServerWebExchangeAttributesKeyContants.*;

/**
 * @Author zcchu
 * @Date 2024/6/7 16:39
 **/
@Slf4j
public class MustHeaderVerifyV1Handler implements IMustHeaderVerifyHandler {
    private static final MustHeaderVerifyV1Handler instance = new MustHeaderVerifyV1Handler();

    private MustHeaderVerifyV1Handler(){}
    public static IMustHeaderVerifyHandler instance() {
        return instance;
    }

    @Override
    public void verify(SystemProperties properties, ServerWebExchange exchange) throws GlobalMustHeaderException {
        final String timestamp = exchange.getRequest().getHeaders().getFirst(HttpHeaderConstants.X_TIMESTAMP);
        if(!BusinessValidateUtil.validateTimestamp(timestamp, properties.getTimestampTimeout())){
            log.error("http headers['{}']='{}' error, should be not null or Math.abs(System.currentTimeMillis() - timestamp) at {}ms.", HttpHeaderConstants.X_TIMESTAMP, timestamp, properties.getTimestampTimeout());
            throw new GlobalMustHeaderException(HttpHeaderConstants.X_TIMESTAMP);
        }
        exchange.getAttributes().put(IS_VERIFIED_TIMESTAMP, true);

        final String clientVersion = exchange.getRequest().getHeaders().getFirst(HttpHeaderConstants.X_C_VERSION);
        if(Objects.isNull(clientVersion)){
            log.error("http headers not contains['{}']", HttpHeaderConstants.X_C_VERSION);
            throw new GlobalMustHeaderException(HttpHeaderConstants.X_C_VERSION);
        }

        final String accessKey = exchange.getRequest().getHeaders().getFirst(HttpHeaderConstants.X_ACCESS_KEY);
        if(Objects.isNull(accessKey)){
            log.error("http headers not contains['{}']", HttpHeaderConstants.X_ACCESS_KEY);
            throw new GlobalMustHeaderException(HttpHeaderConstants.X_ACCESS_KEY);
        }
    }
}
