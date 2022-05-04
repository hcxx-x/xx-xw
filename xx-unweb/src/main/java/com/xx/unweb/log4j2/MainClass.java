package com.xx.unweb.log4j2;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MainClass {
    // 定义日志记录器对象
    public static final Logger LOGGER = LogManager.getLogger(MainClass.class);

    public static void main(String[] args) {
        new MainClass().testQuick();
    }


    public void testQuick() {
        // 日志消息输出
        LOGGER.error("@|green Hello|@");
        LOGGER.warn("warn");
        LOGGER.info("info");
        LOGGER.debug("debug");
        LOGGER.trace("trace");
        LOGGER.info("@|KeyStyle {}|@ = @|ValueStyle {}|@", 123412, 1231);
    }
}
