package org.example.velocity;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * @author hanyangyang
 * @date 2025/5/15
 */
public class Learn2 {
    public static final String modelStr = "#foreach ($result in $ruleEntity) " + "#if($result.condition == \"eq\") $${patternModel}.eq($${result.field},\"$result.resultValue\") " + "and #elseif($result.condition == \"gt\") $${result.field}> $result.resultValue " + "and #elseif($result.condition == \"lt\") $${result.field}< $result.resultValue " + "and #elseif($result.condition == \"ne\") $${patternModel}.ne($${result.field},\"$result.resultValue\") " + "and #elseif($result.condition == \"contains\") $${result.field}.contains(\"$result.resultValue\") " + "and #elseif($result.condition == \"startsWith\") $${result.field}.startsWith(\"$result.resultValue\") " + "and #end " + "#end";

    public boolean ne(String org, String target) {
        if (Objects.equals("空", target) || Objects.equals("null", target)) {
            return StringUtils.isNotBlank(org);
        }
        return !Objects.equals(org, target);
    }


    public boolean eq(String org, String target) {
        if (Objects.equals("空", target) || Objects.equals("null", target)) {
            return StringUtils.isBlank(org);
        }
        return Objects.equals(org, target);
    }

    public String getPattern(List<RuleResult> resultExt) {
        VelocityContext context = new VelocityContext();
        context.put("ruleEntity", resultExt);
        context.put("patternModel", "patternModel");


        StringWriter writer = new StringWriter();
        ve.evaluate(context, writer, "", modelStr);
        String result = writer.toString();


        result = result.trim();
        result = "#if (" + result.substring(0, result.length() - 3) + ") true #else false #end";
        return result;
    }


    private static VelocityEngine ve = new VelocityEngine();

    static {
        ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        ve.setProperty(RuntimeConstants.RUNTIME_LOG_INSTANCE, "org.apache.velocity.runtime.log.NullLogChute");
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
                            "resultValue": "费用"
                        },
                        {
                            "condition": "eq",
                            "field": "tradeSubject",
                            "fieldName": "交易标的",
                            "resultValue": "天猫保证金"
                        }
                    ]
                """;

        List<RuleResult> resultExt = JSONObject.parseArray(a, RuleResult.class);

        Learn2 pattern = new Learn2();
        String result = pattern.getPattern(resultExt);
        System.out.println(result);

        // 解析出最终的表达式之后根据这个表达式去匹配当前实体

        // #if ($patternModel.eq($oppEntityCode,"费用") and    $patternModel.eq($tradeSubject,"天猫保证金") ) true #else false #end

        JSONObject js = new JSONObject();
        js.put("oppEntityCode","1abcHelloworld");
        js.put("tradeSubject","1abcHelloworld");

        VelocityContext context = new VelocityContext();
        context.put("patternModel", new Learn2());
        HashMap<String,Object> map = JSONObject.parseObject(js.toJSONString(), HashMap.class);
        map.forEach((key, value) -> context.put(key,value));

        StringWriter writer = new StringWriter();
        ve.evaluate(context, writer, "", result);
        String finalResult = writer.toString();


        System.out.println(finalResult);

    }
}

