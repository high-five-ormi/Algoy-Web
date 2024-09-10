package com.example.algoyweb.service.study;

import com.example.algoyweb.exception.CustomException;
import com.example.algoyweb.exception.errorcode.CommentErrorCode;
import com.example.algoyweb.exception.errorcode.StudyErrorCode;
import com.example.algoyweb.exception.errorcode.UserErrorCode;
import com.example.algoyweb.model.dto.comment.CommentDto;
import com.example.algoyweb.model.entity.study.Comment;
import com.example.algoyweb.model.entity.study.Participant;
import com.example.algoyweb.model.entity.study.Study;
import com.example.algoyweb.model.entity.user.User;
import com.example.algoyweb.repository.study.CommentRepository;
import com.example.algoyweb.repository.study.ParticipantRepository;
import com.example.algoyweb.repository.study.StudyRepository;
import com.example.algoyweb.repository.user.UserRepository;
import com.example.algoyweb.util.ConvertUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    // 각종 Repository 객체들 (댓글, 참가자, 스터디, 사용자) 의존성 주입
    private final CommentRepository commentRepository;
    private final ParticipantRepository participantRepository;
    private final StudyRepository studyRepository;
    private final UserRepository userRepository;

    /**
     * 특정 스터디에 대한 댓글을 가져오는 메서드
     * @param studyId 스터디 ID
     * @param username 사용자 이름 (null일 수 있음)
     * @return 댓글 목록
     */
    @Transactional(readOnly = true)
    public List<CommentDto> getComments(Long studyId, String username) {

        // 반환할 결과값을 담을 리스트
        List<CommentDto> result = new ArrayList<>();
        // 부모-자식 관계를 정렬하기 위한 Map
        Map<Long, CommentDto> map = new HashMap<>();
        // 스터디 ID에 따른 댓글 목록을 가져옴
        List<Comment> comments = commentRepository.findAllByStudyId(studyId);

        // 가져온 댓글 리스트 순회
        comments.forEach(c -> {
            CommentDto commentDto;

            // 댓글이 삭제된 경우 "삭제된 댓글입니다." 라는 내용으로 변환
            if (c.getIsDeleted()) {
                commentDto = CommentDto.builder()
                        .content("삭제된 댓글입니다.")
                        .id(c.getId())
                        .author(c.getUser().getNickname())
                        .updatedAt(c.getUpdatedAt())
                        .depth(c.getDepth())
                        .children(new ArrayList<>()) // 자식 댓글을 위한 리스트 초기화
                        .isDeleted(c.getIsDeleted())
                        .secret(c.getSecret())
                        .createdAt(c.getCreatedAt())
                        .updatedAt(c.getUpdatedAt())
                        .build();
            } else {
                // 삭제되지 않은 댓글의 경우 그대로 데이터를 담음
                commentDto = CommentDto.builder()
                        .content(c.getContent())
                        .id(c.getId())
                        .author(c.getUser().getNickname())
                        .updatedAt(c.getUpdatedAt())
                        .depth(c.getDepth())
                        .children(new ArrayList<>())
                        .isDeleted(c.getIsDeleted())
                        .secret(c.getSecret())
                        .createdAt(c.getCreatedAt())
                        .updatedAt(c.getUpdatedAt())
                        .build();
            }

            // username이 존재하면 사용자 이메일을 저장
            if (username != null) {
                commentDto.setUserEmail(username);
            }

            // 부모 댓글이 있으면 부모 ID 설정
            if (c.getParent() != null) {
                commentDto.setParent(c.getParent().getId());
            }

            // Map에 댓글 ID와 댓글 DTO를 저장
            map.put(commentDto.getId(), commentDto);

            // 부모 댓글이 있는 경우 부모의 자식 리스트에 추가, 그렇지 않으면 결과 리스트에 추가
            if (c.getParent() != null) {
                map.get(c.getParent().getId()).getChildren().add(commentDto);
            } else {
                result.add(commentDto);
            }
        });

        // 정렬된 댓글 리스트 반환
        return result;
    }

    /**
     * 대댓글이 아닌 댓글을 생성하는 메서드
     * @param commentDto 댓글 DTO
     * @param username 사용자 이름
     * @param studyId 스터디 ID
     * @return 생성된 댓글 DTO
     */
    public CommentDto createNonReply(CommentDto commentDto, String username, Long studyId) {

        // 새로운 댓글 객체 생성
        Comment savedComment = Comment.builder()
                .content(commentDto.getContent())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // 사용자와 스터디를 각각 조회
        User findUser = userRepository.findOptionalByEmail(username).orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_EQUAL_EMAIL));
        Study findStudy = studyRepository.findById(studyId).orElseThrow(() -> new CustomException(StudyErrorCode.STUDY_NOT_FOUND));

        // 댓글에 사용자와 스터디 연결
        savedComment.connectUser(findUser);
        savedComment.connectStudy(findStudy);

        // 저장된 댓글을 DTO로 변환하여 반환
        return ConvertUtils.convertCommentToDto(commentRepository.save(savedComment));
    }

    /**
     * 대댓글을 생성하는 메서드
     * @param commentDto 댓글 DTO
     * @param username 사용자 이름
     * @param studyId 스터디 ID
     * @param parentId 부모 댓글 ID
     * @return 생성된 대댓글 DTO
     */
    public CommentDto createReply(CommentDto commentDto, String username, Long studyId, Long parentId) {

        // 새로운 댓글 객체 생성
        Comment savedComment = Comment.builder()
                .content(commentDto.getContent())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // 부모 댓글을 조회하여 연결
        savedComment.connectParent(commentRepository.findById(parentId).orElseThrow(() -> new CustomException(CommentErrorCode.COMMENT_PARENT_NOT_FOUND)));

        // 사용자와 스터디를 조회 후 연결
        User findUser = userRepository.findOptionalByEmail(username).orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_EQUAL_EMAIL));
        Study findStudy = studyRepository.findById(studyId).orElseThrow(() -> new CustomException(StudyErrorCode.STUDY_NOT_FOUND));

        savedComment.connectUser(findUser);
        savedComment.connectStudy(findStudy);

        // 저장된 댓글을 DTO로 변환하여 반환
        return ConvertUtils.convertCommentToDto(commentRepository.save(savedComment));
    }

    /**
     * 댓글을 수정하는 메서드
     * @param commentDto 수정할 댓글 DTO
     * @param username 사용자 이름
     * @param commentId 댓글 ID
     * @return 수정된 댓글 DTO
     */
    public CommentDto updateComment(CommentDto commentDto, String username, Long commentId) {

        // 수정할 댓글 조회
        Comment updatedComment = commentRepository.findById(commentId).orElseThrow(() -> new CustomException(CommentErrorCode.COMMENT_NOT_FOUND));

        // 댓글 수정 후 DTO로 변환하여 반환
        updatedComment.update(commentDto);
        return ConvertUtils.convertCommentToDto(updatedComment);
    }

    /**
     * 댓글을 삭제하는 메서드
     * @param commentId 댓글 ID
     * @param username 사용자 이름
     */
    public void delete(Long commentId, String username) {

        // 삭제할 댓글 조회
        Comment deletedComment = commentRepository.findById(commentId).orElseThrow(() -> new CustomException(CommentErrorCode.COMMENT_NOT_FOUND));

        // 댓글 작성자와 현재 사용자가 동일하지 않으면 예외 발생
        if (!deletedComment.getUser().getEmail().equals(username)) {
            throw new CustomException(CommentErrorCode.COMMENT_FORBIDDEN);
        }

        // 댓글 삭제 처리
        deletedComment.delete();
    }

    /**
     * 스터디에 참여하는 메서드
     * @param commentId 댓글 ID
     * @param studyId 스터디 ID
     * @param username 사용자 이름
     */
    public void join(Long commentId, Long studyId, String username) {

        // 스터디와 댓글을 조회
        Study findStudy = studyRepository.findById(studyId).orElseThrow(() -> new CustomException(StudyErrorCode.STUDY_NOT_FOUND));
        Comment findComment = commentRepository.findById(commentId).orElseThrow(() -> new CustomException(CommentErrorCode.COMMENT_NOT_FOUND));

        // 사용자 조회
        User findUser = userRepository.findOptionalByEmail(username).orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

        // 스터디 주인이 아니면 예외 발생
        if (!findUser.getUserId().equals(findStudy.getUser().getUserId())) {
            throw new CustomException(UserErrorCode.USER_NOT_EQUAL_ID);
        }

        // 참가자 생성 및 연결
        Participant participant = new Participant();
        participant.connectComment(findComment);
        participant.connectStudy(findStudy);
    }

    /**
     * 스터디 참가자를 삭제하는 메서드
     * @param commentId 댓글 ID
     * @param studyId 스터디 ID
     * @param username 사용자 이름
     */
    public void deleteParticipant(Long commentId, Long studyId, String username) {

        // 참가자 조회
        Participant findParticipant = participantRepository.findByCommentAndStudy(commentId, studyId)
                .orElseThrow(() -> new CustomException(StudyErrorCode.PARTICIPANT_NOT_FOUND));

        // 사용자 조회
        User findUser = userRepository.findOptionalByEmail(username).orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

        // 참가자와 사용자 ID가 다르면 예외 발생
        if (!findUser.getUserId().equals(findParticipant.getComment().getUser().getUserId())) {
            throw new CustomException(UserErrorCode.USER_NOT_EQUAL_ID);
        }

        // 참가자 삭제
        participantRepository.delete(findParticipant);
    }
}

