package com.example.project_module6.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Answers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String content;
    private boolean correct = false;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Questions question;

    public Answers(int id, String content,boolean correct) {
        this.id = id;
        this.content = content;
        this.correct =correct;
    }
}
