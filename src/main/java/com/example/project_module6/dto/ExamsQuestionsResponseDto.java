package com.example.project_module6.dto;

import com.example.project_module6.model.Answers;
import com.example.project_module6.model.Exams;
import com.example.project_module6.model.Questions;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Time;
import java.util.List;
import java.util.PrimitiveIterator;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ExamsQuestionsResponseDto {
    private int id;
    private String title;
    private String category;
    private int numberQuestions;
    private Time testTime;
    private List<QuestionsDto> questions;

}
