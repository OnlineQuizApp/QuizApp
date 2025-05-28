package com.example.project_module6.service;

import com.example.project_module6.model.RatingPoints;
import com.example.project_module6.model.Users;
import com.example.project_module6.repository.IRatingPointsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RatingPointsService implements IRatingPointsService{
    @Autowired
    private IRatingPointsRepository ratingPointsRepository;
    @Override
    public Users getAccumulatedPoints(int idUser) {
            return  ratingPointsRepository.getAccumulatedPoints(idUser);
    }

    @Override
    public List<RatingPoints> getAllRatingPoints() {
        return ratingPointsRepository.getAllRatingPoints();
    }
}
