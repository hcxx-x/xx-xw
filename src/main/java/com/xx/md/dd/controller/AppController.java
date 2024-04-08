package com.xx.md.dd.controller;

import com.xx.md.dd.service.doris.DorisPersonInfoService;
import com.xx.md.dd.service.mysql.PersonInfoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author hanyangyang
 * @since 2024/4/8
 */
@RestController
@RequestMapping
public class AppController {

    @Resource
    private DorisPersonInfoService dorisPersonInfoService;

    @Resource
    private PersonInfoService personInfoService;

    @RequestMapping("/tx")
    public void tx(){
        personInfoService.testTx();
    }
}
