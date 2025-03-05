package org.example.thirty;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

/**
 * @author hanyangyang
 * @date 2025/3/5
 **/
public class ObjectMapperLearn {
  final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();


  /**
   * json 数组转 List
   * 示例入参：
   * jsonArrayStr：[{"brand":"ford"}, {"brand":"Fiat"}]
   * typeReference： new TypeReference<List<Car>>(){}
   * @return
   * @throws IOException
   */
  public static <T> List<T> toList(String jsonArrayStr, Class<T> clazz) throws
      IOException {
    if (jsonArrayStr == null || jsonArrayStr.isEmpty()) {
      return Collections.emptyList();
    }
    // 构建指定类型的 JavaType，避免泛型擦除问题
    JavaType type = OBJECT_MAPPER.getTypeFactory().constructCollectionType(List.class, clazz);
    return OBJECT_MAPPER.readValue(jsonArrayStr, type);
  }

  public static void main(String[] args) throws IOException {
    String str = "[{\"brand\":\"ford\"}, {\"brand\":\"Fiat\"}]";
    List<Car> list = toList(str, Car.class);
    System.out.println(list.toString());

  }






  static class Car {
    private String brand = null;
    private int doors = 0;

    public String getBrand() { return this.brand; }
    public void   setBrand(String brand){ this.brand = brand;}

    public int  getDoors() { return this.doors; }
    public void setDoors (int doors) { this.doors = doors; }
  }

}
