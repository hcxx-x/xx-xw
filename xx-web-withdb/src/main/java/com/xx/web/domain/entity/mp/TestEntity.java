package com.xx.web.domain.entity.mp;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author: hyy
 * @date: 2023/2/1
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("test")
public class TestEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String uniqueField;
    @TableLogic(value = "0",delval = "id")
    private Boolean delFlag;
}
