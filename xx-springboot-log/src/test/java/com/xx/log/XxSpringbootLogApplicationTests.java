package com.xx.log;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xx.log.entity.UserInfo;
import com.xx.log.mapper.UserInfoMapper;
import com.xx.log.service.IUserInfoService;
import lombok.extern.slf4j.Slf4j;
import org.h2.engine.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@SpringBootTest
class XxSpringbootLogApplicationTests {
    @Resource
    private IUserInfoService userInfoService;

    @Resource
    private UserInfoMapper userInfoMapper;


    @Test
    void contextLoads() {
        List<Integer> ids = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            ids.add(i);
        }
        //System.out.println(ids.stream().map(e -> e.toString()).collect(Collectors.joining(",")));
        //log.info("y用户列表1：{}",userInfoService.list(Wrappers.<UserInfo>lambdaQuery().in(UserInfo::getId,ids)));
        log.info("用户列表：{}",userInfoMapper.queryAllUserConditionIdIn(ids));

        List<Long> longs = userInfoMapper.testIntLong();
        System.out.println(longs.get(0).getClass().getName());
        log.info("{}",longs);
    }

    @Test
    void testBatchInsert(){
        List<UserInfo> list = userInfoService.list();
        log.info("批量插入之前的list:{}",list);
        List<UserInfo> userList = new ArrayList<>();
        for (int i = 0; i < 1800; i++) {
            UserInfo user = new UserInfo();
            user.setAge(i);
            user.setName("name"+i);
            user.setId(Long.valueOf(i));
            user.setEmail(i+"@qq.com");
            userList.add(user);
        };
        boolean b = userInfoService.insertBatchSomeColumn(userList);
        log.info("批量插入的结果：{}",b);

        log.info("插入之后的数量：{}",userInfoService.count());
    }

    @Test
    void testAssert(){
        Object o = null;
        Assert.notNull(o,"对象不能为空");
    }

    public static void main(String[] args) {
        /*
        getList方法返回的结果是不带泛型的，可以使用带有泛型的声明去接收，这样就会出来虽然list内部类型是Integer，但是声明的list的泛型却是Long的情况
        但是这种方式会有问题，在获取元素操作的时候很容易出现类型转换失败的异常
        * */
        List<Long> list = getList();
        System.out.println(1);
    }

    public static List getList(){
        List list = new ArrayList<>();
        Object a = Integer.valueOf(1);
        list.add(a);
        return list;
    }




}
