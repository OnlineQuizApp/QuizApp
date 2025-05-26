package com.example.project_module6.repository;

import com.example.project_module6.dto.ExamsQuestionDataDto;
import com.example.project_module6.dto.ExamsQuestionsResponseDto;
import com.example.project_module6.model.ExamQuestions;
import com.example.project_module6.model.Exams;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IExamsQuestionRepository extends JpaRepository<ExamQuestions, Integer> {
    @Modifying
    @Transactional
    @Query(value = "delete  from exam_questions eq  where exam_id=?1",
            nativeQuery = true)
    void deleteByExamId(int examId);

    ///  chi tiết đề thi bao gồm câu hỏi và đáp án
    @Query(value = """
            select e.id as examsId,
            e.title  as title,
            e.category as category,
            e.number_of_questions  as numberOfQuestions,
            e.test_time as testTime,
            q.id as questionsId,
            q.img as img,
            q.video as video,
            group_concat(q.content) as questionsContent,
            group_concat(concat(a.content,"-",a.correct)) AS answers
            from exam_questions eq
            join exams e on eq.exam_id=e.id 
            join questions q on q.id=eq.question_id
            join answers a on a.question_id=q.id  where e.id=?1
            GROUP BY q.id
            """,
            countQuery = """
                    select count(DISTINCT q.id)
                    from exam_questions eq
                    join exams e on eq.exam_id=e.id
                    join questions q on q.id=eq.question_id
                    join answers a on a.question_id=q.id  
                    where e.id=?1
                    """, nativeQuery = true)
    Page<ExamsQuestionDataDto> detailExamsQuestions(int id, Pageable pageable);

    @Query(value = """
            select e.id as examsId,
              e.title  as title,
              e.category as category,
              e.number_of_questions  as numberOfQuestions,
              e.test_time as testTime,
              q.id as questionsId,
              q.img as img,
              q.video as video,
              group_concat(q.content) as questionsContent,
              group_concat(concat(a.content,"-",a.correct)) AS answers
              from exam_questions eq
              join exams e on eq.exam_id=e.id 
              join questions q on q.id=eq.question_id
              join answers a on a.question_id=q.id  where e.id=?1
              GROUP BY q.id
            """, nativeQuery = true)
    List<ExamsQuestionDataDto> detailExamsQuestionsUpdate(int id);

    @Query(value = """
            select e.id,
            e.title,
            e.category,
            e.number_of_questions,
            e.test_time,
            q.id,
            q.img,
            group_concat(q.content) as questionsContent,
            group_concat(concat(a.content,"-",a.correct)) AS answers
            from exam_questions eq\s
            join exams e on eq.exam_id=e.id\s
            join questions q on q.id=eq.question_id\s
            join answers a on a.question_id=q.id  where e.id=?1
            GROUP BY q.id
            """, nativeQuery = true)
    List<ExamsQuestionDataDto> getExamsQuestions(int id);

    boolean existsByExam(Exams exam);

    List<ExamQuestions> findByExam_Id(int examId);
}
