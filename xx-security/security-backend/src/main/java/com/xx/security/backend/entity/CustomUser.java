package com.xx.security.backend.entity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @auther: hanyangyang
 * @date: 2022/11/9
 */
public class CustomUser implements UserDetails {
    private String id;
    private String username;
    private String password;
    private Boolean accountNonExpired = true;
    private Boolean accountNonLocked = true;
    private Boolean credentialsNonExpired = true;
    private Boolean enabled = true;

    private List<RoleEntity> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List list = new ArrayList<GrantedAuthority>();
        roles.forEach(role -> {
            list.add(new SimpleGrantedAuthority(role.getRoleCode()));
        });
        return list;
    }

    public static List<CustomUser> mockData() {
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setRoleCode("admin");
        RoleEntity roleEntity1 = new RoleEntity();
        roleEntity.setRoleCode("role1");


        CustomUser customUser = new CustomUser();
        customUser.setUsername("admin");
        customUser.setPassword("{noop}123");
        List<RoleEntity> list = new ArrayList<>();
        list.add(roleEntity);
        customUser.setRoles(list);

        CustomUser customUser1 = new CustomUser();
        customUser1.setUsername("user1");
        customUser1.setPassword("{noop}123");
        List<RoleEntity> list1 = new ArrayList<>();
        list.add(roleEntity);
        customUser1.setRoles(list1);

        ArrayList<CustomUser> customUsers = new ArrayList<>();
        customUsers.add(customUser);
        customUsers.add(customUser1);
        return customUsers;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAccountNonExpired(Boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    public void setAccountNonLocked(Boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    public void setCredentialsNonExpired(Boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public void setRoles(List<RoleEntity> roles) {
        this.roles = roles;
    }
}
