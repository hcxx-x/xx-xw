package com.xx.shardingsphere;

import com.xx.shardingsphere.entity.User;
import com.xx.shardingsphere.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@SpringBootTest
class ShardingSphereApplicationTests {

    @Resource
    private UserMapper userMapper;

    @Test
    void contextLoads() {
    }

    @Test
    void testWriteReadSplite(){
        User user = new User();
        user.setUname("李四");
        userMapper.insert(user);

        List<User> users = userMapper.selectList(null);
        log.info("第一次查询结果：{}",users);

        List<User> userList = userMapper.selectList(null);
        log.info("第二次查询结果：{}",userList);
    }

    @Transactional
    @Test
    void testWriteReadSomeTx(){
        User user = new User();
        user.setUname("赵六");
        userMapper.insert(user);
        List<User> users = userMapper.selectList(null);
        log.info("第一次查询结果：{}",users);

        List<User> userList = userMapper.selectList(null);
        log.info("第二次查询结果：{}",userList);
    }

    @Test
    void testLoadBalance(){
        List<User> users = userMapper.selectList(null);
        log.info("第一次查询结果：{}",users);

        List<User> userList = userMapper.selectList(null);
        log.info("第二次查询结果：{}",userList);

        List<User> userList2 = userMapper.selectList(null);
        log.info("第三次查询结果：{}",userList);
    }



}
