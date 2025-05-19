package org.example.velocity;

import com.alibaba.fastjson.JSONObject;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.util.List;

/**
 * @author hanyangyang
 * @date 2025/5/15
 */
public class Learn2 {





    private static VelocityEngine ve = new VelocityEngine();

    static {
        ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        //ve.setProperty(RuntimeConstants.RUNTIME_LOG_INSTANCE, "org.apache.velocity.runtime.log.NullLogChute");
        ve.init();
    }

    /**
     * resultValue 其实可以理解为target,就是需要匹配的字符
     *
     * @param args
     */
    public static void main(String[] args) {
        String a = """
                    [
                        {
                            "condition": "eq",
                            "field": "oppEntityCode",
                            "fieldName": "交易对手方结算主体",
                            "resultValue": ["费用,abc"]
                        },
                        {
                            "condition": "eq",
                            "field": "tradeSubject",
                            "fieldName": "交易标的",
                            "resultValue": ["天猫保证金","2"]
                        }
                    ]
                """;

        List<RuleResult> resultExt = JSONObject.parseArray(a, RuleResult.class);

        PatternModel pattern = new PatternModel();
        String result = pattern.getPattern(resultExt);
        System.out.println(result);

        // 解析出最终的表达式之后根据这个表达式去匹配当前实体

        // #if ($patternModel.eq($oppEntityCode,"费用") and    $patternModel.eq($tradeSubject,"天猫保证金") ) true #else false #end
        // #if (($patternModel.eq($oppEntityCode,"费用") || $patternModel.eq() )and    $patternModel.eq($tradeSubject,"天猫保证金") ) true #else false #end

       /* JSONObject js = new JSONObject();
        js.put("oppEntityCode","1abcHelloworld");
        js.put("tradeSubject","1abcHelloworld");

        VelocityContext context = new VelocityContext();
        context.put("patternModel", new Learn2());
        HashMap<String,Object> map = JSONObject.parseObject(js.toJSONString(), HashMap.class);
        map.forEach((key, value) -> context.put(key,value));





        System.out.println(VelocityUtil.getRenderResult(context,result));*/

    }
}

