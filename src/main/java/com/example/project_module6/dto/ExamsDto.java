package com.example.project_module6.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter
        ;

import java.time.LocalTime;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ExamsDto {
    private int id;
    private String title;
    private String category;
    private int numberOfQuestions;
    private LocalTime testTime;
    private boolean softDelete=false;
}
