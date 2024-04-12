package com.xx.so.feign;

import com.xx.core.http.HttpR;
import feign.FeignException;
import feign.Response;
import feign.codec.Decoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 自定义解码，实在不想看到遍地的 Response<T>
 */
public class ResponseDecoder implements Decoder {
    private final SpringDecoder decoder;

    public ResponseDecoder(SpringDecoder decoder) {
        this.decoder = decoder;
    }

    @Override
    public Object decode(Response response, Type type) throws IOException, FeignException {

        Method method = response.request().requestTemplate().methodMetadata().method();
        //如果Feign接口的返回值不是 Response{code:0,...} 结构类型，并且远程响应又是这个结构
        boolean notTheSame = method.getReturnType() != HttpR.class;
        if (notTheSame) {
            /*//构造一个这个结构类型
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
            return result.getData();*/
        }else{
            return this.decoder.decode(response, type);
        }
    }
}