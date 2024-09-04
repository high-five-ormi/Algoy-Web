package com.example.algoyweb.service.planner;

import com.example.algoyweb.exception.CustomException;
import com.example.algoyweb.exception.PlannerErrorCode;
import com.example.algoyweb.model.dto.planner.PlannerDto;
import com.example.algoyweb.model.entity.planner.Planner;
import com.example.algoyweb.model.entity.user.Role;
import com.example.algoyweb.model.entity.user.User;
import com.example.algoyweb.repository.planner.PlannerRepository;
import com.example.algoyweb.repository.user.UserRepository;
import com.example.algoyweb.util.ConvertUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PlannerServiceTest {

    @Mock
    private PlannerRepository plannerRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PlannerService plannerService;

    @Test
    public void testSavePlanner() {
        // Given
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        User user = User.builder()
                .email("user@naver.com")
                .password(passwordEncoder.encode("password"))
                .nickname("user")
                .role(Role.NORMAL)
                .username("user")
                .build();

        Planner planner = Planner.builder()
                .link("Link")
                .endAt(LocalDate.now())
                .status(Planner.Status.DONE)
                .startAt(LocalDate.now())
                .title("My Planner")
                .content("Description")
                .createAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .build();

        // Mocking
        when(userRepository.findByEmail("user@naver.com")).thenReturn(user);
        when(plannerRepository.save(any(Planner.class))).thenReturn(planner);

        // When
        PlannerDto savedPlanner = plannerService.savePlan(ConvertUtils.convertPlannerToDto(planner), "user@naver.com");


        // Then
        assertEquals("My Planner", savedPlanner.getTitle());
        assertEquals("Description", savedPlanner.getContent());
        assertEquals("Link", savedPlanner.getLink());

        verify(plannerRepository, times(1)).save(any(Planner.class));
    }

    @Test
    public void testDeletePlanner() {
        // Given
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        User user = User.builder()
                .email("user@naver.com")
                .password(passwordEncoder.encode("password"))
                .nickname("user")
                .role(Role.NORMAL)
                .username("user")
                .build();

        Planner planner = Planner.builder()
                .id(1L)
                .link("Link")
                .endAt(LocalDate.now())
                .status(Planner.Status.DONE)
                .startAt(LocalDate.now())
                .title("My Planner")
                .content("Description")
                .createAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .user(user)
                .build();

        // Mocking
        when(plannerRepository.findById(1L)).thenReturn(Optional.ofNullable(planner));

        // When
        plannerService.deletePlan(planner.getId(), "user@naver.com");

        // Then
        verify(plannerRepository, times(1)).delete(planner);
    }

    @Test
    public void testUpdatePlanner() {
        // Given
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        User user = User.builder()
                .email("user@naver.com")
                .password(passwordEncoder.encode("password"))
                .nickname("user")
                .role(Role.NORMAL)
                .username("user")
                .build();

        Planner planner = Planner.builder()
                .id(1L)
                .link("Link")
                .endAt(LocalDate.now())
                .status(Planner.Status.DONE)
                .startAt(LocalDate.now())
                .title("My Planner")
                .content("Description")
                .createAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .user(user)
                .build();

        // Mocking
        when(plannerRepository.findById(1L)).thenReturn(Optional.ofNullable(planner));

        // When
        Planner updatedPlanner = ConvertUtils.convertDtoToPlanner(plannerService.updatePlan(PlannerDto.builder()
            .link("Updated Link")
            .endAt(LocalDate.now())
            .status(Planner.Status.DONE)
            .startAt(LocalDate.now())
            .title("Updated Planner")
            .content("Updated Description")
            .createAt(LocalDateTime.now())
            .updateAt(LocalDateTime.now())
            .build(),
            planner.getId(), "user@naver.com"));

        // Then
        assertEquals("Updated Planner", updatedPlanner.getTitle());
        assertEquals("Updated Description", updatedPlanner.getContent());
        assertEquals("Updated Link", updatedPlanner.getLink());
    }

    @Test
    public void testGetAllPlansByUser() {
        // Given
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        User user = User.builder()
                .email("user@naver.com")
                .password(passwordEncoder.encode("password"))
                .nickname("user")
                .role(Role.NORMAL)
                .username("user")
                .build();

        // Create multiple planners
        Planner planner1 = Planner.builder()
                .id(1L)
                .link("Link1")
                .endAt(LocalDate.now().plusDays(1))
                .status(Planner.Status.IN_PROGRESS)
                .startAt(LocalDate.now())
                .title("Planner 1")
                .content("Description 1")
                .createAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .user(user)
                .build();

        Planner planner2 = Planner.builder()
                .id(2L)
                .link("Link2")
                .endAt(LocalDate.now().plusDays(2))
                .status(Planner.Status.IN_PROGRESS)
                .startAt(LocalDate.now())
                .title("Planner 2")
                .content("Description 2")
                .createAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .user(user)
                .build();

        List<Planner> planners = Arrays.asList(planner1, planner2);

        // Mocking the necessary methods
        when(plannerRepository.findByUserEmail(user.getEmail())).thenReturn(planners);

        // When
        List<PlannerDto> getPlanners = plannerService.getPlans("user@naver.com");

        // Then
        assertEquals(2, getPlanners.size());
        assertEquals("Planner 1", getPlanners.get(0).getTitle());
        assertEquals("Planner 2", getPlanners.get(1).getTitle());
        assertEquals("Description 1", getPlanners.get(0).getContent());
        assertEquals("Description 2", getPlanners.get(1).getContent());

        verify(plannerRepository, times(1)).findByUserEmail(user.getEmail());
    }

    @Test
    public void testGetPlanSuccess() {
        // Given
        User user = User.builder()
                .email("user@naver.com")
                .nickname("user")
                .role(Role.NORMAL)
                .username("user")
                .build();

        Planner planner = Planner.builder()
                .id(1L)
                .link("Link")
                .endAt(LocalDate.now().plusDays(1))
                .status(Planner.Status.IN_PROGRESS)
                .startAt(LocalDate.now())
                .title("My Planner")
                .content("Description")
                .createAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .user(user)
                .build();

        // Mocking
        when(plannerRepository.findById(1L)).thenReturn(Optional.of(planner));

        // When
        PlannerDto getPlanner = plannerService.getPlan(1L);

        // Then
        assertEquals("My Planner", getPlanner.getTitle());
        assertEquals("Description", getPlanner.getContent());
        assertEquals("Link", getPlanner.getLink());

        verify(plannerRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetPlanNotFound() {
        // Given
        Long plannerId = 1L;

        // Mocking
        when(plannerRepository.findById(plannerId)).thenReturn(Optional.empty());

        // When
        CustomException exception = assertThrows(CustomException.class, () -> {
            plannerService.getPlan(plannerId);
        });

        // Then
        assertEquals(PlannerErrorCode.PLAN_NOT_FOUND, exception.getErrorCode());

        verify(plannerRepository, times(1)).findById(plannerId);
    }

    @Test
    void testGetPlansMonthWithResults() {
        int year = 2024;
        int month = 9;

        LocalDate startTime = LocalDate.of(year, month, 1);
        LocalDate endTime = LocalDate.of(year, month, 30);  // September has 30 days

        // Given: 가짜 Planner 엔티티 리스트
        User user = User.builder()
                .email("user@naver.com")
                .nickname("user")
                .role(Role.NORMAL)
                .username("user")
                .build();

        Planner planner1 = Planner.builder()
                .id(1L)
                .link("Link")
                .endAt(endTime)
                .status(Planner.Status.IN_PROGRESS)
                .startAt(startTime.plusDays(1))
                .title("My Planner 1")
                .content("Description")
                .createAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .user(user)
                .build();

        Planner planner2 = Planner.builder()
                .id(1L)
                .link("Link")
                .endAt(endTime)
                .status(Planner.Status.IN_PROGRESS)
                .startAt(startTime.plusDays(5))
                .title("My Planner 2")
                .content("Description")
                .createAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .user(user)
                .build();
        List<Planner> planners = Arrays.asList(planner1, planner2);

        // When: 레포지토리 메소드를 모킹
        when(plannerRepository.findByMonth(startTime, endTime)).thenReturn(planners);

        // Then: 메소드를 호출하고 결과를 검증
        List<PlannerDto> result = plannerService.getPlansMonth(year, month);

        assertEquals(2, result.size());
        assertEquals("My Planner 1", result.get(0).getTitle());
        assertEquals("My Planner 2", result.get(1).getTitle());
    }

    @Test
    void testGetPlansMonthNoResults() {
        int year = 2024;
        int month = 9;

        LocalDate startTime = LocalDate.of(year, month, 1);
        LocalDate endTime = LocalDate.of(year, month, 30);

        // When: 레포지토리 메소드를 모킹
        when(plannerRepository.findByMonth(startTime, endTime)).thenReturn(Collections.emptyList());

        // Then: 메소드를 호출하고 결과를 검증
        List<PlannerDto> result = plannerService.getPlansMonth(year, month);

        assertEquals(0, result.size());
    }
}