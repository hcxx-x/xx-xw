package org.example.io;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author hanyangyang
 * @since 2025/3/12
 */
public class FileInputStreamCopyFile {
  public static void main(String[] args) {
    // 获取当前项目编译后的classes类路径，resources中的文件会被编译到这个路径下面
    String dirPath = FileInputStreamCopyFile.class.getClassLoader().getResource("").getPath();
    try (
        FileInputStream fileInputStream = new FileInputStream(dirPath+"\\img.jpg");
        // 复制当当前目录的工作目录（顶级父工程目录下）
        FileOutputStream fileOutputStream = new FileOutputStream("img_copy.jpg");
        ){
      byte[] bytes = new byte[1024];
      int readLength = 0;
      while ((readLength = fileInputStream.read(bytes)) != -1) {
        fileOutputStream.write(bytes,0,readLength);
      }
      fileOutputStream.flush();
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
