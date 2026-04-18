package com.gpnu.user.controller;

import com.gpnu.api.dto.user.UserAuthDTO;
import com.gpnu.api.dto.user.UserSimpleDTO;
import com.gpnu.user.service.IUserService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/innder/users")
@Hidden
public class InnerUserController {

    @Resource
    private IUserService userService;

    @GetMapping("/{userId}")
    public UserSimpleDTO getUserSimple(@PathVariable Long userId) {
        return userService.getUserSimple(userId);
    }

    @PostMapping("/batch")
    public List<UserSimpleDTO> getUserSimpleBatch(@RequestBody List<Long> userIds) {
        return userService.getUserSimpleBatch(userIds);
    }

    @GetMapping("/{userId}/auth")
    public UserAuthDTO getUserForAuth(@PathVariable Long userId) {
        return userService.getUserForAuth(userId);
    }

    @GetMapping("/auth/by-username")
    public UserAuthDTO getUserForAuthByUsername(@RequestParam String username) {
        return userService.getUserForAuthByUsername(username);
    }

    @GetMapping("/auth/by-email")
    public UserAuthDTO getUserForAuthByEmail(@RequestParam String email) {
        return userService.getUserForAuthByEmail(email);
    }

    @GetMapping("/auth/by-mobile")
    public UserAuthDTO getUserForAuthByMobile(@RequestParam String mobile) {
        return userService.getUserForAuthByMobile(mobile);
    }

}
