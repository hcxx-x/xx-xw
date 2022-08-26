package com.xx.common.util;

import cn.hutool.core.util.NumberUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

public class NumUtil {
    enum KW {
        K("k", new BigDecimal(1000)),
        W("w", new BigDecimal(10000));
        private String suffix;
        private BigDecimal divisor;

        KW(String suffix, BigDecimal divisor) {
            this.suffix = suffix;
            this.divisor = divisor;
        }


    }

    public static String toKWBStr(String num, KW type) {
        return toKWBStr(num, type,null, null,null, null);
    }

    public static String toKWBStr(String num, KW type,Integer scale,RoundingMode roundingMode) {
        return toKWBStr(num, type,null, null,scale, roundingMode);
    }


    public static String toKWBStr(String num, KW type,String limit) {
        return toKWBStr(num, type, limit,null, null,null);
    }

    public static String toKWBStr(String num, KW type,String limit,Integer scale,RoundingMode roundingMode) {
        return toKWBStr(num, type, limit,null, scale,roundingMode);
    }

    public static String toKWBStr(String num, KW type,String limit,String topLimit) {
        return toKWBStr(num, type, limit,topLimit, null,null);
    }


    /**
     * 根据传入的数字将数字转成包含K\K+ W\W+的字符串
     * @param num 目标字符串
     * @param type 转换的类型 {@link com.xx.common.util.NumUtil.KW}
     * @param limit 转换成K\W的最小限制，如果num小于该值则不进行转换
     * @param topLimit 转换成K+ \W+ 的最小限制，如果大于该值则会对应的加上+，如果num小于该值，则该入参无效
     * @param scale  精度，保留小数点几位
     * @param roundingMode 四舍五入模式
     * @return 如何要求的字符串
     */
    public static String toKWBStr(String num, KW type, String limit, String topLimit, Integer scale, RoundingMode roundingMode) {
        if (NumberUtil.isNumber(num)
                || (Objects.nonNull(limit) && NumberUtil.isNumber(limit))
                || (Objects.nonNull(topLimit) && NumberUtil.isNumber(topLimit))){
            throw new IllegalArgumentException("数字格式非法！");
        }

        BigDecimal targetNum = new BigDecimal(num);
        String suffix = type.suffix;
        if (Objects.nonNull(limit) && targetNum.compareTo(new BigDecimal(limit)) < 0) {
            return targetNum.toString();
        }
        if (Optional.ofNullable(topLimit).isPresent()){
            BigDecimal topLimitBd = new BigDecimal(topLimit);
            if (targetNum.compareTo(topLimitBd)>0){
                targetNum = topLimitBd;
                suffix = '+'+suffix;
            }
        }

        BigDecimal resultNum;
        if (Objects.isNull(roundingMode)) {
            resultNum = targetNum.divide(type.divisor);
        } else {
            resultNum = targetNum.divide(type.divisor, Optional.ofNullable(scale).orElse(0), roundingMode);
        }

        return resultNum.toString() +suffix;

    }


    public static void main(String[] args) {

    }
}
