package com.example.project.gateway.handler.instance.v1;

import cn.hutool.core.util.StrUtil;

import com.alibaba.fastjson2.JSON;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.project.gateway.constant.ServerWebExchangeAttributesKeyConstants;
import com.example.project.gateway.enums.GatewayErrorEnum;
import com.example.project.gateway.exception.TokenVerifyException;
import com.example.project.gateway.handler.ITokenVerifyHandler;
import com.example.project.gateway.utils.JwtUserVO;
import com.example.project.gateway.utils.JwtUtils;
import com.example.project.gateway.utils.RedisUtils;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.server.ServerWebExchange;

import java.util.Objects;

/**
 * token 校验(v1版)
 */
@Slf4j
public class TokenVerifyV1Handler implements ITokenVerifyHandler {
    private static TokenVerifyV1Handler instance = new TokenVerifyV1Handler();

    private final static String TOKEN_REDIS_PREFIX = "gw:token:";

    private final static String FORBIDDEN_TOKEN_REDIS_PREFIX = "gw:forbidden-token:";

    private TokenVerifyV1Handler(){}

    @Override
    public void verify(String token, ServerWebExchange exchange) throws TokenVerifyException {
        if(StrUtil.isBlank(token)){
            log.error("token:[{}] verify failed", token);
            throw new TokenVerifyException(GatewayErrorEnum.TOKEN_LOST);
        }
        String tokenSuffix = token.substring(token.lastIndexOf(".") + 1);
        final String forbiddenTokenKey = FORBIDDEN_TOKEN_REDIS_PREFIX + tokenSuffix;
        if(RedisUtils.hasKey(forbiddenTokenKey)){
            log.error("token:[{}]已被禁用", token);
            throw new TokenVerifyException(GatewayErrorEnum.TOKEN_EXPIRED);
        }
        final String tokenKey = TOKEN_REDIS_PREFIX + tokenSuffix;
        final JwtUserVO jwtUserVO = RedisUtils.get(tokenKey, JwtUserVO.class);
        if(Objects.nonNull(jwtUserVO)){
            exchange.getAttributes().put(ServerWebExchangeAttributesKeyConstants.JWT_INFO, jwtUserVO);
            return;
        }
        try{
            DecodedJWT jwt = JwtUtils.decodedJWT(token);
            JwtUserVO userVO = JwtUtils.getUserCenterUserInfo(jwt);
            exchange.getAttributes().put(ServerWebExchangeAttributesKeyConstants.JWT_INFO, userVO);
            long expireTime = jwt.getExpiresAt().getTime() - System.currentTimeMillis();
            RedisUtils.set(tokenKey, JSON.toJSONString(userVO), expireTime / 1000);
        } catch (ExpiredJwtException | TokenExpiredException e) {
            log.error("token:[{}]认证过期异常:", token, e);
            throw new TokenVerifyException(GatewayErrorEnum.TOKEN_EXPIRED);
        } catch (SignatureVerificationException e) {
            log.error("token:[{}]认证失败异常:", token, e);
            throw new TokenVerifyException(GatewayErrorEnum.TOKEN_ILLEGAL);
        } catch (Exception e) {
            log.error("token:[{}]校验token异常:", token, e);
            throw new TokenVerifyException(GatewayErrorEnum.TOKEN_ILLEGAL);
        }
    }

    public static TokenVerifyV1Handler instance(){
        return instance;
    }
}
