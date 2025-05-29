package com.example.project_module6.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamResultDTO {
    private int correctAnswers;
    private int totalQuestions;
    private double totalScore;
}
