package com.example.project_module6.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultDTO {
    private Integer id;
    private Integer examId;
    private String examTitle;
    private Integer numberOfQuestions;
    private Double totalScore;
    private String submittedAt;
}
