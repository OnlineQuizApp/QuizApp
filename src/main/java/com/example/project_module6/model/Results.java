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

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "exam_id", nullable = false)
    private Exams exam;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @JsonManagedReference
    @OneToMany(mappedBy = "result",fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserAnswers> userAnswers;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "rating_point_id", nullable = false)
    private RatingPoints ratingPoint;
}
