package com.example.algoyweb.controller.WrongAnswerNote;

import com.example.algoyweb.model.dto.WrongAnswerNote.WrongAnswerNoteDTO;
import com.example.algoyweb.service.WrongAnswerNote.WrongAnswerNoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/algoy/commit")
@RequiredArgsConstructor
public class WrongAnswerNoteController {

    private final WrongAnswerNoteService service;

    // 오답 노트 목록 조회
    @GetMapping
    public String getAllWrongAnswerNotes(Model model) {
        List<WrongAnswerNoteDTO> notes = service.findAll();
        model.addAttribute("notes", notes);
        return "list-wrong-answer-notes";
    }

    // 오답 노트 상세 조회
    @GetMapping("/{id}")
    public String getWrongAnswerNoteById(@PathVariable Long id, Model model) {
        Optional<WrongAnswerNoteDTO> note = service.findById(id);
        if (note.isPresent()) {
            model.addAttribute("note", note.get());
            return "view-wrong-answer-note";
        } else {
            return "error"; // 에러 페이지 처리
        }
    }

    // 새로운 오답 노트 생성 폼
    @GetMapping("/create")
    public String createWrongAnswerNoteForm(Model model) {
        model.addAttribute("note", new WrongAnswerNoteDTO());
        return "create-wrong-answer-note"; // 오답 노트 생성 페이지로 이동
    }

    // 새로운 오답 노트 저장
    @PostMapping
    public String createWrongAnswerNote(@ModelAttribute WrongAnswerNoteDTO dto) {
        System.out.println("게시물 저장 요청: " + dto); // 디버깅 로그 추가
        service.save(dto);
        return "redirect:/algoy/commit"; // 저장 후 목록으로 리다이렉트
    }

    // 오답 노트 수정 폼
    @GetMapping("/{id}/edit")
    public String editWrongAnswerNoteForm(@PathVariable Long id, Model model) {
        Optional<WrongAnswerNoteDTO> note = service.findById(id);
        if (note.isPresent()) {
            model.addAttribute("note", note.get());
            return "edit-wrong-answer-note"; // 수정 페이지로 이동
        } else {
            return "error"; // 에러 페이지 처리
        }
    }

    // 오답 노트 수정 처리
    @PostMapping("/{id}")
    public String updateWrongAnswerNote(@PathVariable Long id, @ModelAttribute WrongAnswerNoteDTO dto) {
        dto.setId(id); // DTO에 ID 설정
        service.save(dto); // 저장 (수정)
        return "redirect:/algoy/commit"; // 수정 후 목록으로 리다이렉트
    }

    // 오답 노트 삭제
    @GetMapping("/{id}/delete")
    public String deleteWrongAnswerNoteById(@PathVariable Long id) {
        service.deleteById(id); // 삭제 서비스 호출
        return "redirect:/algoy/commit"; // 삭제 후 목록으로 리다이렉트
    }
}