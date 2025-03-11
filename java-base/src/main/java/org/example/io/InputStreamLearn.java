package org.example.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;

/**
 * 从文件中获取流
 * （1）MyClass.class.getResourceAsStream
 * 相对路径（不以 / 开头）：从当前类所在的包目录开始查找资源。
 * 例如：MyClass.class.getResourceAsStream("config.properties") 会在 com/example/MyClass 的同级目录下查找文件。
 * 绝对路径（以 / 开头）：从类路径根目录（即 src/main/resources 或 target/classes）开始查找资源。
 * 例如：MyClass.class.getResourceAsStream("/global.properties") 会直接查找类路径根目录下的文件。
 * （2）ClassLoader.getResourceAsStream
 * 路径始终从类路径根目录开始，且不能以 / 开头。
 * 例如：MyClass.class.getClassLoader().getResourceAsStream("config.properties") 会直接查找类路径根目录下的文件，等同于 MyClass.class.getResourceAsStream("/config.properties")。
 * 路径若以 / 开头会返回 null，例如 getResourceAsStream("/global.properties") 会导致加载失败。
 * 
 * 
 * 
 * InputStream/Reader:字节/字符输入流顶层抽象类，指从外界输入到计算机系统
 * 字节输入流
 * 1.FileInputStream:从文件读取数据
 * 2.ByteArrayInputStream:从byte数组读取数据
 *
 * @author hanyangyang
 * @since 2025/3/11
 */
public class InputStreamLearn {

  public static void fileInputStream() {
    FileInputStream fis = null;
    try {
      fis = new FileInputStream(new File("input.txt"));
      byte[] bytes = new byte[1024];
      int readed = 0;
      while ((readed = fis.read(bytes)) != -1) {
        System.out.println(new String(bytes,0,readed));
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      if (fis != null) {
        try {
          fis.close();
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }

    }
  }

  public static void main(String[] args) throws FileNotFoundException {
    fileInputStream();
  }


  public static void getResource() throws IOException {
    // 直接写 D:/data/config.txt 或者 /data/config.txt 以绝对路径获取,
    InputStream resourceAsStream1 = new FileInputStream("input.txt");

    /**
     * 从文件中获取流
     * （1）MyClass.class.getResourceAsStream
     * 相对路径（不以 / 开头）：从当前类所在的包目录开始查找资源。
     * 例如：MyClass.class.getResourceAsStream("config.properties") 会在 com/example/MyClass 的同级目录下查找文件。
     * 绝对路径（以 / 开头）：从类路径根目录（即 src/main/resources 或 target/classes）开始查找资源。
     * 例如：MyClass.class.getResourceAsStream("/global.properties") 会直接查找类路径根目录下的文件。
     *
     **/
    InputStream resourceAsStream2 = InputStreamLearn.class.getResourceAsStream("/input.txt");

    /*
    （2）ClassLoader.getResourceAsStream
    路径始终从类路径根目录开始，且不能以 / 开头。
    例如：MyClass.class.getClassLoader().getResourceAsStream("config.properties") 会直接查找类路径根目录下的文件，等同于 MyClass.class.getResourceAsStream("/config.properties")。
    路径若以 / 开头会返回 null，例如 getResourceAsStream("/global.properties") 会导致加载失败。
     */
    InputStream resourceAsStream3 = InputStreamLearn.class.getClassLoader().getResourceAsStream("input.txt");
  }


  public static void getFile(){
    // 直接写 D:/data/config.txt 或者 /data/config.txt 以绝对路径获取,
    File file = new File("D:/data/config.txt");

    //文件在包内（类路径下的包目录）
    String absolutePath = InputStreamLearn.class.getResource("data.txt").getPath();
    File file1 = new File(absolutePath);

    // 文件在资源目录下（如src/main/resources）
    String resourcePath = InputStreamLearn.class.getClassLoader().getResource("config.properties").getPath();
    File file2 = new File(resourcePath);
  }


  public static void getCurrentDir() {
    System.out.println("当前工作目录：" + System.getProperty("user.dir"));
    System.out.println("类路径根目录：" + InputStreamLearn.class.getResource("/").getPath());
  }
}
