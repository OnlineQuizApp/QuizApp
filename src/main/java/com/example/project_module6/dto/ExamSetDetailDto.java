package com.example.project_module6.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ExamSetDetailDto {
    private Integer id;
    private String name;
    private Date creationDate;
    private List<ExamsDto> exams;
}
