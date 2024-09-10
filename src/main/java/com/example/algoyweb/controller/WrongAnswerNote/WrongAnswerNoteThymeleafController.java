package com.example.algoyweb.controller.WrongAnswerNote;

import com.example.algoyweb.model.dto.WrongAnswerNote.WrongAnswerNoteDTO;
import com.example.algoyweb.service.WrongAnswerNote.WrongAnswerNoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/algoy/commit")
@RequiredArgsConstructor
public class WrongAnswerNoteThymeleafController {

    private final WrongAnswerNoteService service;

    // ai-backend.url 설정값을 저장하는 변수입니다.
    @Value("${ai-backend.url}")
    private String backendUrl;

    // 페이지네이션을 적용한 목록 조회
    @GetMapping
    public String getAllWrongAnswerNotes(@RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "8") int size,
        Model model) {

        Page<WrongAnswerNoteDTO> notesPage = service.findAllWithPagination(page, size);
        int totalPages = notesPage.getTotalPages();

        // 페이지 그룹 계산
        int currentGroup = page / 10;
        int startPage = currentGroup * 10; // 현재 그룹의 시작 페이지
        int endPage = Math.min(startPage + 9, totalPages - 1); // 현재 그룹의 끝 페이지 수정됨

        // '이전' 및 '다음' 그룹 버튼 링크를 위한 페이지 계산
        int previousGroupStartPage = Math.max(0, startPage - 10);
        int nextGroupStartPage = Math.min(startPage + 10, totalPages - 1);

        // 모델에 필요한 데이터 추가
        model.addAttribute("notes", notesPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("previousGroupStartPage", previousGroupStartPage);
        model.addAttribute("nextGroupStartPage", nextGroupStartPage);
        model.addAttribute("backendUrl", backendUrl);

        return "wronganswernote/list-wrong-answer-notes";
    }

    // ID로 조회하는 메소드
    @GetMapping("/{id}")
    public String getWrongAnswerNoteById(@PathVariable Long id, Model model) {
        service.findById(id).ifPresent(note -> model.addAttribute("note", note));
        model.addAttribute("backendUrl", backendUrl);
        return "wronganswernote/view-wrong-answer-note";
    }

    // 오답노트 생성 폼
    @GetMapping("/create")
    public String createWrongAnswerNoteForm(Model model) {
        model.addAttribute("note", new WrongAnswerNoteDTO());
        model.addAttribute("backendUrl", backendUrl);
        return "wronganswernote/create-wrong-answer-note";
    }

    // 오답노트 생성
    @PostMapping("/create")
    public String createWrongAnswerNote(@ModelAttribute WrongAnswerNoteDTO dto) {
        WrongAnswerNoteDTO savedNote = service.save(dto);
        return "redirect:/algoy/commit/" + savedNote.getId();
    }

    // 오답노트 수정 폼
    @GetMapping("/{id}/edit")
    public String editWrongAnswerNoteForm(@PathVariable Long id, Model model) {
        service.findById(id).ifPresent(note -> model.addAttribute("note", note));
        model.addAttribute("backendUrl", backendUrl);
        return "wronganswernote/edit-wrong-answer-note";
    }

    // 오답노트 수정
    @PostMapping("/{id}/edit")
    public String editWrongAnswerNote(@PathVariable Long id, @ModelAttribute WrongAnswerNoteDTO dto) {
        service.update(id, dto);
        return "redirect:/algoy/commit/" + id;
    }
}