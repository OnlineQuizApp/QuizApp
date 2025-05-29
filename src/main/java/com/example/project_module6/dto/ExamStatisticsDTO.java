package com.example.project_module6.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamStatisticsDTO {
    private Long examId;
    private String examTitle;
    private Long totalParticipants;
    private Double percentageAboveEight;
}
