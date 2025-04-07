package com.example.project.gateway.handler.instance.v1;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.StrUtil;

import com.example.project.gateway.exception.SignConsistencyVerifyException;
import com.example.project.gateway.handler.ISignConsistencyVerifyHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.server.ServerWebExchange;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * 一致性签名(v1版)
 */
@Slf4j
public class SignConsistencyVerifyV1Handler implements ISignConsistencyVerifyHandler {

    private static SignConsistencyVerifyV1Handler instance = new SignConsistencyVerifyV1Handler();

    private SignConsistencyVerifyV1Handler(){}

    public static ISignConsistencyVerifyHandler instance() {
        return instance;
    }

    @Override
    public void verify(String sign, String timestamp, String accessKey, ServerWebExchange exchange) throws SignConsistencyVerifyException {
        /**
         * 2.2 签名原始字符串
         * 无论是GET请求还是POST请求，为了保证签名原始字符串的规则统一，都采用以下方式生成原始字符串：
         * 1. 按照参数名的 ASCII 码顺序从小到大排序（字典序），使用 URL 键值对的方式拼接成字符串 S1，（如：k1=value1&k2=value2&k3=value3…）。
         * 2. 针对参数的字段名和字段值都采用原始值，且区分大小写
         * 3. 针对空参数、数组参数、map参数、文件流参数不参与签名校验
         * 4. 获得当前的时间戳timestamp
         * 5. 签名原始字符串:S=timestamp+s1+accessKey
         * 注意：
         * 1. 其中空参数、数组参数、map不参与签名
         * 2. accessKey由后端提供，B端、C端、运管后台不同
         * 2.3 签名方法
         * sign 采用了非对称加密做签名处理，防止参数被篡改风险。
         *
         * 签名过程分为以下两步
         * 1、首先是按照2.2的方式将获的原始字符串S；
         * 2、其次再将 S 根据 SHA256 算法生成S2
         * 3、将S2按照 base64编码 得到sign。
         * 4、将sign字段、timestamp字段、accessKey字段添加到请求头,请求头key分别是x-sign、x-timestamp、x-access-key.
         *
         */
        // 获取参与签名的param
        final String s1 = toParams(getRequestParams(exchange));
        log.debug("s1[request param to str]: {}", s1);

        final String s = timestamp + s1 + accessKey;
        log.debug("S[timestamp + s1 + accessKey]: {}{}{}", timestamp, s1, accessKey);

        final String s2 = sha256(s);
        log.debug("s2[sha256(timestamp + s1 + accessKey)]: {}", s2);

        final String mySign = Base64.encode(s2);
        log.debug("sign[base64.encode(s2)]: {}", mySign);

        if(StrUtil.equals(mySign, sign)){
            return;
        }
        log.error("S[timestamp + s1 + accessKey]: {}{}{}, sign verify failed, request sign in header: {}, compute sign: {}", timestamp, s1, accessKey, sign, mySign);
        throw new SignConsistencyVerifyException();
    }

    /**
     * sha256
     *
     * @param data
     * @return
     */
    private String sha256(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
