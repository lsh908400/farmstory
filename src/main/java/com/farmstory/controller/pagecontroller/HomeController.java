package com.farmstory.controller.pagecontroller;

import com.farmstory.responsedto.board.GetBoardsRespDto;
import com.farmstory.responsedto.board.GetMainBoardsRespDto;
import com.farmstory.service.board.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final BoardService boardService;

    @GetMapping(value = {"/", "/index"})
    public String home(Model model) {
        int page = 0;
        Page<GetMainBoardsRespDto> cropGrow = boardService.selectMainBoardsGrow(page);
        Page<GetMainBoardsRespDto> cropSchool = boardService.selectMainBoardsSchool(page);
        Page<GetMainBoardsRespDto> cropStory = boardService.selectMainBoardsStory(page);
        model.addAttribute("cropGrow", cropGrow);
        model.addAttribute("cropSchool", cropSchool);
        model.addAttribute("cropStory", cropStory);

        Page<GetMainBoardsRespDto> notice = boardService.selectMainBoardsNotice(page);
        model.addAttribute("notice", notice);
        return "index";
    }
}
