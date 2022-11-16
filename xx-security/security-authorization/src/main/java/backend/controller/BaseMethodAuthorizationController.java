package backend.controller;

import backend.entity.User;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreFilter;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import java.util.ArrayList;
import java.util.List;

/**
 * @auther: hanyangyang
 * @date: 2022/11/16
 */
@RequestMapping("/method")
@RestController
public class BaseMethodAuthorizationController {

    // 拥有ROLE_ADMIN角色才可以访问
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("hasRole")
    public String hasRole() {
        return "hasRole";
    }

    // 拥有ROLE_USER或者ROLE_ADMIN其中之一角色就可以访问
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @GetMapping("hasRoles")
    public String hasRoles() {
        return "hasRole ROLE_USER or ROLE_ADMIN";
    }

    /**
     * 限制只能查询Id小于10的数据
     */
    @GetMapping("findById")
    @PreAuthorize("#id<10")
    public String find(int id) {
        return "获取id<10的数据";
    }

    /**
     * 限制只能查询自己的信息
     */
    @GetMapping("findByName")
    @PreAuthorize("principal.username.equals(#username)")
    public String find(String username) {
        return "find user by username......" + username;
    }

    /**
     * 限制只能新增用户名称为abc的用户，#user 表示传入的user参数，name为user的其中一个属性
     */
    @PostMapping("addSelf")
    @PreAuthorize("#user.name.equals('abc')")
    public void add(@RequestBody User user) {
        System.out.println("addUser............" + user);
    }


    // returnObject 指的就是返回的对象,id是对象的属性
    @GetMapping("/find")
    @PostAuthorize("returnObject.id%2==0")
    public User find() {
        User user = new User();
        user.setId(2L);
        return user;
    }

    @GetMapping("/delete")
    @PreFilter(filterTarget="ids", value="filterObject%2==0")
    public String delete(List<Integer> ids) {
        return ids.toString();
    }

    @GetMapping("/findAll")
    @PostFilter("filterObject.id%2==0")
    public List<User> findAll() {
        List<User> userList = new ArrayList<User>();
        User user;
        for (int i=0; i<10; i++) {
            user = new User();
            user.setId((long) i);
            userList.add(user);
        }
        return userList;
    }

    /**
     * 允许所有用户访问
     * @return
     */
    @PermitAll
    @GetMapping("permitAll")
    public String permitAll(){
        return "permitAll";
    }

    /**
     * 拒绝所有用户访问
     * @return
     */
    @DenyAll
    @GetMapping("denyAll")
    public String denyAll(){
        return "denyAll";
    }

    /**
     * 允许拥有ROLE_USER、ROLE_ADMIN的角色访问
     * @return
     */
    @RolesAllowed({"ROLE_USER","ROLE_ADMIN"})
    @GetMapping("rolesAllowed")
    public String rolesAllowed(){
        return "RolesAllowed";
    }


    /**
     * 允许拥有ROLE_USER、ROLE_ADMIN的角色访问
     * @return
     */
    @Secured({ "ROLE_USER", "ROLE_ADMIN" })
    @GetMapping("/secured")
    public String Secured(){
        return "secured";
    }




}
