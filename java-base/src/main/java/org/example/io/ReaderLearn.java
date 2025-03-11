package org.example.io;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

/**
 * @author hanyangyang
 * @since 2025/3/11
 */
public class ReaderLearn {
  public static void main(String[] args) {

    try (Reader reader = new FileReader("input.txt")) {
      int readCharNum;
      char[] chars = new char[8];
      while ((readCharNum = reader.read(chars)) !=-1){
        System.out.println(new String(chars,0,readCharNum));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
