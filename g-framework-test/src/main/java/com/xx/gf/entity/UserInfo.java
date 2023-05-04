package com.xx.gf.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Builder;
import lombok.Data;

/**
 * @author hanyangyang
 * @since 2023/3/27
 */
@Builder
@Data
public class UserInfo {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private Integer age;
    private String email;
}
