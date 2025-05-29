package com.example.project_module6.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ExamsDto {
    private int id;
    private String title;
    private String category;
    private int numberOfQuestions;
    private String testTime;
    private boolean softDelete=false;

    public ExamsDto(int id,String title, String category, int numberOfQuestions, String testTime) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.numberOfQuestions = numberOfQuestions;
        this.testTime = testTime;
    }

}
