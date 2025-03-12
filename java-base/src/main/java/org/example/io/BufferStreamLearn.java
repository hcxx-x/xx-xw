package org.example.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;

/**
 * 字节缓冲流： BufferedInputStream/BufferedOutputStream
 * 字符缓冲流： BufferedReader/BufferedWriter
 * 缓冲流作用：更加高效
 * @author hanyangyang
 * @since 2025/3/12
 */
public class BufferStreamLearn {
  public static void byteBufferStream(){
    BufferedInputStream bis = null;
    BufferedOutputStream bos = null;
    try {
       bis = new BufferedInputStream(new FileInputStream("img.jpg"));
       bos = new BufferedOutputStream(new FileOutputStream("img_copy.jpg"));
      // 读写数据
      int len;
      byte[] bytes = new byte[1024];
      while ((len = bis.read(bytes)) != -1) {
        bos.write(bytes, 0 , len);
      }
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }finally {
      try {
        if (Objects.nonNull(bis)){
          bis.close();
        }
        if (Objects.nonNull(bos)){
          bos.close();
        }
      } catch (IOException e) {
        throw new RuntimeException(e);
      }

    }


  }

  public static void charBufferStream(){
    BufferedReader br = null;
    BufferedWriter bw = null;
    try {
       br = new BufferedReader(new FileReader("input.txt"));
       bw = new BufferedWriter(new FileWriter("input_copy.txt"));
      String line = null;
      while ((line = br.readLine()) != null){
        bw.write(line);
      }
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }finally {
      try {
        if (Objects.nonNull(br)) {
          br.close();
        }
        if (Objects.nonNull(bw)) {
          bw.close();
        }
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }


  }
}
