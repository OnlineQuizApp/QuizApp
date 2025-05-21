package com.example.project_module6.repository;

import com.example.project_module6.model.Results;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.xml.transform.Result;
import java.util.Optional;
@Repository
public interface IResultRepository extends JpaRepository<Results,Integer> {
    Optional<Results> findByUserIdAndExamId(Integer userId, Integer examId);
}