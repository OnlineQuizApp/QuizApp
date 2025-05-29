package com.example.project_module6.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultDetailDTO {
    private Integer resultId;
    private String examTitle;
    private Integer numberOfQuestions;
    private Integer correctAnswers;
    private Double totalScore;
    private List<QuestionDetailDTO> questions;
}
