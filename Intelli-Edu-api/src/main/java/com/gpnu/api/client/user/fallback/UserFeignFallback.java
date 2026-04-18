package com.gpnu.api.client.user.fallback;

import com.gpnu.api.client.user.UserFeignClient;
import com.gpnu.api.dto.user.UserAuthDTO;
import com.gpnu.api.dto.user.UserSimpleDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;


import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class UserFeignFallback implements FallbackFactory<UserFeignClient> {

    @Override
    public UserFeignClient create(Throwable cause) {
        log.error("查询用户服务出现异常", cause);
        return new UserFeignClient() {
            @Override
            public UserSimpleDTO getUserSimple(Long userId) {
                return null;
            }

            @Override
            public List<UserSimpleDTO> getUserSimpleBatch(List<Long> userIds) {
                return Collections.emptyList();
            }

            @Override
            public UserAuthDTO getUserForAuth(Long userId) {
                return null;
            }

            @Override
            public UserAuthDTO getUserForAuthByUsername(String username) {
                return null;
            }

            @Override
            public UserAuthDTO getUserForAuthByEmail(String email) {
                return null;
            }

            @Override
            public UserAuthDTO getUserForAuthByMobile(String mobile) {
                return null;
            }
        };
    }
}