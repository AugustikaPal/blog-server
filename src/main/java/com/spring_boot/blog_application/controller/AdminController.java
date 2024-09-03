package com.spring_boot.blog_application.controller;


import com.spring_boot.blog_application.entity.User;
import com.spring_boot.blog_application.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@CrossOrigin(origins = {"http://127.0.0.1:5500", "http://localhost:5500"})
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;


    @GetMapping("/all-users")
    public String getAllUsers(Model model) {
        List<User> allUsers = userService.getAllUsers();
        model.addAttribute("users", allUsers);
        return "admin_get_users"; // Thymeleaf template name
    }

    @PostMapping("/admin-signup")
    public void createAdmin(@RequestBody User user){
        userService.saveAdmin(user);
    }
}
