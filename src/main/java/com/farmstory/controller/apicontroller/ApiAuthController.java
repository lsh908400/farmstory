package com.farmstory.controller.apicontroller;

import com.farmstory.requestdto.user.GetFindIdReqDto;
import com.farmstory.requestdto.user.GetFindPwdDto;
import com.farmstory.requestdto.user.PutUserReqDto;
import com.farmstory.responsedto.user.GetFindIdRespDto;
import com.farmstory.service.EmailService;
import com.farmstory.service.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class ApiAuthController {

    private final UserService userService;
    private final EmailService emailService;


    @GetMapping("/check/id")
    public ResponseEntity<String> checkId(
            @RequestParam String userId
    ){
        if(userId.contains(" ")) {
            return ResponseEntity.ok().body("EW"); // 공백이 포함된 경우의 응답 코드 예
        }

        if(userId.isEmpty()){
            return ResponseEntity.ok().body("EUI");
        }
        String result = userService.checkId(userId);

        return ResponseEntity.ok().body(result);
    }

    @GetMapping("/check/nick")
    public ResponseEntity<String> checkNick(
            @RequestParam String userNick
    ){
        if(userNick.contains(" ")) {
            return ResponseEntity.ok().body("EW"); // 공백이 포함된 경우의 응답 코드 예
        }

        if(userNick.isEmpty()){
            return ResponseEntity.ok().body("EUN");
        }
        String result = userService.checkNick(userNick);

        return ResponseEntity.ok().body(result);
    }

    @GetMapping("/send/email")
    public ResponseEntity<Map<String, Object>> sendEmail(
            @RequestParam String userEmail,
            @RequestParam (value = "userName" , defaultValue = "0") String userName,
            @RequestParam (value = "userId" , defaultValue = "0") String userId
    ){
        Map<String, Object> response = new HashMap<>();

        if(!userName.equals("0")&&userId.equals("0")){
            String result = userService.checkNameAndEmail(userName,userEmail);
            long expiryTime = System.currentTimeMillis() + 180 * 1000;

            response.put("expiryTime", expiryTime);
            response.put("result", result);


            return ResponseEntity.ok().body(response);
        }
        if(!userId.equals("0")&&userName.equals("0")){
            String result = userService.checkIdAndEmail(userId,userEmail);
            long expiryTime = System.currentTimeMillis() + 180 * 1000;

            response.put("expiryTime", expiryTime);
            response.put("result", result);
            return ResponseEntity.ok().body(response);
        }

        String result = userService.sendEmail(userEmail);
        long expiryTime = System.currentTimeMillis() + 180 * 1000;


        response.put("expiryTime", expiryTime);
        response.put("result", result);



        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/check/pwd")
    public ResponseEntity<String> checkPwd(
            @RequestBody PutUserReqDto request
    ){
        String result = userService.checkPwd(request.getPwd());


        return ResponseEntity.ok().body(result);
    }

    @PutMapping("/pwd")
    public ResponseEntity<String> updatePwd(
            @RequestBody PutUserReqDto request
    ){
        if(request.getUserId()==null){
            String result = userService.updateUserPwd(request.getPwd());
            return ResponseEntity.ok().body(result);
        } else {
            String result = userService.updateUserPwdByUserId(request);

            return ResponseEntity.ok().body(result);
        }
    }

    @PostMapping("/find/id")
    public ResponseEntity<Map<String, Object>> findUser(
            @RequestBody GetFindIdReqDto dto
    ){
        Map<String, Object> response = new HashMap<>();
        GetFindIdRespDto respDto = userService.findByUserName(dto);
        response.put("userInfo",respDto);

        return ResponseEntity.ok().body(response) ;
    }

    @DeleteMapping("/guest")
    public ResponseEntity<String> guest(HttpServletRequest request){
        userService.deleteGuest(request);

        return ResponseEntity.ok().body("SU");
    }



}
