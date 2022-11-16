package backend.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping()
public class TestController {

    @GetMapping("/role_admin")
    public String roleAdmin(){
        return "role_admin";
    }

    @GetMapping("/role_employee")
    public String roleEmployee(){
        return "role_employee";
    }

    @GetMapping("/auth")
    public String auth(){
        return "auth";
    }
}
