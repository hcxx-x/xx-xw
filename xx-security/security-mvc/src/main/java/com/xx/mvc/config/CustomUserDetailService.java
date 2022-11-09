package com.xx.mvc.config;

import com.xx.mvc.entity.CustomUser;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @auther: hanyangyang
 * @date: 2022/11/9
 */

public class CustomUserDetailService implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<CustomUser> customUsers = CustomUser.mockData();
        final UserDetails userDetails;
        for (CustomUser customUser : customUsers) {
            if (customUser.getUsername().equals(username)){
                return customUser;
            }
        }
        throw new UsernameNotFoundException("用户不存在");
    }
}
