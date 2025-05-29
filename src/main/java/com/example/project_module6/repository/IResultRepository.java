package com.example.project_module6.repository;

import com.example.project_module6.model.Results;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.xml.transform.Result;
import java.util.List;
import java.util.Optional;
@Repository
public interface IResultRepository extends JpaRepository<Results,Integer> {
    Optional<Results> findByUserIdAndExamId(Integer userId, Integer examId);
    Results findById(int id);
    @Query(value = "SELECT r.id, r.exam_id, e.title AS exam_title, e.number_of_questions, r.total_score, r.submitted_at " +
            "FROM results r " +
            "JOIN exams e ON r.exam_id = e.id " +
            "JOIN users u ON r.user_id = u.id " +
            "WHERE u.username = :username AND e.soft_delete = FALSE", nativeQuery = true)
    List<Object[]> findResultsByUsername(@Param("username") String username);


    @Query(value = "SELECT r.id, r.exam_id, e.title AS exam_title, e.number_of_questions, r.total_score, r.submitted_at " +
            "FROM results r " +
            "JOIN exams e ON r.exam_id = e.id " +
            "WHERE r.id = :resultId AND e.soft_delete = FALSE", nativeQuery = true)
    List<Object[]> findResultById(@Param("resultId") Integer resultId);
}