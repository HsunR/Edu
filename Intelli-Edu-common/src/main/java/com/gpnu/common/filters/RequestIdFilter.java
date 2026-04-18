package com.gpnu.common.filters;

import com.gpnu.common.constants.Constant;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class RequestIdFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws ServletException, IOException {
        // 1.获取request
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        // 2.获取请求头中的requestId
        String requestId = request.getHeader(Constant.REQUEST_ID_HEADER);
        try {
            // 3.存入MDC
            MDC.put(Constant.REQUEST_ID_HEADER, requestId);
            filterChain.doFilter(request, servletResponse);
        }finally {
            // 4.移除
            MDC.clear();
        }
    }
}
