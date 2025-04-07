package com.example.project.gateway.utils;

import java.util.Objects;

public class BusinessValidateUtil {

    /**
     * 校验时间戳的合法性
     *
     * @param timestamp
     * @return
     */
    public static boolean validateTimestamp(String timestamp, long timeout) {
        if(Objects.isNull(timestamp)){
            return false;
        }
        try {
            long time = Long.parseLong(timestamp);
            long diffTime = System.currentTimeMillis() - time;
            // 不允许客户端时间比服务端时间大，并且客户端请求发起时间不允许比服务端当前时间晚
            if(Math.abs(diffTime)> timeout){
                return false;
            }
            return true;
        }catch (NumberFormatException e){
            return false;
        }
    }
}
