package com.example.algoyweb.controller.study;

import com.example.algoyweb.model.dto.study.StudyDto;
import com.example.algoyweb.model.entity.study.Study;
import com.example.algoyweb.service.study.StudyService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/algoy/study")
public class StudyController {

    private final StudyService studyService;

    @GetMapping("/gets")
    public ResponseEntity<Page<StudyDto>> getStudyList(@RequestParam int page, @RequestParam String keyword) {
        Page<StudyDto> studies = studyService.getStudyList(page, keyword);

        return ResponseEntity.status(HttpStatus.OK).body(studies);
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

    @GetMapping("/count")
    public ResponseEntity<Integer> countStudy(@RequestParam Long studyId) {
        return ResponseEntity.status(HttpStatus.OK).body(studyService.countParticipants(studyId));
    }

    @GetMapping("/search-status")
    public ResponseEntity<Page<StudyDto>> searchStatus(@RequestParam Study.Status status, @RequestParam int page) {
        Page<StudyDto> searchList = studyService.searchStatus(status, page);
        return ResponseEntity.status(HttpStatus.OK).body(searchList);
    }

    @GetMapping("/main")
    public ModelAndView getMain() {
        return new ModelAndView("study/StudyMain");
    }

    @GetMapping("/detail")
    public ModelAndView getDetail(@RequestParam Long studyId) {
        return new ModelAndView("study/StudyDetail");
    }

    @GetMapping("/edit-form")
    public ModelAndView getEdit(@RequestParam Long studyId) {
        return new ModelAndView("study/StudyEdit");
    }

    @GetMapping("/new-form")
    public ModelAndView getNew() {
        return new ModelAndView("study/StudyNew");
    }
}
