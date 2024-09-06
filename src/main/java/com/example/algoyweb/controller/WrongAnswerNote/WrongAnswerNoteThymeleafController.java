package com.example.algoyweb.controller.WrongAnswerNote;

import com.example.algoyweb.model.dto.WrongAnswerNote.WrongAnswerNoteDTO;
import com.example.algoyweb.service.WrongAnswerNote.WrongAnswerNoteService;
import com.example.algoyweb.service.WrongAnswerNote.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/algoy/commit")
@RequiredArgsConstructor
public class WrongAnswerNoteThymeleafController {

    private final WrongAnswerNoteService service;
    private final ImageService imageService;

    // 오답 노트 목록 조회
    @GetMapping
    public String getAllWrongAnswerNotes(Model model) {
        model.addAttribute("notes", service.findAll());
        return "wronganswernote/list-wrong-answer-notes"; // 타임리프 템플릿 이름
    }

    // 오답 노트 상세 조회
    @GetMapping("/{id}")
    public String getWrongAnswerNoteById(@PathVariable Long id, Model model) {
        Optional<WrongAnswerNoteDTO> note = service.findById(id);
        if (note.isPresent()) {
            model.addAttribute("note", note.get());
            return "wronganswernote/view-wrong-answer-note"; // 타임리프 템플릿 이름
        } else {
            return "error";
        }
    }

    // 오답 노트 생성 폼 표시
    @GetMapping("/create")
    public String createWrongAnswerNoteForm(Model model) {
        model.addAttribute("note", new WrongAnswerNoteDTO());
        return "wronganswernote/create-wrong-answer-note"; // 타임리프 템플릿 이름
    }

    // 새로운 오답 노트 저장
    @PostMapping("/create")
    public String createWrongAnswerNote(
        @ModelAttribute WrongAnswerNoteDTO dto,
        @RequestParam(value = "images", required = false) List<MultipartFile> imageFiles)
        throws IOException {
        // 오답 노트 저장
        service.save(dto, imageFiles); // 이미지 파일도 함께 처리
        return "redirect:/algoy/commit";
    }

    // 오답 노트 수정 폼 표시
    @GetMapping("/{id}/edit")
    public String editWrongAnswerNoteForm(@PathVariable Long id, Model model) {
        Optional<WrongAnswerNoteDTO> note = service.findById(id);
        if (note.isPresent()) {
            model.addAttribute("note", note.get());
            return "wronganswernote/edit-wrong-answer-note"; // 타임리프 템플릿 이름
        } else {
            return "error";
        }
    }

    // 오답 노트 수정 처리
    @PostMapping("/{id}/edit")
    public String editWrongAnswerNote(
        @PathVariable Long id,
        @ModelAttribute WrongAnswerNoteDTO dto,
        @RequestParam(value = "images", required = false) List<MultipartFile> imageFiles)
        throws IOException {
        // DTO에 ID 설정
        dto.setId(id);

        // 오답 노트 수정 및 이미지 파일 처리
        service.update(id, dto, imageFiles);
        return "redirect:/algoy/commit/" + id;
    }
}