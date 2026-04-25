package com.gpnu.auth.resource.interceptor;

import com.gpnu.auth.common.constants.AuthConstants;
import com.gpnu.auth.resource.context.UserContextHolder;
import com.gpnu.common.constants.Constant;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.MDC;

public class FeignRelayUserInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        // 1. 透传 requestId，无论是否登录都要透传
        String requestId = MDC.get(Constant.REQUEST_ID_MDC_KEY);
        if (requestId != null) {
            template.header(Constant.REQUEST_ID_HEADER, requestId);
        }

        // 2. 标识来源为 Feign 调用
        template.header(AuthConstants.REQUEST_FROM_HEADER, AuthConstants.REQUEST_FROM_FEIGN);

        // 3. 透传用户信息，未登录则不传
        Long userId = UserContextHolder.getUserIdOrNull();
        if (userId != null) {
            template.header(AuthConstants.USER_ID_HEADER, userId.toString());
            try {
                String userType = UserContextHolder.getUserType();
                template.header(AuthConstants.USER_TYPE_HEADER, userType);
            } catch (Exception ignored) {
            }
        }
    }
}