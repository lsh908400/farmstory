package com.farmstory.config.filter;

import com.farmstory.entity.user.UserEntity;
import com.farmstory.repository.user.UserRepository;
import com.farmstory.service.user.UserService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class CustomLoginFilter implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
//    private final UserService userService;
    private final RequestCache requestCache = new HttpSessionRequestCache();
    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        AuthenticationSuccessHandler.super.onAuthenticationSuccess(request, response, chain, authentication);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String username = authentication.getName();
        Optional<UserEntity> optUser = userRepository.findByUserId(username);

        if (optUser.isPresent() && "withdrawal".equals(optUser.get().getUserRole())) {
            if (optUser.isPresent()) {
                // User의 삭제 날짜 (getUserDeleteAt)를 문자열로 변환한 후 LocalDate로 변환
                LocalDate userDeleteAtDate = LocalDate.parse(optUser.get().getUserDeleteAt().toString().substring(0, 10));
                // 현재 날짜를 문자열로 변환한 후 LocalDate로 변환
                LocalDate currentDate = LocalDate.parse(LocalDateTime.now().toString().substring(0, 10));

                // 두 날짜의 차이 계산
                long daysBetween = ChronoUnit.DAYS.between(userDeleteAtDate, currentDate);

                // 7일 이상 차이나면 출력
                if (daysBetween >= 7) {
                    System.out.println("삭제된 지 7일 이상 지났습니다.");
                    userRepository.delete(optUser.get());
                    SecurityContextHolder.clearContext();

                    // 세션 무효화 (로그아웃 처리)
                    HttpSession session = request.getSession(false);
                    if (session != null) {
                        session.invalidate(); // 세션을 무효화하여 로그아웃 효과
                    }
                    response.sendRedirect("/view/login?error=withdrawal");
                } else {
                    response.sendRedirect("/error/withdrawal"); // "withdrawal" 역할인 경우 에러 페이지로 리다이렉트
                }
            }

        } else if(optUser.isPresent()&& "ban".equals(optUser.get().getUserRole())) {
          response.sendRedirect("/error/ban");
        } else {
            DefaultSavedRequest savedRequest = (DefaultSavedRequest) requestCache.getRequest(request, response);
//            response.sendRedirect("/");  // 성공 시 기본적으로 홈 페이지로 리다이렉트
            if (savedRequest != null) {
                // 이전에 저장된 URL로 리다이렉트
                String targetUrl = savedRequest.getRedirectUrl();
                redirectStrategy.sendRedirect(request, response, targetUrl);
            } else {
                // 이전 URL이 없다면 기본 URL로 리다이렉트
                redirectStrategy.sendRedirect(request, response, "/");
            }
        }
    }
}
