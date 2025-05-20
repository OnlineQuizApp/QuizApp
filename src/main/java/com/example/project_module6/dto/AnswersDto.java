package com.example.project_module6.dto;

import com.example.project_module6.model.Questions;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;


@NoArgsConstructor
@Getter
@Setter
public class AnswersDto {

    private int id;

    @NotBlank(message = "Nội dung câu trả lời không được để trống")
    @Size(max = 500, message = "Nội dung câu trả lời không được vượt quá 500 ký tự")
    private String content;

    private boolean correct;

    public AnswersDto(int id, String content, boolean correct) {
        this.id = id;
        this.content = content;
        this.correct = correct;
    }

    public AnswersDto(String content, boolean correct) {
        this.content = content;
        this.correct = correct;
    }
}
