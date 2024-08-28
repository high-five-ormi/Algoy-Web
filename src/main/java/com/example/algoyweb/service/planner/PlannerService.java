package com.example.algoyweb.service.planner;

import com.example.algoyweb.model.entity.planner.Planner;
import com.example.algoyweb.model.dto.planner.PlannerDto;
import com.example.algoyweb.exception.CustomException;
import com.example.algoyweb.exception.PlannerErrorCode;
import com.example.algoyweb.repository.planner.PlannerRepository;
import com.example.algoyweb.repository.user.UserRepository;
import com.example.algoyweb.util.ConvertUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class PlannerService {

    private final PlannerRepository plannerRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<PlannerDto> getPlansMonth(int year, int month) {
        LocalDateTime startTime = LocalDateTime.of(year, month, 1, 0, 0);

        YearMonth yearMonth = YearMonth.from(startTime);
        int lastDayOfMonth = yearMonth.lengthOfMonth();
        LocalDateTime endTime = LocalDateTime.of(year, month, lastDayOfMonth, 0, 0);

        return Optional.of(plannerRepository.findByMonth(startTime, endTime)).orElseGet(Collections::emptyList).stream()
                .map(ConvertUtils::convertPlannerToDto).toList();
    }

    @Transactional(readOnly = true)
    public PlannerDto getPlan(Long id) {

        return ConvertUtils.convertPlannerToDto(
                plannerRepository.findById(id).orElseThrow(() -> new CustomException(PlannerErrorCode.PLAN_NOT_FOUND)));
    }

    public PlannerDto savePlan(PlannerDto plannerDto, String username) {
        Planner planner = ConvertUtils.convertDtoToPlanner(plannerDto);
        planner.connectUser(userRepository.findByEmail(username));
        return ConvertUtils.convertPlannerToDto(plannerRepository.save(planner));
    }

    public PlannerDto updatePlan(PlannerDto plannerDto, Long id, String username) {
        Planner findPlanner = plannerRepository.findById(id).orElseThrow(() -> new CustomException(PlannerErrorCode.PLAN_NOT_FOUND));
        /*if(findPlanner.getUser().getEmail != username) {
            throw new CustomException(PlannerErrorCode.PLAN_NOT_EQUAL_ID);
        }*/
        findPlanner.updatePlan(plannerDto);
        return ConvertUtils.convertPlannerToDto(findPlanner);
    }

    public void deletePlan(Long id, String username) {
        Planner findPlanner = plannerRepository.findById(id).orElseThrow(() -> new CustomException(PlannerErrorCode.PLAN_NOT_FOUND));
        /*if(findPlanner.getUser().getEmail != username) {
            throw new CustomException(PlannerErrorCode.PLAN_NOT_EQUAL_ID);
        }*/
        plannerRepository.delete(findPlanner);
    }

    public List<PlannerDto> getPlans(String username) {
        return plannerRepository.findByUserEmail(username).stream()
                .map(ConvertUtils::convertPlannerToDto).toList();
    }
}
