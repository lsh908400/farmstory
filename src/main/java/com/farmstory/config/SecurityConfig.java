package com.farmstory.config;

import com.farmstory.config.filter.CustomAuthorizationRequestResolver;
import com.farmstory.config.filter.CustomDateFilter;
import com.farmstory.config.filter.CustomLoginFilter;
import com.farmstory.repository.user.UserRepository;
import com.farmstory.service.user.AuthService;
import com.farmstory.service.user.MyOauth2UserService;
import com.farmstory.service.user.UserScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig  {

    private final UserScheduleService userScheduleService;
    private final AuthService authService;
    private final MyOauth2UserService myOauth2UserService;
    private final UserRepository userRepository;
    private final AuthenticationSuccessHandler customSuccessHandler;
    private final CustomLoginFilter customLoginFilter;
    private final CustomAuthorizationRequestResolver customAuthorizationRequestResolver;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .cors(cors -> cors
                        .configurationSource(corsConfigurationSource())
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/client/boards/**").permitAll()  // GET 요청은 모두 허용
                        .requestMatchers(HttpMethod.POST, "/client/board/**").authenticated()  // POST 요청은 인증된 사용자만
                        .requestMatchers(HttpMethod.PUT, "/client/board/**").authenticated()   // PUT 요청도 인증된 사용자만
                        .requestMatchers("/admin/**").hasRole("admin")
                        .requestMatchers("/board/**").hasAnyRole("user", "admin", "guest")
                        .requestMatchers("/mypage/**").hasAnyRole("user", "admin", "guest")
                        .requestMatchers(HttpMethod.GET,"/client/order/direct").authenticated()
                        .requestMatchers(HttpMethod.GET,"/client/event").authenticated()
                        .requestMatchers("/**", "/error", "/file/**", "/auth/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterAfter(new CustomDateFilter(userScheduleService), UsernamePasswordAuthenticationFilter.class)
                .formLogin(login -> login
                        .loginPage("/view/login")  // 로그인 페이지 URL
                        .loginProcessingUrl("/auth/user/login")  // 로그인 인증 처리 URL (디비 처리)
                        .successHandler(customLoginFilter)  // 로그인 성공 후 이동할 URL
                        .failureUrl("/view/login?error=true")  // 로그인 실패 시 이동할 URL
                        .usernameParameter("userName")  // 사용자명 파라미터 이름
                        .passwordParameter("pwd")  // 비밀번호 파라미터 이름
                        .permitAll()  // 로그인 페이지는 인증 없이 접근 가능
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/user/login")
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(myOauth2UserService)
                        )
                        .authorizationEndpoint(authorization -> authorization
                                .authorizationRequestResolver(customAuthorizationRequestResolver)
                        )
                        .successHandler(customLoginFilter)
                )
                .logout(logout -> logout
                        .invalidateHttpSession(true)
                        .logoutRequestMatcher(new AntPathRequestMatcher("/auth/user/logout","GET"))  // 절대 경로로 설정
                        .logoutSuccessUrl("/")
                        .deleteCookies("JSESSIONID")
                )
                .httpBasic(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
        ;


        return http.build();

    }

    @Bean
    public PasswordEncoder passwordEncoder (){
        return new BCryptPasswordEncoder();
    }

    @Bean
    protected CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.addAllowedHeader("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);

        return source;
    }
}
