package com.example.algoyweb.model.dto.allen;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@AllArgsConstructor
public class SolvedACResponse {
    private Long id;  // 엔티티의 ID
    private String algoyUsername;
    private List<String> response;
    private LocalDateTime updatedAt;
}
