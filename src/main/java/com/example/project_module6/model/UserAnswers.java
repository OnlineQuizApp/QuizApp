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
@Table(name = "user_answers")
public class UserAnswers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "result_id", nullable = false)
    private Results result;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Questions question;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "answer_id", nullable = false)
    private Answers answer;
}
