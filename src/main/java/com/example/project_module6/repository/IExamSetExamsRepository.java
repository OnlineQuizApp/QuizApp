package com.example.project_module6.repository;

import com.example.project_module6.model.ExamSetExam;
import com.example.project_module6.model.ExamSets;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IExamSetExamsRepository extends JpaRepository<ExamSetExam,Integer> {
    @Modifying
    @Transactional
    @Query(value = "delete from exam_set_exam ese  where exam_set_id=?1",
            nativeQuery = true)
    void deleteByExamSetId(int examSetId);

    List<ExamSetExam> findExamSetExamByExam_Id(int examId);

    @Modifying
    @Transactional
    @Query(value = """

            DELETE FROM exam_set_exam
           WHERE exam_set_id = ?1 AND exam_id = ?2;
            """,nativeQuery = true)
    void deleteExamByExamSet(int examSetId,int ExamId);
}
