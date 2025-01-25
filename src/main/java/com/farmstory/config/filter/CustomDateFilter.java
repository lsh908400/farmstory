package com.farmstory.config.filter;

import com.farmstory.entity.user.UserEntity;
import com.farmstory.service.user.UserScheduleService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;


public class CustomDateFilter implements Filter {

    private UserScheduleService userScheduleService;

    public CustomDateFilter(UserScheduleService userScheduleService) {
        this.userScheduleService = userScheduleService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String requestURI = httpServletRequest.getRequestURI();


        if (requestURI.endsWith(".css") || requestURI.endsWith(".js") ||
            requestURI.endsWith(".png") || requestURI.endsWith(".jpg") ||
            requestURI.endsWith(".jpeg") || requestURI.endsWith(".gif") ||
            requestURI.endsWith(".svg") || requestURI.endsWith(".ico") ||
            requestURI.startsWith("/pages/"))  {
            chain.doFilter(request, response);
            return;
        }

        // 오늘 날짜 구하기
        LocalDate today = LocalDate.now();

        String dbToday = userScheduleService.selectYear(today);




        httpServletRequest.setAttribute("dbToday", dbToday);

        // 다음 필터로 전달
        chain.doFilter(request, response);
    }

}
