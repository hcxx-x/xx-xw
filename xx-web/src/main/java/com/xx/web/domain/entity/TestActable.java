package com.xx.web.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.annotation.Table;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlCharsetConstant;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlEngineConstant;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;

/**
 * 该类用于测试AcTable根据实体类生成mysql的表
 * 关于AcTable的相关文档，可以查看 https://www.yuque.com/sunchenbin/actable
 */
@Data
@TableName("test_actable")
@Table(name="test_actable",charset= MySqlCharsetConstant.UTF8MB4,engine= MySqlEngineConstant.InnoDB,comment="测试actable要使用的表")
public class TestActable {

    @TableId(type=IdType.AUTO)
    private Long id;


    @Column(name = "content",type = MySqlTypeConstant.VARCHAR,length = 20,comment="测试内容字段")
    private String content;

}
