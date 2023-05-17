package com.gf.config.mp.methods;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlInjectionUtils;
import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

/**
 * 在使用on duplicate key update时使用自增主键会有问题，之后返回一个自增主键，
 * 参考：https://forums.mysql.com/read.php?39,703493,703493
 * 个人理解：
 * 当使用 on duplicate key update时，官方也不能获取实际影响的行数，也就是不知道自增了几个主键，
 * 因为在使用这个语法的时候，如果是插入，则影响的行数是1，如果是更新，则影响的行数是2
 * 那为什么在使用navicat 时可以返回影响了几行呢，上面那篇文章说的是 那个影响的行数是在mysql的响应数据包中的，对于链接器来说并不好用，大概是这个么个意思吧....
 *
 *
 * @author hanyangyang
 * @since 2023/5/4
 */
public class InsertOrUpdateOnDuplicateKey extends AbstractMethod {


    public InsertOrUpdateOnDuplicateKey(String methodName) {
        super(methodName);
    }

    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        KeyGenerator keyGenerator = NoKeyGenerator.INSTANCE;
        final String sql = "<script>insert into %s %s values %s on duplicate key update %s</script>";

        final String fieldSql = prepareFieldSql(tableInfo);
        final String valueSql = prepareValuesSqlForMysqlBatch(tableInfo);
        String updateSql = prepareUpdateSql(fieldSql);

        final String sqlResult = String.format(sql, tableInfo.getTableName(), fieldSql, valueSql,updateSql);
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sqlResult, modelClass);
        String keyProperty = null;
        String keyColumn = null;
        // 表包含主键处理逻辑,如果不包含主键当普通字段处理
        if (tableInfo.havePK()) {
            if (tableInfo.getIdType() == IdType.AUTO) {
                /* 自增主键 */
                keyGenerator = Jdbc3KeyGenerator.INSTANCE;
                keyProperty = tableInfo.getKeyProperty();
                // 去除转义符
                keyColumn = SqlInjectionUtils.removeEscapeCharacter(tableInfo.getKeyColumn());
            } else {
                if (null != tableInfo.getKeySequence()) {
                    keyGenerator = TableInfoHelper.genKeyGenerator(this.methodName, tableInfo, builderAssistant);
                    keyProperty = tableInfo.getKeyProperty();
                    keyColumn = tableInfo.getKeyColumn();
                }
            }
        }
        return this.addInsertMappedStatement(mapperClass, modelClass, methodName, sqlSource, keyGenerator, keyProperty, keyColumn);
    }

    /**
     * 获取表的所有字段
     * @param tableInfo
     * @return
     */
    private String prepareFieldSql(TableInfo tableInfo) {
        StringBuilder fieldSql = new StringBuilder();
        fieldSql.append(tableInfo.getKeyColumn()).append(",");
        tableInfo.getFieldList().forEach(x -> {
            fieldSql.append(x.getColumn()).append(",");
        });
        fieldSql.delete(fieldSql.length() - 1, fieldSql.length());
        fieldSql.insert(0, "(");
        fieldSql.append(")");
        return fieldSql.toString();
    }

    private String prepareValuesSqlForMysqlBatch(TableInfo tableInfo) {
        final StringBuilder valueSql = new StringBuilder();
        valueSql.append("<foreach collection=\"list\" item=\"item\" index=\"index\" open=\"(\" separator=\"),(\" close=\")\">");
        valueSql.append("#{item.").append(tableInfo.getKeyProperty()).append("},");
        tableInfo.getFieldList().forEach(x -> valueSql.append("#{item.").append(x.getProperty()).append("},"));
        valueSql.delete(valueSql.length() - 1, valueSql.length());
        valueSql.append("</foreach>");
        return valueSql.toString();
    }

    private String prepareUpdateSql(String fieldSql){
        if (StrUtil.isBlank(fieldSql)){
            return "";
        }
        fieldSql = fieldSql.substring(1, fieldSql.length() - 1);
        String[] fields = fieldSql.split(",");
        StringBuilder updateSql = new StringBuilder("");
        for (int i = 0; i < fields.length; i++) {
            updateSql.append(fields[i]).append("=").append("VALUES(").append(fields[i]).append("),");
        }
        return updateSql.delete(updateSql.length()-1,updateSql.length()).toString();

    }
}