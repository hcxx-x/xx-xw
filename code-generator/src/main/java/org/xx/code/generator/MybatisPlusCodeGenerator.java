package org.xx.code.generator;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.converts.PostgreSqlTypeConvert;
import com.baomidou.mybatisplus.generator.fill.Column;
import com.baomidou.mybatisplus.generator.keywords.PostgreSqlKeyWordsHandler;

import java.sql.SQLException;
import java.util.*;

/**
 * @author hanyangyang
 */
public class MybatisPlusCodeGenerator {
    /**
     * 数据库连接配置
     */
    static final String DB_URL = "jdbc:mysql://localhost:3306/xw_web?characterEncoding=utf8&useSSL=true";
    static final String DB_USER = "root";
    static final String DB_PWD = "root";
    private static final DataSourceConfig.Builder DATA_SOURCE_BUILDER =
            new DataSourceConfig.Builder(DB_URL, DB_USER, DB_PWD)
                    .schema("xx_mate")
                    .keyWordsHandler(new PostgreSqlKeyWordsHandler())
                    .typeConvert(new PostgreSqlTypeConvert());


    /**
     * 是否是多模块项目，如果是则会根据代码中定义的规则将 api 和 service相关的类，分别生成到不同的项目中
     */
    static final boolean IS_MULTI_MODULE_PROJECT = true;

    /**
     * 获取当前项目的路径，注意：如果是多模块项目那么这种方法获取的是最外层项目的路径
     */
    static final String TOP_MODULE_PATH = System.getProperty("user.dir");


    /**
     * 父级包
     */
    static final String BASE_PACKAGE;

    /**
     * entity对象所在的包，会在父级包下生成子包
     */
    static final String ENTITY_PACKAGE = "domain.entity";

    /**
     * 用于存放不同类型的java文件生成的具体目录
     */
    static final Map<OutputFile, String> PATH_MAP = new HashMap<>(6);


    /**
     * 填充PATH_MAP
     */
    static {
        Scanner scanner = new Scanner(System.in);
        System.out.println("是否将区分api和service: yes or no; 默认yes");
        String spiltApiAndService = scanner.nextLine();
        if (!"yes".equals(spiltApiAndService) && !"no".equals(spiltApiAndService)) {
            System.out.println("只能输入yes或者no，请重新执行程序");
        }

        System.out.println("请输入模块名称，如果区分api和service 则无需带上api/service！");
        String moduleName = scanner.nextLine();

        System.out.println("请输入包路径！");
        String packagePath = scanner.nextLine();
        BASE_PACKAGE = packagePath;


        String mainPath = "/src/main/";
        String javaPath = mainPath + "java/";
        String resourcePath = mainPath + "resources/";
        String mapperXmlResourcePath = resourcePath + "mapper";

        String basePackagePath = packagePath.replace(".", "\\/");


        String basePath = TOP_MODULE_PATH + "/" + moduleName + "/";
        String apiBasePath = "";
        String serviceBasePath = "";
        String xmlPath = "";

        if ("yes".equals(spiltApiAndService)) {

            String API_MODULE_NAME = moduleName + "-api";
            String SERVICE_MODULE_NAME = moduleName + "-service";

            apiBasePath = basePath + API_MODULE_NAME + javaPath + basePackagePath;
            serviceBasePath = basePath + SERVICE_MODULE_NAME + javaPath + basePackagePath;
            xmlPath = basePath + SERVICE_MODULE_NAME + mapperXmlResourcePath;
        } else {
            apiBasePath = basePath + javaPath + basePackagePath;
            serviceBasePath = basePath + javaPath + basePackagePath;
            xmlPath = basePath + mapperXmlResourcePath;
        }

        String entityPath = ENTITY_PACKAGE.replace(".", "\\/");

        PATH_MAP.put(OutputFile.entity, apiBasePath + "/" + entityPath);
        PATH_MAP.put(OutputFile.mapper, serviceBasePath + "/mapper");
        PATH_MAP.put(OutputFile.service, apiBasePath + "/service");
        PATH_MAP.put(OutputFile.serviceImpl, serviceBasePath + "/service/impl");
        PATH_MAP.put(OutputFile.controller, serviceBasePath + "/controller");
        PATH_MAP.put(OutputFile.xml, xmlPath);
    }



    public static void main(String[] args) {
        FastAutoGenerator.create(DATA_SOURCE_BUILDER)
                // 全局配置
                .globalConfig((scanner, builder) -> builder.author(scanner.apply("请输入作者名称？")).disableOpenDir())
                // 包配置
                .packageConfig((scanner, builder) -> builder.pathInfo(PATH_MAP).parent(BASE_PACKAGE).entity(ENTITY_PACKAGE))
                // 策略配置
                .strategyConfig((scanner, builder) -> builder.addTablePrefix("sys_").addInclude(getTables(scanner.apply("请输入表名，多个英文逗号分隔？所有输入 all")))
                        .controllerBuilder().enableRestStyle().enableHyphenStyle()
                        .entityBuilder().enableLombok().addTableFills(new Column("create_time", FieldFill.INSERT))
                        .build())
                /*
                    模板引擎配置，默认 Velocity 可选模板引擎 Beetl 或 Freemarker
                   .templateEngine(new BeetlTemplateEngine())
                   .templateEngine(new FreemarkerTemplateEngine())
                 */.execute();
    }


    protected static List<String> getTables(String tables) {
        return "all".equalsIgnoreCase(tables) ? Collections.emptyList() : Arrays.asList(tables.split(","));
    }


}
