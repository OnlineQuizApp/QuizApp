package com.example.project_module6.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "exam_questions")
public class ExamQuestions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private double score;


    @ManyToOne
    @JoinColumn(name = "exam_id", nullable = false)
    private Exams exam;


    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Questions question;

}
