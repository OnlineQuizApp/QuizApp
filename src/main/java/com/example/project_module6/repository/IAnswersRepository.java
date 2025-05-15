package com.example.project_module6.repository;

import com.example.project_module6.model.Answers;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IAnswersRepository extends JpaRepository<Answers,Integer> {
    @Modifying // báo cho Spring rằng đây là câu lệnh thay đổi dữ liệu (không phải SELECT).
    @Transactional // đảm bảo việc xoá thực hiện trong một transaction
    @Query(value = "delete from answers a where a.question_id  = ?1",nativeQuery = true)
    void deleteAnswersByQuestionId(int id);

    List<Answers> findByQuestionId(Integer questionId);
}
