package org.example.io;

import java.io.FileWriter;
import java.io.IOException;

/**
 * @author hanyangyang
 * @since 2025/3/12
 */
public class WriterLearn {
  public static void main(String[] args) {
    try (FileWriter fileWriter = new FileWriter("writer.txt")) {
      // 直接写字符串
      fileWriter.write("hello world");
      // 将流中的数据刷新到文件中，否则文件可能不会写入
      fileWriter.flush();
    } catch (IOException e) {
    }
  }
}
