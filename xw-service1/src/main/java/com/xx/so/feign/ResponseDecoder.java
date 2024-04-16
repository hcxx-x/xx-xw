package com.xx.so.feign;


import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xx.core.http.HttpR;
import feign.FeignException;
import feign.Response;
import feign.Util;
import feign.codec.Decoder;
import org.apache.commons.io.IOUtils;
import org.springframework.cloud.openfeign.support.SpringDecoder;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 自定义解码，实在不想看到遍地的 Response<T>
 */
public class ResponseDecoder implements Decoder {
    private final SpringDecoder decoder;


    public ResponseDecoder(SpringDecoder decoder) {
        this.decoder = decoder;
    }



    /**
     * 方法返回值如果是void 就不会走这个类
     * @param response
     * @param type
     * @return
     * @throws IOException
     * @throws FeignException
     */
    @Override
    public Object decode(Response response, Type type) throws IOException, FeignException {
        /*Method method = response.request().requestTemplate().methodMetadata().method();
        //如果Feign接口的返回值不是 Response{code:0,...} 结构类型，并且远程响应又是这个结构
        boolean notTheSame = method.getReturnType() != HttpR.class;
        if (notTheSame) {
            //构造一个这个结构类型
            Type newType =
                    new ParameterizedType() {
                        @Override
                        public Type[] getActualTypeArguments() {
                            return new Type[]{type};
                        }
                        @Override
                        public Type getRawType() {
                            return HttpR.class;
                        }
                        @Override
                        public Type getOwnerType() {
                            return null;
                        }
                    };
            HttpR result = (HttpR) this.decoder.decode(response, newType);
            //只返回data
            return result.getData();
        }
        return this.decoder.decode(response, type);*/
        // TODO 可以根据注解判断是否需要执行下面的操作
        String bodyStr = Util.toString(response.body().asReader(Util.UTF_8));
        JSONObject jsonObject = JSONUtil.parseObj(bodyStr);
        // 拿到Json对象最顶层的key
        Set<String> jsonKey = jsonObject.keySet();
        Field[] declaredFields = HttpR.class.getDeclaredFields();
        Set<String> fieldNameSet = Arrays.stream(declaredFields).map(Field::getName).collect(Collectors.toSet());
        // 如果属性名和json key完全匹配，说明符合HttpR的数据结构
        if (fieldNameSet.containsAll(jsonKey) && jsonKey.containsAll(fieldNameSet)){
            // 说明符合HttpR的数据结构
            Integer code = jsonObject.getInt("code");
            if (Objects.equals(code, 0)){
                return jsonObject.getObj("data");
            }else{
                // 可以抛出自定义异常或者解码异常等，根据需要定义即可
                throw new RuntimeException(jsonObject.getStr("msg"));
            }
        }else{
            return this.decoder.decode(response, type);
        }


    }

    public static void main(String[] args) {
        JSONObject jsonObject = JSONUtil.parseObj("{\"code1\":0,\"msg\":\"\",\"data\":{\"id\":1,\"name\":\"张三\"}}");
        System.out.println(jsonObject.keySet());
    }
}