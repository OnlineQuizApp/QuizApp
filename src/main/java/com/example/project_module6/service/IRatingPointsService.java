package com.example.project_module6.service;

import com.example.project_module6.model.RatingPoints;
import com.example.project_module6.model.Users;

import java.util.List;

public interface IRatingPointsService {
   Users getAccumulatedPoints(int idUser);
   List<RatingPoints> getAllRatingPoints();
}
