package com.example.algoyweb.controller.WrongAnswerNote;

import com.example.algoyweb.model.entity.WrongAnswerNote.Code;
import com.example.algoyweb.service.WrongAnswerNote.CodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/codes")
@RequiredArgsConstructor
public class CodeController {
    private final CodeService codeService;

    // 특정 오답노트의 코드 블록 불러오기
    @GetMapping("/{wrongAnswerNoteId}")
    public ResponseEntity<Map<String, Object>> getCodesByWrongAnswerNoteId(@PathVariable Long wrongAnswerNoteId) {
        List<Code> codes = codeService.getCodesByWrongAnswerNoteId(wrongAnswerNoteId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("codes", codes);
        return ResponseEntity.ok(response);
    }

    // 코드 블록 저장하기
    @PostMapping
    public ResponseEntity<Map<String, Object>> saveCode(@RequestBody Code code) {
        Code savedCode = codeService.saveCode(code);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("code", savedCode);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 코드 블록 수정하기
    @PutMapping("/{codeId}")
    public ResponseEntity<Map<String, Object>> updateCode(@PathVariable Long codeId, @RequestBody Code code) {
        Code updatedCode = codeService.updateCode(codeId, code);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("code", updatedCode);
        return ResponseEntity.ok(response);
    }

    // 코드 블록 삭제하기
    @DeleteMapping("/{codeId}")
    public ResponseEntity<Map<String, Object>> deleteCode(@PathVariable Long codeId) {
        boolean success = codeService.deleteCode(codeId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        return ResponseEntity.ok(response);
    }
}