package com.xx.security.backend.config;

import com.xx.security.backend.entity.CustomUser;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @auther: hanyangyang
 * @date: 2022/11/10
 */
@Service
public class CustomUserDetailService implements UserDetailsService, UserDetailsPasswordService {
    /**
     * 密码校验成功后执行，会替换不是默认加密方式进行加密的用户密码
     * 模拟在数据库中更新用户密码，新密码会通过PasswordEncoderFactories中配置的默认的加密方式进行加密
     * @return
     */
    @Override
    public UserDetails updatePassword(UserDetails user, String newPassword) {
        List<CustomUser> customUsers = CustomUser.mockData();
        for (CustomUser customUser : customUsers) {
            if (customUser.getUsername().equals(user.getUsername())){
                customUser.setPassword(newPassword);
                return customUser;
            }
        }
        return user;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<CustomUser> customUsers = CustomUser.mockData();
        for (CustomUser customUser : customUsers) {
            if (customUser.getUsername().equals(username)){
                return customUser;
            }
        }
        throw new UsernameNotFoundException("用户不存在");
    }
}
