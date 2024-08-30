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
@RequestMapping("/thymeleaf")
@RequiredArgsConstructor
public class WrongAnswerNoteThymeleafController {

    private final WrongAnswerNoteService service;

    @GetMapping
    public String getAllWrongAnswerNotes(Model model) {
        List<WrongAnswerNoteDTO> notes = service.findAll();
        model.addAttribute("notes", notes);
        return "list-wrong-answer-notes";
    }

    @GetMapping("/commit/{id}")
    public String getWrongAnswerNoteById(@PathVariable Long id, Model model) {
        Optional<WrongAnswerNoteDTO> note = service.findById(id);
        if (note.isPresent()) {
            model.addAttribute("note", note.get());
            return "view-wrong-answer-note";
        } else {
            return "error";
        }
    }

    @GetMapping("/create")
    public String createWrongAnswerNoteForm(Model model) {
        model.addAttribute("note", new WrongAnswerNoteDTO());
        return "create-wrong-answer-note";
    }

    @PostMapping("/create")
    public String createWrongAnswerNote(@ModelAttribute WrongAnswerNoteDTO dto) {
        service.save(dto);
        return "redirect:/thymeleaf"; // 경로를 /thymeleaf로 수정
    }

    @GetMapping("/{id}/edit")
    public String editWrongAnswerNoteForm(@PathVariable Long id, Model model) {
        Optional<WrongAnswerNoteDTO> note = service.findById(id);
        if (note.isPresent()) {
            model.addAttribute("note", note.get());
            return "edit-wrong-answer-note";
        } else {
            return "error";
        }
    }

    @PostMapping("/{id}")
    public String updateWrongAnswerNote(@PathVariable Long id, @ModelAttribute WrongAnswerNoteDTO dto) {
        dto.setId(id);
        service.save(dto);
        return "redirect:/thymeleaf";
    }

    @GetMapping("/{id}/delete")
    public String deleteWrongAnswerNoteById(@PathVariable Long id) {
        service.deleteById(id);
        return "redirect:/thymeleaf";
    }
}