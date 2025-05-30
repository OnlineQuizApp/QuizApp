package com.example.project_module6.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "exam_sets")
public class ExamSets {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String img;
    @Column(name = "creation_date")
    private Date creationDate;
    @Column(name = "soft_delete")
    private boolean softDelete=false;

    public ExamSets(String name, String img, Date creationDate) {
        this.name = name;
        this.img = img;
        this.creationDate = creationDate;
    }
}
