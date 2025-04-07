package com.example.project.gateway.handler;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONException;
import com.example.project.gateway.exception.SignConsistencyVerifyException;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;

import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * 请求一致性校验处理器接口
 */
public interface ISignConsistencyVerifyHandler {

    /**
     * 签名一致性校验
     *
     * @param sign      签名
     * @param timestamp 时间戳(ms)
     * @param accessKey
     * @param exchange
     * @throws SignConsistencyVerifyException 认定签名一致性校验不通过
     */
    void verify(String sign, String timestamp, String accessKey, ServerWebExchange exchange) throws SignConsistencyVerifyException;


    /**
     * 将参数Map转换为URL查询字符串。
     * @param param 需要转换的参数Map，键值对形式。
     * @return 如果参数Map为空或无内容，返回空字符串；否则返回排序后的键值对组成的查询字符串，格式为"key=value"，并用"&"连接。
     */
    default String toParams(Map<String, String> param) {
        if(MapUtil.isEmpty(param)){
            return "";
        }
        return param.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e->String.format("%s=%s", e.getKey(), e.getValue()))
                .collect(Collectors.joining("&"));
    }

    /**
     * 获取请求参数
     *
     * @param exchange
     * @return
     */
    default Map<String, String> getRequestParams(ServerWebExchange exchange){
        MediaType mediaType = exchange.getRequest().getHeaders().getContentType();
        // 不是get请求，并且请求的mediaType is json
        if (MediaType.APPLICATION_FORM_URLENCODED.isCompatibleWith(mediaType) || (MediaType.APPLICATION_JSON.isCompatibleWith(mediaType) && !Objects.equals(exchange.getRequest().getMethod(), HttpMethod.GET))) {
            AtomicReference<String> requestBody = new AtomicReference<>("");
            Flux<DataBuffer> body = exchange.getRequest().getBody();
            body.subscribe(buffer -> {
                CharBuffer charBuffer = StandardCharsets.UTF_8.decode(buffer.asByteBuffer());
                requestBody.set(charBuffer.toString());
            });
            // 走body
            return bodyToMap(requestBody.get());
        }
        if(Objects.equals(exchange.getRequest().getMethod(), HttpMethod.GET)){
            // 走url的rawquery
            return rawQueryToMap(URLUtil.decode(exchange.getRequest().getURI().getRawQuery()));
        }else {
            // 走queryParams
            return queryParamsToMap(exchange.getRequest().getQueryParams());
        }
    }

    /**
     * 将body中的参数处理成map
     * @param body
     * @return
     */
    default Map<String, String> bodyToMap(String body) {
        if(StrUtil.isBlank(body)){
            return new HashMap<>();
        }
        try {
            return JSON.parseObject(body)
                    .entrySet()
                    .stream()
                    .filter(e -> isJoinSign(e.getValue()))
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().toString()));
        }catch (JSONException e){
            // 不是json格式, 使用路径方式解析
            return rawQueryToMap(body);
        }
    }

    /**
     * 判断当前数据是否参与sign
     *
     * @param value
     * @return
     */
    default boolean isJoinSign(Object value) {
        if(Objects.isNull(value) || StrUtil.isBlank(value.toString()) || value instanceof Collections || value instanceof Map|| value instanceof JSON){
            return false;
        }
        return true;
    }

    /**
     * 将查询参数转换为Map。
     * @param queryParams 需要转换的MultiValueMap类型的查询参数。
     * @return 返回一个Map，其中键是查询参数的名称，值是对应的单一值。如果查询参数的值不是单一值或者为空，则该参数不会被包含在返回的Map中。
     */
    default Map<String, String> queryParamsToMap(MultiValueMap<String, String> queryParams) {
        return queryParams.entrySet()
                .stream()
                .filter(e-> Objects.nonNull(e.getValue()) && e.getValue().size() == 1)
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().get(0)));
    }

    /**
     * 将原始查询字符串转换为键值对映射。
     * @param rawQuery 需要转换的原始查询字符串，格式为"key1=value1&key2=value2"等。
     * @return 返回一个Map对象，其中包含从原始查询字符串中解析出的键值对。如果原始查询字符串为空或null，则返回一个空的HashMap。
     */
    default Map<String, String> rawQueryToMap(String rawQuery) {
        if(Objects.isNull(rawQuery)){
            return new HashMap<>();
        }
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        final int len = rawQuery.length();
        String name = null;
        int pos = 0; // 未处理字符开始位置
        int i; // 未处理字符结束位置
        char c; // 当前字符
        for (i = 0; i < len; i++) {
            c = rawQuery.charAt(i);
            switch (c) {
                case '='://键和值的分界符
                    if (null == name) {
                        // name可以是""
                        name = rawQuery.substring(pos, i);
                        // 开始位置从分节符后开始
                        pos = i + 1;
                    }
                    // 当=不作为分界符时，按照普通字符对待
                    break;
                case '&'://键值对之间的分界符
                    map.set(name, rawQuery.substring(pos, i));
                    name = null;
                    if (i+4 < len && "amp;".equals(rawQuery.substring(i + 1, i + 5))) {
                        // issue#850@Github，"&amp;"转义为"&"
                        i+=4;
                    }
                    // 开始位置从分节符后开始
                    pos = i + 1;
                    break;
            }
        }

        // 处理结尾
        map.set(name, rawQuery.substring(pos, i));
        return map.entrySet()
                .stream()
                .filter(e-> Objects.nonNull(e.getValue()) && e.getValue().size() == 1)
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().get(0)));
    }


}
