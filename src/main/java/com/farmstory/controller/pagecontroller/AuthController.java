package com.farmstory.controller.pagecontroller;

import com.farmstory.repository.user.UserRepository;
import com.farmstory.requestdto.user.SignupUserAddressDto;
import com.farmstory.requestdto.user.SignupUserDto;
import com.farmstory.service.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final UserRepository userRepository;

    @PostMapping("/user/signup")
    public ModelAndView postUser(
            @ModelAttribute SignupUserDto user,
            @ModelAttribute SignupUserAddressDto address
            ){
        ModelAndView mav = new ModelAndView();
        userService.insertUser(user,address);
        mav.setViewName("pages/auth/login");
        return mav;
    }

    @GetMapping("/withdrawal")
    public ModelAndView postWithdrawal(HttpServletRequest request){
        ModelAndView mav = new ModelAndView();
        userService.withdrawal(request);
        mav.setViewName("redirect:/");

        return mav;
    }

}
