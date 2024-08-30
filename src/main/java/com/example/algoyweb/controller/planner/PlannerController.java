package com.example.algoyweb.controller.planner;

import com.example.algoyweb.model.dto.planner.PlannerDto;
import com.example.algoyweb.service.planner.PlannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/algoy/planner")
public class PlannerController {

    private final PlannerService plannerService;

    // 메인 페이지에 들어갈 한달 사이의 플래너 불러오기
    @GetMapping
    @PostAuthorize("hasAnyRole('ROLE_NORMAL', 'ROLE_ADMIN')")
    public ResponseEntity<List<PlannerDto>> getCalender(@RequestParam int year, @RequestParam int month) {
        List<PlannerDto> plannerDtoList = plannerService.getPlansMonth(year, month);

        return ResponseEntity.status(HttpStatus.OK).body(plannerDtoList);
    }

    // 유저의 플랜 전체 불러오기
    @GetMapping("/get/plans")
    @PostAuthorize("hasAnyRole('ROLE_NORMAL','ROLE_ADMIN')")
    public ResponseEntity<List<PlannerDto>> getPlanner(@AuthenticationPrincipal UserDetails user) {
       List<PlannerDto> plannerDtoList = plannerService.getPlans(user.getUsername());

       return ResponseEntity.status(HttpStatus.OK).body(plannerDtoList);
    }

    // 플랜 상세 가져오기
    @GetMapping("/{id}")
    @PostAuthorize("hasAnyRole('ROLE_NORMAL', 'ROLE_ADMIN')")
    public ResponseEntity<PlannerDto> getPlan(@PathVariable Long id) {
        PlannerDto plannerDto = plannerService.getPlan(id);

        return ResponseEntity.status(HttpStatus.OK).body(plannerDto);
    }

    // 플래너 저장 폼 불러오기
    @GetMapping("/save-form")
    @PostAuthorize("hasAnyRole('ROLE_NORMAL', 'ROLE_ADMIN')")
    public ModelAndView saveForm() {
        return new ModelAndView("planner/SaveForm");
    }

    // 플래너 수정 폼 불러오기
    @GetMapping("/edit-form")
    @PostAuthorize("hasAnyRole('ROLE_NORMAL', 'ROLE_ADMIN')")
    public ModelAndView editForm(@RequestParam Long id) {
        return new ModelAndView("planner/EditForm");
    }

    // 플래너 메인 페이지 불러오기
    @GetMapping("/calender")
    @PostAuthorize("hasAnyRole('ROLE_NORMAL', 'ROLE_ADMIN')")
    public ModelAndView viewCalender() {
        return new ModelAndView("planner/PlannerMain");
    }

    // 플랜 저장
    @PostMapping("/save")
    @PostAuthorize("hasAnyRole('ROLE_NORMAL', 'ROLE_ADMIN')")
    public ResponseEntity<PlannerDto> savePlan(@RequestBody PlannerDto plannerDto, @AuthenticationPrincipal UserDetails userDetails) {
        PlannerDto savedDto = plannerService.savePlan(plannerDto, userDetails.getUsername());

        return ResponseEntity.status(HttpStatus.CREATED).body(savedDto);
    }

    //플랜 수정
    @PostMapping("/edit/{id}")
    @PostAuthorize("hasAnyRole('ROLE_NORMAL', 'ROLE_ADMIN')")
    public ResponseEntity<PlannerDto> editPlan(@RequestBody PlannerDto plannerDto, @PathVariable Long id , @AuthenticationPrincipal UserDetails userDetails) {
        PlannerDto updatedDto = plannerService.updatePlan(plannerDto, id, userDetails.getUsername());

        return ResponseEntity.status(HttpStatus.OK).body(updatedDto);
    }

    // 플랜 삭제
    @PostMapping("/delete/{id}")
    @PostAuthorize("hasAnyRole('ROLE_NORMAL','ROLE_ADMIN')")
    public ResponseEntity<String> deletePlan(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        plannerService.deletePlan(id, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.OK).body("삭제 완료");
    }
}
