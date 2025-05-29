package com.example.project_module6.repository;

import com.example.project_module6.model.RatingPoints;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IRatingPointRepository extends JpaRepository<RatingPoints,Integer> {
    RatingPoints findByUserId(int id);
    RatingPoints findById(int id);
}
