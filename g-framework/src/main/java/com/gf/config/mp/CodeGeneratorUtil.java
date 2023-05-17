package com.gf.config.mp;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.fill.Column;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * @author hanyangyang
 * @since 2023/5/5
 */
public class CodeGeneratorUtil {
    static final String  JDBC_URL_MYSQL="jdbc:mysql://%s/%s?serverTimezone=Asia/Shanghai";


    public static void generator(String hostWithPort,String username,String password,String dbName) {
        DataSourceConfig.Builder dataSourceConfigBuilder = new DataSourceConfig
                .Builder(String.format(JDBC_URL_MYSQL,hostWithPort,dbName), username, password)
                .schema(dbName);
        StringBuilder srcPathBuild = new StringBuilder();

        FastAutoGenerator.create(dataSourceConfigBuilder)
                // 全局配置
                .globalConfig((scanner, builder) -> {
                    String topProjectPath = System.getProperty("user.dir");
                    String subProjectPath = scanner.apply("请输入子工程路径,若存在多层嵌套可使用英文句号分割").trim();
                    srcPathBuild.append(topProjectPath).append(File.separator).append(subProjectPath.replace(".", File.separator));
                    builder.outputDir(srcPathBuild+"/src/main/java/");
                    builder.author(scanner.apply("请输入作者名称？"));
                })
                // 包配置
                .packageConfig((scanner, builder) -> {
                    builder.parent(scanner.apply("请输入包名？"));
                    builder.pathInfo(Collections.singletonMap(OutputFile.xml, srcPathBuild+"/src/main/resources/mapper/"));
                })
                // 策略配置
                .strategyConfig((scanner, builder) -> builder.addInclude(getTables(scanner.apply("请输入表名，多个英文逗号分隔？所有输入 all")))
                        .entityBuilder().enableLombok().addTableFills(new Column("create_time", FieldFill.INSERT))
                        .controllerBuilder().enableRestStyle().enableHyphenStyle()
                        .serviceBuilder().superServiceClass(IEnhanceService.class).superServiceImplClass(EnhanceServiceImpl.class)
                        .mapperBuilder().superClass(EnhanceBaseMapper.class)
                        .build())
                /*
                    模板引擎配置，默认 Velocity 可选模板引擎 Beetl 或 Freemarker
                   .templateEngine(new BeetlTemplateEngine())
                   .templateEngine(new FreemarkerTemplateEngine())
                 */
                .execute();

    }

    // 处理 all 情况
    protected static List<String> getTables(String tables) {
        return "all".equals(tables) ? Collections.emptyList() : Arrays.asList(tables.split(","));
    }

    public static void main(String[] args) throws IOException {
        System.out.println(System.getProperty("user.dir"));
        File directory = new File("");//参数为空
        String courseFile = directory.getCanonicalPath() ;
        System.out.println(courseFile);
        System.out.println( System.getProperty("java.class.path"));



    }


}
