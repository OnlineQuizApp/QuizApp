package com.example.project_module6.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAnswerDTO {
    private int questionId;
    private List<Integer> selectedAnswerIds;
}
