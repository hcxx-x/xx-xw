package org.example.velocity;

import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.VelocityContext;

import java.io.StringWriter;
import java.util.List;
import java.util.Objects;

/**
 * @author hanyangyang
 * @date 2025/5/16
 */
public class PatternModel {
    public static final String modelStr = """
            #foreach ($result in $ruleEntity) 
            #if($result.condition == "eq")  $${patternModel}.eq($${result.field},"$result.resultValue") 
            and #elseif($result.condition == "gt") $${result.field}> $result.resultValue 
            and #elseif($result.condition == "lt") $${result.field}< $result.resultValue 
            and #elseif($result.condition == "ne") $${patternModel}.ne($${result.field},"$result.resultValue") 
            and #elseif($result.condition == "contains") $${result.field}.contains("$result.resultValue") 
            and #elseif($result.condition == "startsWith") $${result.field}.startsWith("$result.resultValue") 
            and #end 
            #end
            """;

    public static final String modelStr2 = """
            #foreach ($parentResult in $ruleEntity) 
            #foreach ($result in $parentResult.resultValue)
             #if ($foreach.index == 0) ( #end
             #if ($foreach.index != 0 && $foreach.index != $parentResult.size) or #end
            #if($parentResult.condition == "eq")  $${patternModel}.eq($${parentResult.field},"$result")
               
             #elseif($parentResult.condition == "gt") $${parentResult.field}> $result
             
             #elseif($parentResult.condition == "lt") $${parentResult.field}< $result
    
             #elseif($parentResult.condition == "ne") $${patternModel}.ne($${parentResult.field},"$result")

             #elseif($parentResult.condition == "contains") $${parentResult.field}.contains("$result")
        
             #elseif($parentResult.condition == "startsWith") $${parentResult.field}.startsWith("$result") 
            #end
            #end)
            and
            #end
            """;



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
        String result =  VelocityUtil.getRenderResult(context, modelStr2);
        result = result.trim();
        result = "#if (" + result.substring(0, result.length() - 3) + ") true #else false #end";
        return result;
    }


    //#if ($patternModel.eq($oppEntityCode,"[费用, 交易]")
    //and   $patternModel.eq($tradeSubject,"[天猫保证金]")
    //) true #else false #end
    public String isArray(String fieldValue){
        return fieldValue.startsWith("[") && fieldValue.endsWith("]") ? "true" : "false";
    }
}
