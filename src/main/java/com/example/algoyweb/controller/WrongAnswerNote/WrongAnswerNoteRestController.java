package com.example.algoyweb.controller.WrongAnswerNote;

import com.example.algoyweb.model.dto.WrongAnswerNote.WrongAnswerNoteDTO;
import com.example.algoyweb.service.WrongAnswerNote.WrongAnswerNoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/algoy/commit")
@RequiredArgsConstructor
public class WrongAnswerNoteRestController {

    private final WrongAnswerNoteService service;

    // 페이지네이션을 지원하는 오답노트 목록 조회 (현재 로그인한 사용자만)
    @GetMapping
    @PostAuthorize("hasAnyRole('ROLE_NORMAL', 'ROLE_ADMIN')")
    public ResponseEntity<Page<WrongAnswerNoteDTO>> getAllWrongAnswerNotes(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @AuthenticationPrincipal UserDetails user) {
        Page<WrongAnswerNoteDTO> notes = service.findAllWithPagination(page, size,
            user.getUsername());
        return ResponseEntity.ok(notes);
    }

    // ID로 오답노트 조회 (현재 로그인한 사용자만)
    @GetMapping("/{id}")
    @PostAuthorize("hasAnyRole('ROLE_NORMAL', 'ROLE_ADMIN')")
    public ResponseEntity<WrongAnswerNoteDTO> getWrongAnswerNoteById(
        @PathVariable Long id,
        @AuthenticationPrincipal UserDetails user) {
        Optional<WrongAnswerNoteDTO> note = service.findById(id, user.getUsername());
        return note.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 오답노트 생성 (현재 로그인한 사용자만)
    @PostMapping
    @PostAuthorize("hasAnyRole('ROLE_NORMAL', 'ROLE_ADMIN')")
    public ResponseEntity<WrongAnswerNoteDTO> createWrongAnswerNote(
        @RequestBody WrongAnswerNoteDTO dto,
        @AuthenticationPrincipal UserDetails user) {
        WrongAnswerNoteDTO savedNote = service.save(dto, user.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedNote);
    }

    // 오답노트 수정 (현재 로그인한 사용자만)
    @PutMapping("/{id}")
    @PostAuthorize("hasAnyRole('ROLE_NORMAL', 'ROLE_ADMIN')")
    public ResponseEntity<WrongAnswerNoteDTO> updateWrongAnswerNote(
        @PathVariable Long id,
        @RequestBody WrongAnswerNoteDTO dto,
        @AuthenticationPrincipal UserDetails user) {
        WrongAnswerNoteDTO updatedNote = service.update(id, dto, user.getUsername());
        return ResponseEntity.ok(updatedNote);
    }

    // 오답노트 삭제 (현재 로그인한 사용자만)
    @DeleteMapping("/{id}")
    @PostAuthorize("hasAnyRole('ROLE_NORMAL', 'ROLE_ADMIN')")
    public ResponseEntity<Void> deleteWrongAnswerNote(
        @PathVariable Long id,
        @AuthenticationPrincipal UserDetails user) {
        service.deleteById(id, user.getUsername());
        return ResponseEntity.noContent().build();
    }

    // 검색기능 추가 (현재 로그인한 사용자만)
    @GetMapping("/search")
    @PostAuthorize("hasAnyRole('ROLE_NORMAL', 'ROLE_ADMIN')")
    public ResponseEntity<List<WrongAnswerNoteDTO>> searchWrongAnswerNotes(
        @RequestParam String query,
        @AuthenticationPrincipal UserDetails user) {
        List<WrongAnswerNoteDTO> notes = service.search(query, user.getUsername());
        return ResponseEntity.ok(notes);
    }
}