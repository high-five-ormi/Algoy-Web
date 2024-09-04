package com.example.algoyweb.controller.WrongAnswerNote;

import com.example.algoyweb.model.dto.WrongAnswerNote.WrongAnswerNoteDTO;
import com.example.algoyweb.service.WrongAnswerNote.WrongAnswerNoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/algoy/commit")
@RequiredArgsConstructor
public class WrongAnswerNoteRestController {

    private final WrongAnswerNoteService service;

    // 오답 노트 목록 조회
    @GetMapping
    public ResponseEntity<List<WrongAnswerNoteDTO>> getAllWrongAnswerNotes() {
        List<WrongAnswerNoteDTO> notes = service.findAll();
        return ResponseEntity.ok(notes);
    }

    // 오답 노트 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<WrongAnswerNoteDTO> getWrongAnswerNoteById(@PathVariable Long id) {
        Optional<WrongAnswerNoteDTO> note = service.findById(id);
        return note.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 새로운 오답 노트 저장
    @PostMapping
    public ResponseEntity<WrongAnswerNoteDTO> createWrongAnswerNote(
        @RequestBody WrongAnswerNoteDTO dto) {
        WrongAnswerNoteDTO savedNote = service.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedNote);
    }

    // 오답 노트 수정 처리
    @PutMapping("/{id}")
    public ResponseEntity<WrongAnswerNoteDTO> updateWrongAnswerNote(@PathVariable Long id,
        @RequestBody WrongAnswerNoteDTO dto) {
        dto.setId(id); // DTO에 ID 설정
        WrongAnswerNoteDTO updatedNote = service.save(dto); // 저장 (수정)
        return ResponseEntity.ok(updatedNote);
    }

    // 오답 노트 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWrongAnswerNoteById(@PathVariable Long id) {
        service.deleteById(id); // 삭제 서비스 호출
        return ResponseEntity.noContent().build(); // 삭제 후 응답 바디 없음
    }
}