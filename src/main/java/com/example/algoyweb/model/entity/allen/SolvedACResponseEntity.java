package com.example.algoyweb.model.entity.allen;

import com.example.algoyweb.model.dto.allen.SolvedACResponseDto;
import com.example.algoyweb.model.entity.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;


@Entity
@Table(name = "solved_ac_response")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolvedACResponseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "response_id", nullable = false)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private User user;

    @Column(name = "user_email", nullable = false)
    private String userEmail;

    @ElementCollection
    @CollectionTable(name = "response_list", joinColumns = @JoinColumn(name = "response_id"))
    @Column(name = "response", nullable = false)
    private List<String> response;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public void updateResponse(List<String> response) {
        this.response = response;
        this.updatedAt = LocalDateTime.now();
    }

    public SolvedACResponseDto toDto() {
        return SolvedACResponseDto.builder()
                .id(this.id)
                .userId(this.user.getUsername())
                .userEmail(this.user.getEmail()) // 유저 entity의 getUsername() 메서드 호출
                .response(this.response)
                .updatedAt(this.updatedAt)
                .build();
    }

}
