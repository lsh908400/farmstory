package com.farmstory.controller.apicontroller;

import com.farmstory.requestdto.cart.DeleteCartReqDto;
import com.farmstory.requestdto.cart.PostCartItemReqDto;
import com.farmstory.service.cart.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/client")
@RequiredArgsConstructor
public class ApiCartController {

    private final CartService cartService;
    @PostMapping("/cart")
    public ResponseEntity<String> postCart(
            @RequestBody PostCartItemReqDto request
    ){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null){
            return ResponseEntity.ok().body("/view/login");
        }
        String result = cartService.insertCart(request);

        String path = "/client/products?section=product&type=all";


        return ResponseEntity.ok().body(path);
    }

    @DeleteMapping("/cart")
    public ResponseEntity<String> deleteCart(
            @RequestBody DeleteCartReqDto request){
        System.out.println(request);
        String result = cartService.deleteCart(request);
        String path = null;

        if(result.equals("SU")){
            path = "/mypage/carts?section=mypage&type=cart";
        }

        return ResponseEntity.ok(path);
    }
}
