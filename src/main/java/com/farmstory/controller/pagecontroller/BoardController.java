package com.farmstory.controller.pagecontroller;

import com.farmstory.entity.user.UserEntity;
import com.farmstory.repository.user.UserRepository;
import com.farmstory.responsedto.board.GetBoardsRespDto;
import com.farmstory.service.board.BoardService;
import com.farmstory.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.data.domain.Page;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collections;
import java.util.Optional;

@Controller
@RequestMapping("/client")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final UserService userService;
    private final UserRepository userRepository;

//    @GetMapping("/boards")
//    public ModelAndView getBoards(
//            @RequestParam(value="searchValue", defaultValue = "") String searchValue,
//            @RequestParam String section,
//            @RequestParam String type,
//            @RequestParam(value = "page", defaultValue = "0") int page,
//            @RequestParam(value = "myBoard", defaultValue = "") String myBoard
//    ){
//        if(searchValue.equals("")&&myBoard.equals("")){
//            ModelAndView mav = new ModelAndView();
//            mav.setViewName("pages/board/board_list");
//            mav.addObject("section", section);
//            mav.addObject("type", type);
//
//            Page<GetBoardsRespDto> boards = boardService.selectBoards(page,section,type);
//            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//            String username = authentication.getName();
//            mav.addObject("myBoard", myBoard);
//            String admin = userService.checkRole();
////        String owner = userService.checkBoardUser();
//            if(boards!=null){
//                mav.addObject("boards", boards);
//                mav.addObject("page", page);
//                mav.addObject("currentPage", page);
//                mav.addObject("totalPage", Math.ceil((double)boards.getTotalElements()/10));
//                mav.addObject("admin", admin);
//                mav.addObject("type", type);
//                mav.addObject("section", section);
//            } else {
//                mav.addObject("page", 0);
//                mav.addObject("currentPage", 0);
//                mav.addObject("totalPage", 0);
//                mav.addObject("admin", admin);
//                mav.addObject("type", type);
//                mav.addObject("section", section);
//            }
//
//            return mav;
//        } else if(myBoard.equals("1")){
//            ModelAndView mav = new ModelAndView();
//            mav.setViewName("pages/board/board_list");
//            mav.addObject("section", section);
//            mav.addObject("type", type);
//            mav.addObject("myBoard", myBoard);
//            Page<GetBoardsRespDto> boards = boardService.selectMyBoards(page,section,type);
//            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//            String username = authentication.getName();
//
//            String admin = userService.checkRole();
//            if(boards!=null){
//                mav.addObject("boards", boards);
//                mav.addObject("page", page);
//                mav.addObject("currentPage", page);
//                mav.addObject("totalPage", Math.ceil((double)boards.getTotalElements()/10));
//                mav.addObject("admin", admin);
//                mav.addObject("type", type);
//                mav.addObject("section", section);
//            } else {
//                mav.addObject("page", 0);
//                mav.addObject("currentPage", 0);
//                mav.addObject("totalPage", 0);
//                mav.addObject("admin", admin);
//                mav.addObject("type", type);
//                mav.addObject("section", section);
//            }
//
//            return mav;
//        } else {
//            Page<GetBoardsRespDto> boards = boardService.selectBoardsBySearch(page,searchValue,section,type);
//            ModelAndView mav = new ModelAndView();
//            mav.setViewName("pages/board/board_list");
//            mav.addObject("section", section);
//            mav.addObject("type", type);
//            mav.addObject("searchValue", searchValue);
//            mav.addObject("myBoard", myBoard);
//            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//            String username = authentication.getName();
//            String admin = userService.checkRole();
////        String owner = userService.checkBoardUser();
//            if(boards!=null){
//                mav.addObject("boards", boards);
//                mav.addObject("page", page);
//                mav.addObject("currentPage", page);
//                mav.addObject("totalPage", Math.ceil((double)boards.getTotalElements()/10));
//                mav.addObject("admin", admin);
//                mav.addObject("type", type);
//                mav.addObject("section", section);
//            } else {
//                mav.addObject("page", 0);
//                mav.addObject("currentPage", 0);
//                mav.addObject("totalPage", 0);
//                mav.addObject("admin", admin);
//                mav.addObject("type", type);
//                mav.addObject("section", section);
//            }
//
//            return mav;
//        }

//    }
@GetMapping("/boards")
public ModelAndView getBoards(
        @RequestParam(value = "searchValue", defaultValue = "") String searchValue,
        @RequestParam String section,
        @RequestParam String type,
        @RequestParam(value = "page", defaultValue = "0") int page,
        @RequestParam(value = "myBoard", defaultValue = "") String myBoard,
        @RequestParam(value = "boardIdx", defaultValue = "0") String boardIdx
) {
    ModelAndView mav = new ModelAndView();
    if(!boardIdx.equals("0")){
        mav.addObject("getBoardIdx",boardIdx);
    }
    mav.setViewName("pages/board/board_list");
    mav.addObject("section", section);
    mav.addObject("type", type);
    mav.addObject("myBoard", myBoard);

    // boards 데이터를 가져오기 위한 조건 처리
    Page<GetBoardsRespDto> boards;
    if (!myBoard.isEmpty() && myBoard.equals("1")) {
        boards = boardService.selectMyBoards(page, section, type); // 마이 게시물
    } else if (!searchValue.isEmpty()) {
        boards = boardService.selectBoardsBySearch(page, searchValue, section, type); // 검색된 게시물
        mav.addObject("searchValue", searchValue); // 검색 값 추가
    } else {
        boards = boardService.selectBoards(page, section, type); // 일반 게시물 목록
    }

    // 로그인 상태 확인
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String username = null;
    String admin = null;

    if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
        username = authentication.getName();
        admin = userService.checkRole(); // 관리자 역할 확인
        mav.addObject("admin", admin);
        mav.addObject("cantlogin","false");
    } else {
        admin = "false"; // 로그인하지 않은 경우 기본 역할 설정
        mav.addObject("admin", admin);
        mav.addObject("cantlogin","true");
    }

    if (boards != null && !boards.isEmpty()) {
        mav.addObject("boards", boards);
        mav.addObject("page", page);
        mav.addObject("currentPage", page);
        mav.addObject("totalPage", Math.ceil((double) boards.getTotalElements() / 10));
    } else {
        mav.addObject("boards", Collections.emptyList()); // 게시글이 없을 경우 빈 리스트
        mav.addObject("page", 0);
        mav.addObject("currentPage", 0);
        mav.addObject("totalPage", 0);
    }

    return mav;
}

}
