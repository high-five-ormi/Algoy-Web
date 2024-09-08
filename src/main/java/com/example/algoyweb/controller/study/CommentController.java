package com.example.algoyweb.controller.study;

import com.example.algoyweb.model.dto.comment.CommentDto;
import com.example.algoyweb.service.study.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/algoy")
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/comment/gets")
    public ResponseEntity<List<CommentDto>> getComments(@RequestParam Long studyId, @AuthenticationPrincipal Optional<UserDetails> userDetails) {

        List<CommentDto> commentDtoList = commentService.getComments(studyId, userDetails.orElse(null).getUsername());

        return ResponseEntity.status(HttpStatus.OK).body(commentDtoList);
    }

    @PostMapping("/comment/non-reply")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_NORMAL')")
    public ResponseEntity<CommentDto> createNonReply(@RequestBody CommentDto commentDto,
                                                     @AuthenticationPrincipal UserDetails userDetails,
                                                     @RequestParam Long studyId) {
        CommentDto saveComment = commentService.createNonReply(commentDto, userDetails.getUsername(), studyId);

        return ResponseEntity.status(HttpStatus.CREATED).body(saveComment);
    }

    @PostMapping("/comment/reply")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_NORMAL')")
    public ResponseEntity<CommentDto> createNonReply(@RequestBody CommentDto commentDto,
                                                     @AuthenticationPrincipal UserDetails userDetails,
                                                     @RequestParam Long studyId,
                                                     @RequestParam Long commentId) {
        CommentDto saveComment = commentService.createReply(commentDto, userDetails.getUsername(), studyId, commentId);

        return ResponseEntity.status(HttpStatus.CREATED).body(saveComment);
    }

    @PostMapping("/comment/update")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_NORMAL')")
    public ResponseEntity<CommentDto> updateComment(@RequestBody CommentDto commentDto,
                                                    @AuthenticationPrincipal UserDetails userDetails,
                                                    @RequestParam Long commentId) {
        CommentDto updateComment = commentService.updateComment(commentDto, userDetails.getUsername(), commentId);

        return ResponseEntity.status(HttpStatus.OK).body(updateComment);
    }

    @PostMapping("/comment/delete")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_NORMAL')")
    public ResponseEntity<String> deleteComment(@RequestParam Long commentId,
                                                @AuthenticationPrincipal UserDetails userDetails) {

        commentService.delete(commentId, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.OK).body("삭제 완료");
    }

    @PostMapping("/comment/join")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_NORMAL')")
    public ResponseEntity<String> joinComment(@RequestParam Long commentId,
                                              @RequestParam Long studyId,
                                              @AuthenticationPrincipal UserDetails userDetails) {

        commentService.join(commentId, studyId, userDetails.getUsername());

        return ResponseEntity.status(HttpStatus.OK).body("스터디 가입 성공");
    }

    @PostMapping("/comment/out")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_NORMAL')")
    public ResponseEntity<String> outComment(@RequestParam Long commentId,
                                             @RequestParam Long studyId,
                                             @AuthenticationPrincipal UserDetails userDetails) {

        commentService.deleteParticipant(commentId, studyId, userDetails.getUsername());

        return ResponseEntity.status(HttpStatus.OK).body("스터디 삭제 성공");
    }
}
