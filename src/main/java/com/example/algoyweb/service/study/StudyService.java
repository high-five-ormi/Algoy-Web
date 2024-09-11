package com.example.algoyweb.service.study;

import com.example.algoyweb.exception.CustomException;
import com.example.algoyweb.exception.errorcode.StudyErrorCode;
import com.example.algoyweb.exception.errorcode.UserErrorCode;
import com.example.algoyweb.model.dto.study.StudyDto;
import com.example.algoyweb.model.entity.study.Study;
import com.example.algoyweb.model.entity.user.User;
import com.example.algoyweb.repository.study.ParticipantRepository;
import com.example.algoyweb.repository.study.StudyRepository;
import com.example.algoyweb.repository.user.UserRepository;
import com.example.algoyweb.util.ConvertUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class StudyService {

    // 스터디 및 사용자 관련 Repository 의존성 주입
    private final StudyRepository studyRepository;
    private final UserRepository userRepository;
    private final ParticipantRepository participantRepository;

    /**
     * 스터디 목록을 페이지네이션하여 가져오는 메서드
     *
     * @param page    페이지 번호
     * @param keyword
     * @return 스터디 목록 DTO 리스트
     */
    @Transactional(readOnly = true)
    public Page<StudyDto> getStudyList(int page, String keyword) {
        // 페이지 요청 생성 (10개씩 내림차순 정렬)
        Pageable pageable = PageRequest.of(page, 12, Sort.by("id").descending());
        // 페이지 요청
        Page<Study> studyPage = studyRepository.findAllByKeyword(pageable, keyword);

        // 스터디 목록을 DTO로 변환하여 반환
        return studyPage.map(ConvertUtils::convertStudyToDto);
    }

    /**
     * 스터디를 저장하는 메서드
     * @param studyDto 저장할 스터디 정보 DTO
     * @param username 사용자 이름
     * @return 저장된 스터디 DTO
     */
    public StudyDto save(StudyDto studyDto, String username) {
        // DTO를 Study 엔티티로 변환
        Study savedStudy = ConvertUtils.convertDtoToStudy(studyDto);
        // 사용자 정보 조회
        User findUser = userRepository.findOptionalByEmail(username).orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_EQUAL_EMAIL));
        // 스터디에 사용자 연결
        savedStudy.connectUser(findUser);

        // 스터디 저장
        studyRepository.save(savedStudy);

        // 저장된 스터디를 DTO로 변환하여 반환
        return ConvertUtils.convertStudyToDto(savedStudy);
    }

    /**
     * 특정 스터디 정보를 가져오는 메서드
     * @param id 스터디 ID
     * @return 스터디 정보 DTO
     */
    @Transactional(readOnly = true)
    public StudyDto getStudy(Long id) {
        // 스터디 ID로 스터디 조회
        Study findStudy = studyRepository.findById(id).orElseThrow(() -> new CustomException(StudyErrorCode.STUDY_NOT_FOUND));
        // 조회된 스터디를 DTO로 변환하여 반환
        return ConvertUtils.convertStudyToDto(findStudy);
    }

    /**
     * 특정 스터디를 삭제하는 메서드
     * @param id 스터디 ID
     */
    public void deleteStudy(Long id) {
        // 스터디 ID로 스터디 삭제
        studyRepository.deleteById(id);
    }

    /**
     * 특정 스터디 정보를 수정하는 메서드
     * @param id 스터디 ID
     * @param studyDto 수정할 스터디 정보 DTO
     * @param username 사용자 이름
     * @return 수정된 스터디 정보 DTO
     */
    public StudyDto updateStudy(Long id, StudyDto studyDto, String username) {
        // 스터디 ID로 스터디 조회
        Study findStudy = studyRepository.findById(id).orElseThrow(() -> new CustomException(StudyErrorCode.STUDY_NOT_FOUND));
        // 사용자 정보 조회
        User findUser = userRepository.findOptionalByEmail(username).orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_EQUAL_EMAIL));

        // 스터디 작성자와 요청 사용자가 동일하지 않으면 예외 발생
        if (!findStudy.getUser().getUserId().equals(findUser.getUserId())) {
            throw new CustomException(UserErrorCode.USER_NOT_EQUAL_ID);
        }

        // 스터디 정보 업데이트
        findStudy.update(studyDto);

        // 수정된 스터디를 DTO로 변환하여 반환
        return ConvertUtils.convertStudyToDto(findStudy);
    }


    public Integer countParticipants(Long studyId) {

        return participantRepository.countByStudyId(studyId);
    }

    public Page<StudyDto> searchStatus(Study.Status status, int page) {
        // 페이지 요청 생성 (10개씩 내림차순 정렬)
        Pageable pageable = PageRequest.of(page, 12, Sort.by("id").descending());
        // 페이지 요청
        Page<Study> studyPage = studyRepository.findAllByStatus(pageable, status);

        // 스터디 목록을 DTO로 변환하여 반환
        return studyPage.map(ConvertUtils::convertStudyToDto);
    }
}

