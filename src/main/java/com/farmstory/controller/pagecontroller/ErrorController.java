package com.farmstory.controller.pagecontroller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/error")
public class ErrorController {

    @GetMapping("/withdrawal")
    public ModelAndView errorPage() {
        ModelAndView mav = new ModelAndView();


        mav.setViewName("pages/error_login");
        return mav;
    }

    @GetMapping("/ban")
    public ModelAndView errorPage2() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("pages/error_ban");
        mav.addObject("ban","ban");

        SecurityContextHolder.clearContext();


        return mav;
    }
}
