package com.example.project_module6.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Exams {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String title;
    private String category;
    @Column(name = "number_of_questions")
    private int numberOfQuestions;
    @Column(name = "test_time")
    private LocalTime testTime;
    @Column(name = "soft_delete")
    private boolean softDelete=false;

}
