package com.example.algoyweb.controller.study;

import com.example.algoyweb.model.dto.study.StudyDto;
import com.example.algoyweb.service.study.StudyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/algoy/study")
public class StudyController {

    private final StudyService studyService;

    @GetMapping("/gets")
    public ResponseEntity<List<StudyDto>> getStudyList(@RequestParam int page) {
        List<StudyDto> findList = studyService.getStudyList(page);

        return ResponseEntity.status(HttpStatus.OK).body(findList);
    }

    @PostMapping("/new")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_NORMAL')")
    public ResponseEntity<StudyDto> createStudy(@RequestBody StudyDto studyDto, @AuthenticationPrincipal UserDetails userDetails) {
        StudyDto newStudy = studyService.save(studyDto, userDetails.getUsername());

        return ResponseEntity.status(HttpStatus.CREATED).body(newStudy);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<StudyDto> getStudy(@PathVariable Long id) {

        StudyDto findStudy = studyService.getStudy(id);
        return ResponseEntity.status(HttpStatus.OK).body(findStudy);
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_NORMAL')")
    public ResponseEntity<String> deleteStudy(@PathVariable Long id) {
        studyService.deleteStudy(id);
        return ResponseEntity.status(HttpStatus.OK).body("삭제 완료");
    }

    @PostMapping("/update/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_NORMAL')")
    public ResponseEntity<StudyDto> updateStudy(@PathVariable Long id, @RequestBody StudyDto studyDto, @AuthenticationPrincipal UserDetails userDetails) {
        StudyDto updatedStudy = studyService.updateStudy(id, studyDto, userDetails.getUsername());

        return ResponseEntity.status(HttpStatus.OK).body(updatedStudy);
    }
}
