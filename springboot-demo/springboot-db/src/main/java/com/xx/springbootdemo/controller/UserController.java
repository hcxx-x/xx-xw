package com.xx.springbootdemo.controller;

import com.xx.springbootdemo.learn.TransactionWithLockLearn;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author hanyangyang
 * @date 2025/3/4
 **/
@RequestMapping("/user")
@RestController
public class UserController {

  @Resource
  private TransactionWithLockLearn transactionWithLockLearn;

  @GetMapping("/repeatInsert")
  public String repeatInsertUser(@RequestParam("phone") String phone) throws InterruptedException {
    transactionWithLockLearn.TransactionDefinitionTx(phone);
    return "SUCCESS";
  }
}
