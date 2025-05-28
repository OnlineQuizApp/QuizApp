package com.example.project_module6.model;

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
@Table(name = "rating_points")
public class RatingPoints {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private double accumulatedPoints;
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", unique = true)
    private Users user;

}
