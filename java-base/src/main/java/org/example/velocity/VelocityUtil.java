package org.example.velocity;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.io.StringWriter;
import java.util.List;

/**
 * @author hanyangyang
 * @date 2025/5/15
 */
public class VelocityUtil {
    private static VelocityEngine ve = new VelocityEngine();

    static {
        // 设置资源加载器为类路径加载器
        ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        // 设置ValueExtractor的属性，指定类路径资源加载器
        ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        // 设置Velocity的日志实例属性为NullLogChute，以禁用日志输出
        ve.setProperty(RuntimeConstants.RUNTIME_LOG_INSTANCE, "org.apache.velocity.runtime.log.NullLogChute");
        ve.init();
    }

    /**
     * 获取渲染后结果
     *
     * @param context 上下文内容
     * @param vtlStr  VTL表达式字符串
     * @return 结合VTL和上下文内容渲染之后的结果
     */
    public static String getRenderResult(Context context, String vtlStr) {
        StringWriter writer = new StringWriter();
        // 从context取值去做模板解析，输出到 output writer中。
        ve.evaluate(context, writer, "", vtlStr);
        return writer.toString();
    }
}
