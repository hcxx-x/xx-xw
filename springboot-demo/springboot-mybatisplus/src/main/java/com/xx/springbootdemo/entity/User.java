package com.xx.springbootdemo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * @author hanyangyang
 * @date 2025/3/4
 **/
@Data
public class User {
  @TableId(type = IdType.AUTO)
  private Long id;
  private String name;
  private String phone;
  private String gender;
  private LocalDateTime gmtCreate;
  private LocalDateTime gmtModified;
}
