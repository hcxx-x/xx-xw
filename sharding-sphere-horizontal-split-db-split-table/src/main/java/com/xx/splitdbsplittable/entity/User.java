package com.xx.splitdbsplittable.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author hanyangyang
 * @since 2023/7/26
 */


@TableName("t_user")
@Data
public class User {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private String uname;
}
