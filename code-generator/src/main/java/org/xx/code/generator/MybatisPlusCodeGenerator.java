package org.xx.code.generator;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;

import java.sql.SQLException;

/**
 * @author hanyangyang
 */
public class MybatisPlusCodeGenerator {
    static final String DB_URL = "jdbc:mysql://localhost:3306/xw_web?characterEncoding=utf8&useSSL=true";
    static final String DB_USER = "root";
    static final String DB_PWD = "root";

    /**
     * 数据源配置
     */
    private static final DataSourceConfig.Builder DATA_SOURCE_CONFIG = new DataSourceConfig
            .Builder(DB_URL,DB_USER,DB_PWD);

    /**
     * 执行 run
     */
    public static void main(String[] args) throws SQLException {
        FastAutoGenerator.create(DATA_SOURCE_CONFIG)
                // 全局配置
                .globalConfig((scanner, builder) -> builder.author(scanner.apply("请输入作者名称")))
                // 包配置
                .packageConfig((scanner, builder) -> builder.parent(scanner.apply("请输入包名")))
                // 策略配置
                .strategyConfig((scanner, builder) -> builder.addInclude(scanner.apply("请输入表名，多个表名用,隔开")))
                /*
                    模板引擎配置，默认 Velocity 可选模板引擎 Beetl 或 Freemarker
                   .templateEngine(new BeetlTemplateEngine())
                   .templateEngine(new FreemarkerTemplateEngine())
                 */
                .execute();
    }
}
