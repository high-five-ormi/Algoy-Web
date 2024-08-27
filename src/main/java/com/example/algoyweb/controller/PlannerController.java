package com.example.algoyweb.controller;

import com.example.algoyweb.dto.PlannerDto;
import com.example.algoyweb.service.PlannerService;
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
@RequestMapping("/planner")
public class PlannerController {

    private final PlannerService plannerService;

    @GetMapping
    @PostAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<List<PlannerDto>> getCalender(@RequestParam int year, @RequestParam int month) {
        List<PlannerDto> plannerDtoList = plannerService.getPlansMonth(year, month);

        return ResponseEntity.status(HttpStatus.OK).body(plannerDtoList);
    }

    @GetMapping("/{id}")
    @PostAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<PlannerDto> getPlan(@PathVariable Long id) {
        PlannerDto plannerDto = plannerService.getPlan(id);

        return ResponseEntity.status(HttpStatus.OK).body(plannerDto);
    }

    @GetMapping("/save-form")
    @PostAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ModelAndView saveForm() {
        return new ModelAndView("planner/SaveForm");
    }

    @GetMapping("/calender")
    //@PostAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ModelAndView viewCalender() {
        return new ModelAndView("planner/PlannerMain");
    }

    @PostMapping("/save")
    @PostAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<PlannerDto> savePlan(@RequestBody PlannerDto plannerDto) {
        PlannerDto savedDto = plannerService.savePlan(plannerDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedDto);
    }

    @PostMapping("/edit/{id}")
    @PostAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<PlannerDto> editPlan(@RequestBody PlannerDto plannerDto, @PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        PlannerDto updatedDto = plannerService.updatePlan(plannerDto, id, userDetails.getUsername());

        return ResponseEntity.status(HttpStatus.OK).body(updatedDto);
    }

    @PostMapping("/delete/{id}")
    @PostAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    public ResponseEntity<String> deletePlan(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        plannerService.deletePlan(id, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.OK).body("삭제 완료");
    }
}
