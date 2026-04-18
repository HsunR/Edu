package com.gpnu.api.client.user;

import com.gpnu.api.client.user.fallback.UserFeignFallback;
import com.gpnu.api.dto.user.UserAuthDTO;
import com.gpnu.api.dto.user.UserSimpleDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@FeignClient(name = "Intelli-Edu-user", path = "/api/user",
             fallbackFactory = UserFeignFallback.class)
public interface UserFeignClient {

    /**
     * 获取用户简要信息（姓名、头像、类型）
     */
    @GetMapping("/inner/users/{userId}")
    UserSimpleDTO getUserSimple(@PathVariable("userId") Long userId);

    /**
     * 批量获取用户简要信息
     */
    @PostMapping("/inner/users/batch")
    List<UserSimpleDTO> getUserSimpleBatch(@RequestBody List<Long> userIds);

    /**
     * 获取用户完整鉴权信息
     */
    @GetMapping("/inner/users/{userId}/auth")
    UserAuthDTO getUserForAuth(@PathVariable("userId") Long userId);

    /**
     * 根据用户名查询鉴权信息
     */
    @GetMapping("/inner/users/auth/by-username")
    UserAuthDTO getUserForAuthByUsername(@RequestParam("username") String username);

    /**
     * 根据邮箱查询鉴权信息
     */
    @GetMapping("/inner/users/auth/by-email")
    UserAuthDTO getUserForAuthByEmail(@RequestParam("email") String email);

    /**
     * 根据手机号查询鉴权信息
     */
    @GetMapping("/inner/users/auth/by-mobile")
    UserAuthDTO getUserForAuthByMobile(@RequestParam("mobile") String mobile);
}