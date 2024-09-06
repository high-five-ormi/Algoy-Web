package com.example.algoyweb.controller.planner;

import com.example.algoyweb.model.dto.planner.PlannerDto;
import com.example.algoyweb.service.planner.PlannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/algoy/planner")
public class PlannerController {

    private final PlannerService plannerService;

    // ai-backend.url 설정값을 저장하는 변수입니다.
    @Value("${ai-backend.url}")
    private String backendUrl;

    // 메인 페이지에 들어갈 한달 사이의 플래너 목록을 불러오는 엔드포인트
    @GetMapping
    @PostAuthorize("hasAnyRole('ROLE_NORMAL', 'ROLE_ADMIN')")
    public ResponseEntity<List<PlannerDto>> getCalender(@RequestParam int year, @RequestParam int month) {
        // 지정된 연도와 월에 해당하는 플래너 목록을 서비스에서 가져옴
        List<PlannerDto> plannerDtoList = plannerService.getPlansMonth(year, month);

        // 가져온 플래너 목록을 HTTP 상태 코드 200(OK)와 함께 반환
        return ResponseEntity.status(HttpStatus.OK).body(plannerDtoList);
    }

    // 유저의 전체 플래너 목록을 불러오는 엔드포인트
    @GetMapping("/get/plans")
    @PostAuthorize("hasAnyRole('ROLE_NORMAL','ROLE_ADMIN')")
    public ResponseEntity<List<PlannerDto>> getPlanner(@AuthenticationPrincipal UserDetails user) {
        // 현재 인증된 사용자의 이름으로 플래너 목록을 서비스에서 가져옴
        List<PlannerDto> plannerDtoList = plannerService.getPlans(user.getUsername());

        // 가져온 플래너 목록을 HTTP 상태 코드 200(OK)와 함께 반환
        return ResponseEntity.status(HttpStatus.OK).body(plannerDtoList);
    }

    // 특정 플랜의 상세 정보를 가져오는 엔드포인트
    @GetMapping("/{id}")
    @PostAuthorize("hasAnyRole('ROLE_NORMAL', 'ROLE_ADMIN')")
    public ResponseEntity<PlannerDto> getPlan(@PathVariable Long id) {
        // ID로 특정 플랜의 상세 정보를 서비스에서 가져옴
        PlannerDto plannerDto = plannerService.getPlan(id);

        // 가져온 플랜 정보를 HTTP 상태 코드 200(OK)와 함께 반환
        return ResponseEntity.status(HttpStatus.OK).body(plannerDto);
    }

    // 플래너 저장 폼을 불러오는 엔드포인트
    @GetMapping("/save-form")
    @PostAuthorize("hasAnyRole('ROLE_NORMAL', 'ROLE_ADMIN')")
    public ModelAndView saveForm(Model model) {
        model.addAttribute("backendUrl", backendUrl);
        // 플래너 저장 폼 페이지로 이동
        return new ModelAndView("planner/SaveForm");
    }

    // 플래너 수정 폼을 불러오는 엔드포인트
    @GetMapping("/edit-form")
    @PostAuthorize("hasAnyRole('ROLE_NORMAL', 'ROLE_ADMIN')")
    public ModelAndView editForm(Model model, @RequestParam Long id) {
        model.addAttribute("backendUrl", backendUrl);
        // 플래너 수정 폼 페이지로 이동 (수정할 플랜 ID와 함께)
        return new ModelAndView("planner/EditForm");
    }

    // 플래너 메인 페이지를 불러오는 엔드포인트
    @GetMapping("/calender")
    @PostAuthorize("hasAnyRole('ROLE_NORMAL', 'ROLE_ADMIN')")
    public ModelAndView viewCalender(Model model) {
        model.addAttribute("backendUrl", backendUrl);
        // 플래너 메인 페이지로 이동
        return new ModelAndView("planner/PlannerMain");
    }

    // 새로운 플랜을 저장하는 엔드포인트
    @PostMapping("/save")
    @PostAuthorize("hasAnyRole('ROLE_NORMAL', 'ROLE_ADMIN')")
    public ResponseEntity<PlannerDto> savePlan(@RequestBody PlannerDto plannerDto, @AuthenticationPrincipal UserDetails userDetails) {
        // 새로운 플랜을 저장하고, 저장된 플랜 정보를 반환
        PlannerDto savedDto = plannerService.savePlan(plannerDto, userDetails.getUsername());

        // 저장된 플랜 정보를 HTTP 상태 코드 201(Created)와 함께 반환
        return ResponseEntity.status(HttpStatus.CREATED).body(savedDto);
    }

    // 기존 플랜을 수정하는 엔드포인트
    @PostMapping("/edit/{id}")
    @PostAuthorize("hasAnyRole('ROLE_NORMAL', 'ROLE_ADMIN')")
    public ResponseEntity<PlannerDto> editPlan(@RequestBody PlannerDto plannerDto, @PathVariable Long id , @AuthenticationPrincipal UserDetails userDetails) {
        // 플랜을 수정하고, 수정된 플랜 정보를 반환
        PlannerDto updatedDto = plannerService.updatePlan(plannerDto, id, userDetails.getUsername());

        // 수정된 플랜 정보를 HTTP 상태 코드 200(OK)와 함께 반환
        return ResponseEntity.status(HttpStatus.OK).body(updatedDto);
    }

    // 특정 플랜을 삭제하는 엔드포인트
    @PostMapping("/delete/{id}")
    @PostAuthorize("hasAnyRole('ROLE_NORMAL','ROLE_ADMIN')")
    public ResponseEntity<String> deletePlan(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        // 플랜을 삭제하고 완료 메시지를 반환
        plannerService.deletePlan(id, userDetails.getUsername());

        // 삭제 완료 메시지를 HTTP 상태 코드 200(OK)와 함께 반환
        return ResponseEntity.status(HttpStatus.OK).body("삭제 완료");
    }

    // 플랜을 검색하는 엔드포인트
    @GetMapping("/search")
    @PostAuthorize("hasAnyRole('ROLE_NORMAL', 'ROLE_ADMIN')")
    public ResponseEntity<List<PlannerDto>> search(@RequestParam String keyword) {

        List<PlannerDto> plannerDtoList = plannerService.searchPlans(keyword);

        return ResponseEntity.status(HttpStatus.OK).body(plannerDtoList);
    }
}
