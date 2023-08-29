package com.xx.springbootdruid;

import com.xx.springbootdruid.entity.UserInfo;
import com.xx.springbootdruid.service.IUserInfoService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@SpringBootTest
class SpringbootDruidApplicationTests {
	@Resource
	private IUserInfoService userInfoService;

	@Test
	void contextLoads() {
		List<UserInfo> list = userInfoService.list();
		log.info("list:{}",list);
	}

}
