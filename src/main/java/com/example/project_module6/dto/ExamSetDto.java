package com.example.project_module6.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ExamSetDto {
    private int id;
    private String name;
    private String img;
    private Date creationDate;
    private boolean softDelete=false;

    public ExamSetDto(String name, String img, Date creationDate) {
        this.name = name;
        this.img = img;
        this.creationDate = creationDate;
    }
}
