package com.example.project_module6.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "exam_set_exam")
public class ExamSetExam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne
    @JoinColumn(name = "exam_set_id", nullable = false)
    private ExamSets examSet;
    @ManyToOne
    @JoinColumn(name = "exam_id", nullable = false)
    private Exams exam;

}
