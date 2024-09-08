package com.example.algoyweb.controller.WrongAnswerNote;

import com.example.algoyweb.model.dto.WrongAnswerNote.ImageDTO;
import com.example.algoyweb.model.dto.WrongAnswerNote.WrongAnswerNoteDTO;
import com.example.algoyweb.service.WrongAnswerNote.ImageService;
import com.example.algoyweb.service.WrongAnswerNote.WrongAnswerNoteService;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/algoy/commit")
@RequiredArgsConstructor
public class WrongAnswerNoteThymeleafController {

    private final WrongAnswerNoteService service;
    private final ImageService imageService;

    // 페이지네이션을 적용한 목록 조회
    @GetMapping
    public String getAllWrongAnswerNotes(@RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        Model model) {
        Page<WrongAnswerNoteDTO> notesPage = service.findAllWithPagination(page, size);
        model.addAttribute("notes", notesPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", notesPage.getTotalPages());
        return "wronganswernote/list-wrong-answer-notes";
    }

    // ID로 조회하는 메소드
    @GetMapping("/{id}")
    public String getWrongAnswerNoteById(@PathVariable Long id, Model model) {
        service.findById(id).ifPresent(note -> model.addAttribute("note", note));
        return "wronganswernote/view-wrong-answer-note";
    }

    // 오답노트 생성 폼
    @GetMapping("/create")
    public String createWrongAnswerNoteForm(Model model) {
        model.addAttribute("note", new WrongAnswerNoteDTO());
        return "wronganswernote/create-wrong-answer-note";
    }

    // 오답노트 생성
    @PostMapping("/create")
    public String createWrongAnswerNote(@ModelAttribute WrongAnswerNoteDTO dto) {
        // DTO 값 확인을 위해 로그 추가
        System.out.println("Received DTO: " + dto); // 로그를 통해 DTO 데이터 확인
        service.save(dto);
        return "redirect:/algoy/commit";
    }

    // 오답노트 수정 폼
    @GetMapping("/{id}/edit")
    public String editWrongAnswerNoteForm(@PathVariable Long id, Model model) {
        service.findById(id).ifPresent(note -> model.addAttribute("note", note));
        return "wronganswernote/edit-wrong-answer-note";
    }

    // 오답노트 수정 및 이미지 처리
    @PostMapping("/{id}/edit")
    public String editWrongAnswerNote(
        @PathVariable Long id,
        @ModelAttribute WrongAnswerNoteDTO dto,
        @RequestParam(value = "images", required = false) List<MultipartFile> imageFiles) throws IOException {

        service.update(id, dto);
        if (imageFiles != null && !imageFiles.isEmpty()) {
            for (MultipartFile file : imageFiles) {
                imageService.uploadImage(file);
            }
        }
        return "redirect:/algoy/commit/" + id;
    }
}