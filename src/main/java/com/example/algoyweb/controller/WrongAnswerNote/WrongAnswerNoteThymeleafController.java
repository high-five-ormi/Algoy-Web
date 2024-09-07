package com.example.algoyweb.controller.WrongAnswerNote;

import com.example.algoyweb.model.dto.WrongAnswerNote.WrongAnswerNoteDTO;
import com.example.algoyweb.service.WrongAnswerNote.WrongAnswerNoteService;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
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

    // ID로 조회하는 메소드 (기존)
    @GetMapping("/{id}")
    public String getWrongAnswerNoteById(@PathVariable Long id, Model model) {
        Optional<WrongAnswerNoteDTO> note = service.findById(id);
        if (note.isPresent()) {
            model.addAttribute("note", note.get());
            return "wronganswernote/view-wrong-answer-note";
        } else {
            return "error";
        }
    }

    // 오답노트 생성 폼 (기존)
    @GetMapping("/create")
    public String createWrongAnswerNoteForm(Model model) {
        model.addAttribute("note", new WrongAnswerNoteDTO());
        return "wronganswernote/create-wrong-answer-note";
    }

    // 오답노트 생성 (기존)
    @PostMapping("/create")
    public String createWrongAnswerNote(
        @ModelAttribute WrongAnswerNoteDTO dto,
        @RequestParam(value = "images", required = false) List<MultipartFile> imageFiles) throws IOException {

        service.save(dto, imageFiles);
        return "redirect:/algoy/commit";
    }

    // 오답노트 수정 폼 (기존)
    @GetMapping("/{id}/edit")
    public String editWrongAnswerNoteForm(@PathVariable Long id, Model model) {
        Optional<WrongAnswerNoteDTO> note = service.findById(id);
        if (note.isPresent()) {
            model.addAttribute("note", note.get());
            return "wronganswernote/edit-wrong-answer-note";
        } else {
            return "error";
        }
    }

    // 오답노트 수정 (기존)
    @PostMapping("/{id}/edit")
    public String editWrongAnswerNote(
        @PathVariable Long id,
        @ModelAttribute WrongAnswerNoteDTO dto,
        @RequestParam(value = "images", required = false) List<MultipartFile> imageFiles) throws IOException {

        dto.setId(id);
        service.update(id, dto, imageFiles);
        return "redirect:/algoy/commit/" + id;
    }
}