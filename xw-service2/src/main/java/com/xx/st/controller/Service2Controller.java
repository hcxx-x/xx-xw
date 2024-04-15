package com.xx.st.controller;

import com.xx.core.http.HttpR;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * @author hanyangyang
 * @since 2024/4/11
 */
@RestController
@RequestMapping("/service2")
public class Service2Controller {

    @RequestMapping("/api1")
    public void api1() throws InterruptedException {

    }

    @RequestMapping("/api2")
    public HttpR<String> api2() throws InterruptedException {
        return HttpR.ok("service api2");
    }

    @RequestMapping("/api3")
    public HttpR<String> api3() throws InterruptedException {
        return HttpR.ok("service api2");
    }
}
