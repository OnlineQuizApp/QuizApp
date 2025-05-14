package com.example.project_module6.dto;

import com.example.project_module6.model.Questions;
import jakarta.persistence.*;
import lombok.*;


@NoArgsConstructor
@Getter
@Setter
public class AnswersDto {
    private int id;
    private String content;
    private boolean correct;

    public AnswersDto(int id, String content, boolean correct) {
        this.id = id;
        this.content = content;
        this.correct = correct;
    }
}
