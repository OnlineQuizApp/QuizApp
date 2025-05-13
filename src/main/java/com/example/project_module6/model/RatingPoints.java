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
    private int accumulatedPoints;

    @OneToOne
    @JoinColumn(name = "id_user", referencedColumnName = "id", unique = true)
    private Users user;

    @JsonManagedReference
    @OneToMany(mappedBy = "ratingPoint", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Results> results;
}
