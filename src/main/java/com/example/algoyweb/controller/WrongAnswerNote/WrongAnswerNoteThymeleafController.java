package com.example.algoyweb.controller.WrongAnswerNote;

import com.example.algoyweb.model.dto.WrongAnswerNote.WrongAnswerNoteDTO;
import com.example.algoyweb.service.WrongAnswerNote.WrongAnswerNoteService;
import com.example.algoyweb.service.WrongAnswerNote.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Controller
@RequestMapping("/algoy/commit")
@RequiredArgsConstructor
public class WrongAnswerNoteThymeleafController {

    private final WrongAnswerNoteService service;
    private final ImageService imageService;

    // ai-backend.url 설정값을 저장하는 변수입니다.
    @Value("${ai-backend.url}")
    private String backendUrl;

    @GetMapping
    public String getAllWrongAnswerNotes(Model model) {
        model.addAttribute("notes", service.findAll());
        model.addAttribute("backendUrl", backendUrl);
        return "wronganswernote/list-wrong-answer-notes";
    }

    @GetMapping("/{id}")
    public String getWrongAnswerNoteById(@PathVariable Long id, Model model) {
        Optional<WrongAnswerNoteDTO> note = service.findById(id);
        if (note.isPresent()) {
            model.addAttribute("note", note.get());
            model.addAttribute("backendUrl", backendUrl);
            return "wronganswernote/view-wrong-answer-note";
        } else {
            return "error";
        }
    }

    @GetMapping("/create")
    public String createWrongAnswerNoteForm(Model model) {
        model.addAttribute("note", new WrongAnswerNoteDTO());
        model.addAttribute("backendUrl", backendUrl);
        return "wronganswernote/create-wrong-answer-note";
    }

    @PostMapping("/create")
    public String createWrongAnswerNote(
        @ModelAttribute WrongAnswerNoteDTO dto,
        @RequestParam(value = "image", required = false) MultipartFile imageFile) {
        service.save(dto);
        if (imageFile != null && !imageFile.isEmpty()) {
            imageService.uploadImage(imageFile);
        }
        return "redirect:/algoy/commit";
    }

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

    @PostMapping("/{id}/edit")
    public String editWrongAnswerNote(
        @PathVariable Long id,
        @ModelAttribute WrongAnswerNoteDTO dto,
        @RequestParam(value = "image", required = false) MultipartFile imageFile) {
        dto.setId(id); // DTO에 ID 설정
        service.update(id, dto);
        if (imageFile != null && !imageFile.isEmpty()) {
            imageService.uploadImage(imageFile);
        }
        return "redirect:/algoy/commit/" + id;
    }
}