package com.xx;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author hanyangyang
 * @since 2023/7/12
 */
public class JacksonUtil {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    // 日期格式化
    private static final String STANDARD_FORMAT = "yyyy-MM-dd HH:mm:ss";

    static {
        //对象的所有字段全部列入
        OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        //取消默认转换timestamps形式
        OBJECT_MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        //忽略空Bean转json的错误
        OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        //所有的日期格式都统一为以下的样式，即yyyy-MM-dd HH:mm:ss
        OBJECT_MAPPER.setDateFormat(new SimpleDateFormat(STANDARD_FORMAT));
        //忽略 在json字符串中存在，但是在java对象中不存在对应属性的情况。防止错误
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private JacksonUtil() {
    }

    /**
     * 对象转Json格式字符串
     *
     * @param obj 对象
     * @return Json格式字符串
     */
    public static <T> String toJsonStr(T obj) {
        if (obj == null) {
            return null;
        }
        try {
            return obj instanceof String ? (String) obj : OBJECT_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 对象转file
     *
     * @param fileName
     * @param obj
     */
    public static void toFile(String fileName, Object obj) {
        if (obj == null) {
            return;
        }
        try {
            OBJECT_MAPPER.writeValue(new File(fileName), obj);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 对象转Json格式字符串(格式化的Json字符串)
     *
     * @param obj 对象
     * @return 美化的Json格式字符串
     */
    public static <T> String toJsonStrPretty(T obj) {
        if (obj == null) {
            return null;
        }
        try {
            return obj instanceof String ? (String) obj : OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 字符串转换为自定义对象
     *
     * @param jsonStr 要转换的字符串
     * @param clazz   自定义对象的class对象
     * @return 自定义对象
     */
    public static <T> T toBean(String jsonStr, Class<T> clazz) {
        if (Objects.isNull(jsonStr) || "".equals(jsonStr.trim()) || clazz == null) {
            return null;
        }
        try {
            return clazz.equals(String.class) ? (T) jsonStr : OBJECT_MAPPER.readValue(jsonStr, clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * json 字符串转化为指定类型的java对象
     *
     * @param jsonStr
     * @param typeReference
     * @param <T>
     * @return
     */
    public static <T> T toBean(String jsonStr, TypeReference<T> typeReference) {
        if (Objects.isNull(jsonStr) || "".equals(jsonStr.trim()) || typeReference == null) {
            return null;
        }
        try {
            return (T) (typeReference.getType().equals(String.class) ? jsonStr : OBJECT_MAPPER.readValue(jsonStr, typeReference));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T toCollection(String str, Class<?> collectionClazz, Class<?>... elementClazzes) {
        JavaType javaType = OBJECT_MAPPER.getTypeFactory().constructParametricType(collectionClazz, elementClazzes);
        try {
            return OBJECT_MAPPER.readValue(str, javaType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将JSON数据转换成列表
     *
     * @param jsonStr JSON数据
     * @param beanType 对象类型
     * @return 列表
     */
    public static <T> List<T> toList(String jsonStr, Class<T> beanType) {
        try {
            JavaType javaType = OBJECT_MAPPER.getTypeFactory().constructParametricType(List.class, beanType);
            List<T> resultList = OBJECT_MAPPER.readValue(jsonStr, javaType);
            return resultList;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将JSON数据转换成Set集合
     *
     * @param jsonStr    JSON数据
     * @param elementType 元素类型
     * @return Set集合
     */
    public static <E> Set<E> toSet(String jsonStr, Class<E> elementType) {
        try {
            JavaType javaType = OBJECT_MAPPER.getTypeFactory().constructCollectionType(Set.class, elementType);
            Set<E> resultSet = OBJECT_MAPPER.readValue(jsonStr, javaType);
            return resultSet;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将JSON数据转换成Map集合
     *
     * @param jsonStr  JSON数据
     * @param keyType   键类型
     * @param valueType 值类型
     * @return Map集合
     */
    public static <K, V> Map<K, V> toMap(String jsonStr, Class<K> keyType, Class<V> valueType) {
        try {
            JavaType javaType = OBJECT_MAPPER.getTypeFactory().constructMapType(Map.class, keyType, valueType);
            Map<K, V> resultMap = OBJECT_MAPPER.readValue(jsonStr, javaType);
            return resultMap;
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    /**
     * 判断是字符串是否是json字符串
     * @param str
     * @return
     */
    public static boolean isJson(String str){
        try {
            OBJECT_MAPPER.createParser(str);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 判断json字符串是否包含某个属性
     * @param jsonStr json 字符串
     * @param field 属性名称
     * @return 是否包含
     */
    public static boolean hasField(String jsonStr,String field){
        try {
            JsonNode jsonNode = OBJECT_MAPPER.readTree(jsonStr);
            return jsonNode.has(field);
        } catch (JsonProcessingException e) {
            return false;
        }
    }


}
