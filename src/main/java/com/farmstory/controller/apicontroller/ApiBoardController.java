package com.farmstory.controller.apicontroller;

import com.farmstory.requestdto.board.PostBoardReqDto;
import com.farmstory.requestdto.board.PutBoardReqDto;
import com.farmstory.responsedto.board.GetBoardRespDto;
import com.farmstory.responsedto.board.GetBoardsRespDto;
import com.farmstory.service.board.BoardService;
import com.farmstory.service.user.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/client")
public class ApiBoardController {

    private final BoardService boardService;
    private final UserService userService;

    public ApiBoardController(BoardService boardService, UserService userService) {
        this.boardService = boardService;
        this.userService = userService;
    }

    @PostMapping("/board")
    public ResponseEntity<String> postBoard(
            @RequestParam("board") String boardJson,
            @RequestPart(value = "file_one", required = false) MultipartFile fileOne,
            @RequestPart(value = "file_two", required = false) MultipartFile fileTwo

            ){
        ObjectMapper objectMapper = new ObjectMapper();
        PostBoardReqDto boardRequest;
        try {
            boardRequest = objectMapper.readValue(boardJson, PostBoardReqDto.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Invalid product data");
        }
        String path1 = null;
        String path2 = null;
        try {
            if (fileOne != null && !fileOne.isEmpty()) {
                path1 = boardService.saveFile(fileOne);
            }
            if (fileTwo != null && !fileTwo.isEmpty()) {
                path2 = boardService.saveFile(fileTwo);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String result = boardService.insertBoard(boardRequest,path1,path2);

        String path = "/client/boards?section=" + boardRequest.getSection() + "&type=" + boardRequest.getType();

        return ResponseEntity.ok(path);
    }

    @PutMapping("/board")
    public ResponseEntity<String> putBoard(
            @RequestParam("board") String boardJson,
            @RequestPart(value = "file_one", required = false) MultipartFile fileOne,
            @RequestPart(value = "file_two", required = false) MultipartFile fileTwo
    ){
        ObjectMapper objectMapper = new ObjectMapper();
        PutBoardReqDto boardRequest;
        try {
            boardRequest = objectMapper.readValue(boardJson, PutBoardReqDto.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Invalid product data");
        }
        String path1 = null;
        String path2 = null;
        try {
            if (fileOne != null && !fileOne.isEmpty()) {
                path1 = boardService.saveFile(fileOne);
            }
            if (fileTwo != null && !fileTwo.isEmpty()) {
                path2 = boardService.saveFile(fileTwo);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        boardService.updateBoard(boardRequest,path1,path2);

        String path = "/api/client/board?boardIdx="+boardRequest.getBoardIdx();

        return ResponseEntity.ok().body(path);
    }

    @DeleteMapping("/board")
    public ResponseEntity<String> deleteBoard(
            @RequestParam String section,
            @RequestParam String type,
            @RequestParam Long boardIdx
    ){
        boardService.deleteBoard(boardIdx);


        String path = "/client/boards?section=" + section + "&type=" + type;

        return ResponseEntity.ok().body(path);
    }

    @GetMapping("/board")
    public ResponseEntity<Map<String, Object>> getBoard(
            @RequestParam int boardIdx
    ) {
        Map<String, Object> map = new HashMap<>();

        // 게시글 조회
        GetBoardRespDto boardRespDto = boardService.getBoard(boardIdx);

        if (boardRespDto == null) {
            map.put("status", "NFB"); // No Found Board
            return ResponseEntity.ok().body(map);
        }

        // 로그인 상태 확인
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username ;
        String admin ;

        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            username = authentication.getName();
            admin = userService.checkRole(); // 관리자인지 확인
            map.put("admin", admin); // 관리자인 경우 정보를 추가

        } else {
            admin = "guest"; // 로그인하지 않은 경우 기본 역할 설정
        }

        if(authentication != null) {
            username = authentication.getName();
            System.out.println("=============================");
            System.out.println(username);
            System.out.println(boardRespDto.getUserId());
            System.out.println("=============================");
            if (username.equals(boardRespDto.getUserId())) {
                map.put("eq", "IDXEQUAL"); // 작성자와 현재 사용자 ID가 같음
            }
        }

        // 사용자 정보와 게시글 작성자 정보 비교


        // 게시글 정보 추가
        map.put("board", boardRespDto);
        map.put("status", "SUCCESS"); // 성공적으로 조회됨

        return ResponseEntity.ok().body(map);
    }

    //    @GetMapping("/board")
//    public ResponseEntity<Map<String, Object>> getBoard(
//            @RequestParam int boardIdx
//    ){
//        Map<String, Object> map = new HashMap<>();
//
//        GetBoardRespDto boardRespDto = boardService.getBoard(boardIdx);
//        String admin = userService.checkRole();
//        map.put("admin",admin);
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String username = authentication.getName();
//
//        if(boardRespDto == null){
//            map.put("status", "NFB");
//            return ResponseEntity.ok().body(map);
//        }
//        if(username.equals(boardRespDto.getUserId())){
//            map.put("status","IDXEQUAL");
//        }
//        map.put("board", boardRespDto);
//
//        return ResponseEntity.ok().body(map);
//    }
    @Value("${file.upload-dir}")
    private String uploadDir;

    @GetMapping("/board/download")
    public ResponseEntity<Resource> downloadBoard(
            @RequestParam long boardFileIdx
    ) throws IOException {


        String fileDirectory = uploadDir;

        String fileName = boardService.downloadBoard(boardFileIdx);

        Path filePath = Paths.get(fileDirectory).resolve(fileName).normalize();
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists()) {
            throw new FileNotFoundException("File not found " + fileName);
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

}
