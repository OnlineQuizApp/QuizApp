package com.example.project_module6.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDTO {
    private int id;
    private String content;
    private String img;
    private String video;
    private int categoryId;
    private List<AnswersDto> answers;
}
