package com.example.project_module6.dto;

import com.example.project_module6.model.Users;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RatingPointsDto {
    private int id;
    private double accumulatedPoints;
    private Users user;
}
