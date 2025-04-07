package com.example.project.gateway.utils;

import cn.hutool.core.bean.BeanUtil;


import cn.hutool.core.map.MapUtil;
import com.alibaba.fastjson2.JSON;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.project.gateway.enums.ErrorCodeEnum;
import com.example.project.gateway.http.Response;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @description: JWT-用户控台工具类
 * @author: liheng
 * @create: 2024-06-05 16:46
 **/
@Slf4j
@Component
public class JwtUtils {
    //加密秘钥
    private static String secret;

    //过期时间
    private static Long expire;

    /**
     * @Author lsc
     * <p> 校验token是否正确 </p>
     * @Param token
     */
    public static Response verify(String token) {
        try {
            JwtUserVO jwtUserVO = verifyJwt(token);
            return Response.success(jwtUserVO);
        } catch (ExpiredJwtException e) {
            log.info("token:[{}]认证过期异常:", token, e);
            return Response.restResult(ErrorCodeEnum.TOKEN_EXPIRE.getErrorCode(), ErrorCodeEnum.TOKEN_EXPIRE.getDesc());
        } catch (SignatureVerificationException e) {
            log.info("token:[{}]认证失败异常:", token, e);
            return Response.restResult(ErrorCodeEnum.TOKEN_SIGN.getErrorCode(), ErrorCodeEnum.TOKEN_SIGN.getDesc());
        } catch (Exception e) {
            log.info("token:[{}]校验token异常:", token, e);
            return Response.restResult(ErrorCodeEnum.UNKNOWN_ERROR.getErrorCode(), ErrorCodeEnum.UNKNOWN_ERROR.getDesc());
        }
    }

    /**
     * 校验token
     *
     * @param token
     * @return
     *
     * @throws ExpiredJwtException,SignatureVerificationException,Exception
     */
    public static JwtUserVO verifyJwt(String token) {
        DecodedJWT jwt = decodedJWT(token);
        return getUserCenterUserInfo(jwt);
    }

    public static DecodedJWT decodedJWT(String token) {
        // 设置加密算法
        Algorithm algorithm = Algorithm.HMAC256(secret);
        JWTVerifier verifier = JWT.require(algorithm).build();
        // 校验TOKEN
        verifier.verify(token);
        DecodedJWT jwt = JWT.decode(token);
        return jwt;
    }


    /* *
     * @Author lsc
     * <p>生成签名,30min后过期 </p>
     * @Param payload 握手参数
     * @Param secret 秘钥
     * @Param expireTime 过期时间 单位ms
     * @Return java.lang.String
     */
    public static String sign(Map<String, Object> payload) {
        Date expiresAt = constructExpiresAt();
        return sign(payload, expiresAt);

    }

    public static String sign(Map<String, Object> payload, Date expiresAt) {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        JWTCreator.Builder builder = JWT.create();
        payload.forEach(
                (key, val) -> {
                    if(Objects.isNull(val)){
                        return;
                    }
                    if(isPrimitiveOrWrapper(val.getClass())){
                        builder.withClaim(key, val.toString());
                    }else{
                        builder.withClaim(key, JSON.toJSONString(val));
                    }
                });
        // 附带username信息
        return builder.withExpiresAt(expiresAt).sign(algorithm);
    }

    public static Date constructExpiresAt() {
        return new Date(System.currentTimeMillis() + expire);
    }


    /**
     * 用户中心的token对应的decodeJWT
     *
     * @param jwt
     * @return
     */
    public static JwtUserVO getUserCenterUserInfo(DecodedJWT jwt) {
        Map<String, Claim> result = jwt.getClaims();
        Map<String, String> jwtMap = MapUtil.newHashMap();
        result.keySet().forEach(x -> jwtMap.put(x, result.get(x).asString()));
        return BeanUtil.toBean(jwtMap, JwtUserVO.class);
    }

    @Value("${jwt.secret:ynbybyuis$$@@%}")
    public void setSecret(String secret){
        this.secret = secret;
    }
    @Value("${jwt.expire:86400000}")
    public void setExpire(Long expire){
        this.expire = expire;
    }


    private static final Set<Class<?>> WRAPPER_TYPES = new HashSet<>(Arrays.asList(
            Boolean.class, Character.class, Byte.class, Short.class,
            Integer.class, Long.class, Float.class, Double.class,
            String.class, Void.class // Void 也可以被视为一种特殊的包装类型
    ));

    public static boolean isPrimitiveOrWrapper(Class<?> clazz) {
        if (clazz.isPrimitive()) {
            return true; // 是基础类型
        }
        return WRAPPER_TYPES.contains(clazz); // 是包装类型
    }

    public static void main(String[] args) {
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJuYW1lIjoi6ZmG55-z56OKIiwiaWQiOiIxODAzOTgwMTAyODI2Mjc0MiIsInRlbmFudENvZGUiOiIxMDAwMyIsInVzZXJUeXBlIjoib3BzYWRtaW4iLCJzZWNyZXQiOiJZazhJeVVoeVVZVjlOeDBUNVdVWVhnPT0iLCJleHAiOjE3MzQzMzQ3NTF9.3ilg4BVy1iJeGxJd6Fkzt_AuuerN7-P0sQAmzJL8-ps";
        secret = "ynbybyuis$$@@%";
        DecodedJWT decodedJWT = JwtUtils.decodedJWT(token);
//        System.out.println(decodedJWT);
        String decrypt = AesUtil.decrypt("Yk8IyUhyUYV9Nx0T5WUYXg==", "10ffbc73e62263fcaf8348056c622931ff47da3cc17669ae874a1c4f3be7cb03");
        System.out.println(decrypt);
    }
}
