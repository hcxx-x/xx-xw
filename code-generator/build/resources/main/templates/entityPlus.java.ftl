package ${package.Entity};

import java.io.Serializable;
import lombok.Data;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
<#list table.fields as field>
<#if (logicDeleteFieldName!"") == field.name>
import com.baomidou.mybatisplus.annotation.TableLogic;
</#if>
</#list>

<#list table.importPackages as pkg>
    <#if pkg == "java.util.Date">
import ${pkg};
    </#if>
</#list>

/**
* @author ${author}
* @date ${date}
* @description ${table.name} : ${table.comment!}
*/
<#if entityLombokModel>
@Data
</#if>
@TableName("${table.name}")
<#if swagger2>
@ApiModel("${table.comment!}")
</#if>
public  class ${entity} <#if superEntityClass??> extends  ${superEntityClass}</#if> implements Serializable{

private static final long serialVersionUID = 1L;
<#-- ----------  属性私有化  ---------->
<#list table.fields as field>

<#if field.keyFlag>
    <#assign keyPropertyName="${field.propertyName}"/>
</#if>

<#if field.keyFlag>
<#-- 主键 -->
    /**
    * 主键 : ${field.name},  ${field.comment!}
    */
<#-- 普通字段 -->
<#elseif !field.keyFlag>
    /**
    * ${field.name},  ${field.comment!}
    */
</#if>
<#-- 乐观锁注解 -->
<#if (versionFieldName!"") == field.name>
    @Version
</#if>
<#-- 逻辑删除注解 -->
<#if (logicDeleteFieldName!"") == field.name>
    @TableLogic
</#if>
<#--是否添加swagger注释-->
<#if swagger2>
    @ApiModelProperty("${field.comment!}")
</#if>
<#if field.propertyType == "LocalDateTime">
    @TableField("${field.name}")
    private Date ${field.propertyName};
</#if>
<#if field.propertyType != "LocalDateTime">
    @TableField("${field.name}")
    private ${field.propertyType} ${field.propertyName};
</#if>
</#list>

<#------------  构造函数   ----------- -->
<#if !entityLombokModel>
    public ${entity}(<#list table.fields as field><#if field.propertyType == "LocalDateTime">LocalDateTime ${field.propertyName}</#if><#if field.propertyType != "LocalDateTime">${field.propertyType} ${field.propertyName}</#if><#sep>,</#list>){
    <#list table.fields as field>
        this.${field.propertyName} = ${field.propertyName};
    </#list>
    }

    public ${entity}(){
    }
</#if>

<#------------  getter.setter封装  ---------->
<#if !entityLombokModel>
    <#list table.fields as field>
        <#if field.propertyType == "boolean">
            <#assign getprefix="is"/>
        <#else>
            <#assign getprefix="get"/>
        </#if>
        public <#if field.propertyType == "LocalDateTime">Date</#if><#if field.propertyType != "LocalDateTime">${field.propertyType}</#if> ${getprefix}${field.capitalName}() {
        return ${field.propertyName};
        }
        <#if entityBuilderModel>
            public ${entity} set${field.capitalName}(${field.propertyType} ${field.propertyName}) {
        <#else>
            public void set${field.capitalName}(<#if field.propertyType == "LocalDateTime">Date</#if><#if field.propertyType != "LocalDateTime">${field.propertyType}</#if> ${field.propertyName}) {
        </#if>
        this.${field.propertyName} = ${field.propertyName};
        <#if entityBuilderModel>
            return this;
        </#if>
        }
    </#list>
</#if>

<#-------------  重写toString()  ----------------->
<#if !entityLombokModel>
    @Override
    public String toString() {
    return "${entity}{" +
    <#list table.fields as field>
        <#if field_index==0>
            "${field.propertyName}=" + ${field.propertyName} +
        <#else>
            ", ${field.propertyName}=" + ${field.propertyName} +
        </#if>
    </#list>
    "}";
    }
</#if>
}
