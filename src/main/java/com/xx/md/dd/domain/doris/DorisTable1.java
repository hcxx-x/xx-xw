package com.xx.md.dd.domain.doris;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * OLAP
 * @TableName table1
 */
@Data
@TableName("table_test")
public class DorisTable1 implements Serializable {


    /**
     * 
     */
    private Integer siteid;

    /**
     * 
     */
    private Integer citycode;

    /**
     * 
     */
    private String username;

    /**
     * 
     */
    private Long pv;


}