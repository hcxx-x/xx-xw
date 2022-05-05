package org.xx.code.generator;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.OutputFile;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

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

    /**
     * 是否是多模块项目，如果是则会根据代码中定义的规则将 api 和 service相关的类，分别生成到不同的项目中
     */
    static final boolean IS_MULTI_MODULE_PROJECT=true;

    /**
     * 获取当前项目的路径，注意：如果是多模块项目那么这种方法获取的是最外层项目的路径
     */
    static final String TOP_MODULE_PATH = System.getProperty("user.dir");

    /**
     * 模块名称
     */
    static final String MODULE_NAME = "code-generator";

    /**
     * 根据模块名称自动生成，如果是多模块项目则会使用到下面两个常量
     */
    static final String API_MODULE_NAME = MODULE_NAME + "-api";
    static final String SERVICE_MODULE_NAME = MODULE_NAME + "-service";

    /**
     * 父级包
     */
    static final String BASE_PACKAGE = "com.xx";

    /**
     * entity对象所在的包，会在父级包下生成子包
     */
    static final String ENTITY_PACKAGE="domain.entity";

    /**
     * 用于存放不同类型的java文件生成的具体目录
     */
    static final Map<OutputFile, String> PATH_MAP = new HashMap<>(6);


    /**
     * 填充PATH_MAP
     */
    static {
        String mainPath = "/src/main/";
        String javaPath = mainPath+"java/";
        String resourcePath = mainPath+"resources/";
        String mapperXmlResourcePath = resourcePath+"mapper";

        String basePackagePath = BASE_PACKAGE.replace(".","\\/");


        String basePath = TOP_MODULE_PATH + "/" + MODULE_NAME + "/";
        String apiBasePath = "";
        String serviceBasePath = "";
        String xmlPath = "";

        if (IS_MULTI_MODULE_PROJECT){
             apiBasePath = basePath + API_MODULE_NAME + javaPath + basePackagePath;
             serviceBasePath = basePath + SERVICE_MODULE_NAME + javaPath + basePackagePath;
             xmlPath = basePath + SERVICE_MODULE_NAME + mapperXmlResourcePath;
        }else{
             apiBasePath = basePath +  javaPath + basePackagePath;
             serviceBasePath = basePath +  javaPath + basePackagePath;
             xmlPath = basePath +  mapperXmlResourcePath;
        }

        String entityPath = ENTITY_PACKAGE.replace(".","\\/");

        PATH_MAP.put(OutputFile.entity, apiBasePath + "/"+entityPath);
        PATH_MAP.put(OutputFile.mapper, serviceBasePath + "/mapper");
        PATH_MAP.put(OutputFile.service, apiBasePath + "/service");
        PATH_MAP.put(OutputFile.serviceImpl, serviceBasePath + "/service/impl");
        PATH_MAP.put(OutputFile.controller, serviceBasePath + "/controller");
        PATH_MAP.put(OutputFile.xml, xmlPath);
    }


    /**
     * 数据源配置
     */
    private static final DataSourceConfig.Builder DATA_SOURCE_CONFIG = new DataSourceConfig
            .Builder(DB_URL, DB_USER, DB_PWD);

    /**
     * 执行 run
     */
    public static void main(String[] args) throws SQLException {
        System.out.println(System.getProperty("user.dir"));
        FastAutoGenerator.create(DATA_SOURCE_CONFIG)
                // 全局配置
                .globalConfig((scanner, builder) -> builder.author(scanner.apply("请输入作者名称")))
                // 包配置
                .packageConfig((scanner, builder) -> builder.pathInfo(PATH_MAP).parent(BASE_PACKAGE).entity(ENTITY_PACKAGE))
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
