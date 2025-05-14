package com.example.project_module6.dto;

import com.example.project_module6.model.Answers;
import com.example.project_module6.model.Categorys;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class QuestionDtoResponse {
    private int id;
    private String content;
    private String img;
    private List<AnswersDto> answers;
    private Categorys category;
    private boolean softDelete=false;
}
