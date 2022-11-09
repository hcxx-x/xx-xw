package com.xx.mvc.entity;

/**
 * @auther: hanyangyang
 * @date: 2022/11/9
 */
public class RoleEntity {
    private String id;

    private String roleCode;

    private String roleName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}
