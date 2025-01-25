package com.farmstory.controller.apicontroller;

import com.farmstory.requestdto.board.PostCommentReqDto;
import com.farmstory.requestdto.board.PutCommentReqDto;
import com.farmstory.responsedto.board.GetCommentsRespDto;
import com.farmstory.service.board.CommentService;
import com.farmstory.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/client")
@RequiredArgsConstructor
public class ApiCommentController {

    private final CommentService commentService;
    private final UserService userService;

//    @GetMapping("/comments")
//    public ResponseEntity<Map<String,Object>> getComments(
//            @RequestParam Long boardIdx
//    ) {
//        Map<String,Object> response = new HashMap<>();
//
//        List<GetCommentsRespDto> comments = commentService.getComments(boardIdx);
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String username = authentication.getName();
//        if(comments==null){
//            response.put("empty","EMC");
//        }
//        response.put("comments", comments);
//        response.put("boardIdx", boardIdx);
//
//        return ResponseEntity.ok().body(response);
//
//    }
    @GetMapping("/comments")
    public ResponseEntity<Map<String, Object>> getComments(
            @RequestParam Long boardIdx
    ) {
        Map<String, Object> response = new HashMap<>();

        // 댓글 조회
        List<GetCommentsRespDto> comments = commentService.getComments(boardIdx);

        // 로그인 상태 확인
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = null;

        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            username = authentication.getName(); // 로그인한 사용자의 이름
        }

        // 댓글이 없을 경우 처리
        if (comments == null || comments.isEmpty()) {
            response.put("empty", "EMC"); // No Comments
        } else {
            response.put("comments", comments); // 댓글이 존재할 경우 추가
        }

        // 게시글 ID 추가
        response.put("boardIdx", boardIdx);

        // 사용자 정보 추가 (선택사항)
        if (username != null) {
            response.put("username", username); // 로그인한 사용자 이름을 추가
        } else {
            response.put("username", "guest"); // 로그인하지 않은 경우
        }

        return ResponseEntity.ok().body(response);
    }



    @PostMapping("/comment")
    public ResponseEntity<String> postComment(
            @RequestBody PostCommentReqDto request
    ){

        commentService.postComment(request);


        String path = "/api/client/comments?boardIdx="+request.getBoardIdx();

        return ResponseEntity.ok(path);
    }

    @PutMapping("/comment")
    public ResponseEntity<String> putComment(
            @RequestBody PutCommentReqDto request
    ){
        commentService.updateComment(request);

        String path = "/api/client/comments?boardIdx=boardIdx";

        return ResponseEntity.ok().body(path);
    }

    @DeleteMapping("/comment")
    public ResponseEntity<String> deleteComment(
            @RequestParam Long commentIdx
    ){
        commentService.deleteComment(commentIdx);

        String path = "/api/client/comments?boardIdx=";

        return ResponseEntity.ok().body(path);
    }
}
