package com.example.project_module6.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExamResponseDto {
    private int id;
    private Integer index;
    private String title;
    private String category;
    private String status;      // "Đã thi" hoặc "Chưa thi"
    private String actionLabel; // "Xem kết quả" hoặc "Làm bài thi"
}
