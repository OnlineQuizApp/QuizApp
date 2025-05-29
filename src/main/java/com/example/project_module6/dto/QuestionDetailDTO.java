package com.example.project_module6.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDetailDTO {
    private Integer questionId;
    private String content;
    private String userAnswer;
    private String correctAnswer;
}
