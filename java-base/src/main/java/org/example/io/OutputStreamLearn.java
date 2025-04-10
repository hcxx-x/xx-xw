package org.example.io;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author hanyangyang
 * @since 2025/3/11
 */
public class OutputStreamLearn {
  public static void main(String[] args) {
    // append 默认false,表示清空原文件写，可以设置true表示追加
    try(OutputStream os = new FileOutputStream("output.txt",false)){
      os.write("hello world".getBytes());
      os.flush();
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
