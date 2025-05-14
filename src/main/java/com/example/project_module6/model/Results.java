package com.example.project_module6.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Results {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private double totalScore;
    private LocalDate submittedAt;


    @ManyToOne
    @JoinColumn(name = "exam_id", nullable = false)
    private Exams exam;


    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;



    @ManyToOne
    @JoinColumn(name = "rating_point_id", nullable = false)
    private RatingPoints ratingPoint;
}
