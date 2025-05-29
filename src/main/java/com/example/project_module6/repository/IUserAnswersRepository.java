package com.example.project_module6.repository;

import com.example.project_module6.model.UserAnswers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IUserAnswersRepository extends JpaRepository<UserAnswers,Integer> {
    UserAnswers findById(int id);
    @Query(value = "SELECT q.id AS question_id, q.content, ua.answer_id AS user_answer_id, a1.content AS user_answer, " +
            "a2.content AS correct_answer " +
            "FROM user_answers ua " +
            "JOIN questions q ON ua.question_id = q.id " +
            "LEFT JOIN answers a1 ON ua.answer_id = a1.id " +
            "JOIN answers a2 ON a2.question_id = q.id AND a2.correct = TRUE " +
            "WHERE ua.result_id = :resultId AND q.soft_delete = FALSE", nativeQuery = true)
    List<Object[]> findUserAnswersByResultId(@Param("resultId") Integer resultId);
}
