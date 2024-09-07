package com.example.algoyweb.service.planner;

import com.example.algoyweb.model.entity.planner.Planner;
import com.example.algoyweb.model.dto.planner.PlannerDto;
import com.example.algoyweb.exception.CustomException;
import com.example.algoyweb.exception.errorcode.PlannerErrorCode;
import com.example.algoyweb.repository.planner.PlannerRepository;
import com.example.algoyweb.repository.user.UserRepository;
import com.example.algoyweb.util.ConvertUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class PlannerService {

    private final PlannerRepository plannerRepository;
    private final UserRepository userRepository;

    // 지정된 연도와 월에 해당하는 플래너 목록을 가져오는 메서드
    @Transactional(readOnly = true)
    public List<PlannerDto> getPlansMonth(int year, int month) {
        // 연도와 월의 첫날을 시작 날짜로 설정
        LocalDate startTime = LocalDate.of(year, month, 1);

        // 주어진 연도와 월에 해당하는 YearMonth 객체를 생성
        YearMonth yearMonth = YearMonth.from(startTime);
        // 해당 월의 마지막 날짜를 구함
        int lastDayOfMonth = yearMonth.lengthOfMonth();
        // 연도와 월의 마지막 날을 종료 날짜로 설정
        LocalDate endTime = LocalDate.of(year, month, lastDayOfMonth);

        // 지정된 기간 내의 플래너 목록을 가져오고, DTO로 변환하여 반환
        return Optional.of(plannerRepository.findByMonth(startTime, endTime))
                .orElseGet(Collections::emptyList).stream()
                .map(ConvertUtils::convertPlannerToDto).toList();
    }

    // 특정 ID에 해당하는 플래너의 상세 정보를 가져오는 메서드
    @Transactional(readOnly = true)
    public PlannerDto getPlan(Long id) {
        // ID로 플래너를 조회하고, 없으면 예외를 발생시킴
        return ConvertUtils.convertPlannerToDto(
                plannerRepository.findById(id)
                        .orElseThrow(() -> new CustomException(PlannerErrorCode.PLAN_NOT_FOUND)));
    }

    // 새로운 플래너를 저장하는 메서드
    public PlannerDto savePlan(PlannerDto plannerDto, String username) {
        // DTO를 플래너 엔티티로 변환
        Planner planner = ConvertUtils.convertDtoToPlanner(plannerDto);
        // 해당 유저를 플래너와 연결
        planner.connectUser(userRepository.findByEmail(username));
        // 플래너를 저장하고, 저장된 엔티티를 DTO로 변환하여 반환
        return ConvertUtils.convertPlannerToDto(plannerRepository.save(planner));
    }

    // 기존 플래너를 수정하는 메서드
    public PlannerDto updatePlan(PlannerDto plannerDto, Long id, String username) {
        // ID로 플래너를 조회하고, 없으면 예외를 발생시킴
        Planner findPlanner = plannerRepository.findById(id)
                .orElseThrow(() -> new CustomException(PlannerErrorCode.PLAN_NOT_FOUND));

        // 플래너 소유자가 현재 사용자와 일치하지 않으면 예외를 발생시킴
        if (!Objects.equals(findPlanner.getUser().getEmail(), username)) {
            throw new CustomException(PlannerErrorCode.PLAN_NOT_EQUAL_ID);
        }

        // 플래너 정보를 업데이트
        findPlanner.updatePlan(plannerDto);
        // 업데이트된 플래너를 DTO로 변환하여 반환
        return ConvertUtils.convertPlannerToDto(findPlanner);
    }

    // 특정 플래너를 삭제하는 메서드
    public void deletePlan(Long id, String username) {
        // ID로 플래너를 조회하고, 없으면 예외를 발생시킴
        Planner findPlanner = plannerRepository.findById(id)
                .orElseThrow(() -> new CustomException(PlannerErrorCode.PLAN_NOT_FOUND));

        // 플래너 소유자가 현재 사용자와 일치하지 않으면 예외를 발생시킴
        if (!Objects.equals(findPlanner.getUser().getEmail(), username)) {
            throw new CustomException(PlannerErrorCode.PLAN_NOT_EQUAL_ID);
        }

        // 플래너를 삭제
        plannerRepository.delete(findPlanner);
    }

    // 특정 사용자의 모든 플래너를 가져오는 메서드
    @Transactional(readOnly = true)
    public List<PlannerDto> getPlans(String username) {

        // 사용자 이메일로 플래너를 조회하고, DTO로 변환하여 반환
        List<PlannerDto> plannerDtoList = plannerRepository.findByUserEmail(username).stream()
                .map(ConvertUtils::convertPlannerToDto).toList();

        List<PlannerDto> tempList = new ArrayList<>();
        List<PlannerDto> orderedList = new ArrayList<>();

        int i, j;
        for (i = 0; i < plannerDtoList.size(); i++) {
            if(plannerDtoList.get(i).getStatus() == Planner.Status.IN_PROGRESS) {
                tempList.add(plannerDtoList.get(i));
            }
        }
        orderedList.addAll(sortPlans(tempList));

        tempList = new ArrayList<>();

        for (i = 0; i < plannerDtoList.size(); i++) {
            if(plannerDtoList.get(i).getStatus() == Planner.Status.TODO) {
                tempList.add(plannerDtoList.get(i));
            }
        }
        orderedList.addAll(sortPlans(tempList));

        tempList = new ArrayList<>();

        for (i = 0; i < plannerDtoList.size(); i++) {
            if(plannerDtoList.get(i).getStatus() == Planner.Status.DONE) {
                tempList.add(plannerDtoList.get(i));
            }
        }
        orderedList.addAll(sortPlans(tempList));


        return orderedList;
    }

    private List<PlannerDto> sortPlans(List<PlannerDto> plannerDtoList) {
        int i, j;
        PlannerDto key;
        for(i = 1; i < plannerDtoList.size(); i++) {
            key = plannerDtoList.get(i);
            for(j = i - 1; j >= 0 && plannerDtoList.get(j).getStartAt().isAfter(key.getStartAt()); j--){
                plannerDtoList.set(j + 1, plannerDtoList.get(j)); // 레코드의 오른쪽으로 이동
            }
            plannerDtoList.set(j + 1, key);
        }
        return plannerDtoList;
    }

    @Transactional(readOnly = true)
    public List<PlannerDto> searchPlans(String keyword) {

        List<PlannerDto> plannerDtoList = plannerRepository.findByKeyword(keyword).stream()
                .map(ConvertUtils::convertPlannerToDto).toList();

        List<PlannerDto> tempList = new ArrayList<>();
        List<PlannerDto> orderedList = new ArrayList<>();

        int i, j;
        for (i = 0; i < plannerDtoList.size(); i++) {
            if(plannerDtoList.get(i).getStatus() == Planner.Status.IN_PROGRESS) {
                tempList.add(plannerDtoList.get(i));
            }
        }
        orderedList.addAll(sortPlans(tempList));

        tempList = new ArrayList<>();

        for (i = 0; i < plannerDtoList.size(); i++) {
            if(plannerDtoList.get(i).getStatus() == Planner.Status.TODO) {
                tempList.add(plannerDtoList.get(i));
            }
        }
        orderedList.addAll(sortPlans(tempList));

        tempList = new ArrayList<>();

        for (i = 0; i < plannerDtoList.size(); i++) {
            if(plannerDtoList.get(i).getStatus() == Planner.Status.DONE) {
                tempList.add(plannerDtoList.get(i));
            }
        }
        orderedList.addAll(sortPlans(tempList));


        return orderedList;
    }
}
