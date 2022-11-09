package com.xx.security.backend.controller;

import com.google.code.kaptcha.Producer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * @auther: hanyangyang
 * @date: 2022/11/9
 */
@RequestMapping("kaptcha")
@RestController
public class KaptchaController {

    @Resource
    private Producer producer;

    @GetMapping("/getImage")
    public void getImage(HttpServletResponse response, HttpServletRequest request) throws IOException {
        String text = producer.createText();
        System.out.println("生成的验证码是："+text);
        HttpSession session = request.getSession();
        session.setAttribute("kaptcha",text);
        ServletOutputStream outputStream = response.getOutputStream();
        ImageIO.write(producer.createImage(text),"png",outputStream);
    }
}
