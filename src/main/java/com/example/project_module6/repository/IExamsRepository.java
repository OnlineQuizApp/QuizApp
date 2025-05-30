package com.example.project_module6.repository;

import com.example.project_module6.dto.ExamsQuestionDataDto;
import com.example.project_module6.model.Exams;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IExamsRepository extends JpaRepository<Exams, Integer> {

    ///  tìm kiếm  đề thi
    @Query(value = """
            select 
            distinct 
            e.id,
            e.title,
            e.category,
            e.number_of_questions,
            e.test_time,
            e.soft_delete,
            e.exits_exam_set_exam            from exams e join exam_questions eq on e.id=eq.exam_id
            where e.soft_delete=false and e.id in(select eq.exam_id from exam_questions eq)
            and e.category like ?1
            """,
            countQuery = """
           SELECT * FROM exams e WHERE e.soft_delete = false and e.category like ?1
                    """,
            nativeQuery = true)
    Page<Exams> searchExamsByCategory(String category, Pageable pageable);

    @Query(value = """
             select 
            distinct 
            e.id,
            e.title,
            e.category,
            e.number_of_questions,
            e.test_time,
            e.soft_delete,
               e.exits_exam_set_exam         from exams e join exam_questions eq on e.id=eq.exam_id
            where e.soft_delete=false and e.id in(select eq.exam_id from exam_questions eq)
            and e.title like ?1
            """,
            countQuery = """
           SELECT * FROM exams e WHERE e.soft_delete = false and e.title like ?1
                    """,
            nativeQuery = true)
    Page<Exams> searchExamsByTitle(String title, Pageable pageable);

    @Query(value = """
            select 
            distinct 
            e.id,
            e.title,
            e.category,
            e.number_of_questions,
            e.test_time,
            e.soft_delete,
            e.exits_exam_set_exam from exams e join exam_questions eq on e.id=eq.exam_id
            where e.soft_delete=false and e.id in(select eq.exam_id from exam_questions eq)
            ORDER BY e.id DESC
                   """,nativeQuery = true)
    List<Exams> getAllExams();

    @Query(value = """
            select 
            distinct 
            e.id,
            e.title,
            e.category,
            e.number_of_questions,
            e.test_time,
            e.soft_delete,
            e.exits_exam_set_exam from exams e join exam_questions eq on e.id=eq.exam_id
            where e.soft_delete=false and e.id in(select eq.exam_id from exam_questions eq)
            ORDER BY e.id DESC
            """,
            countQuery = """
            select * from exams e where e.soft_delete=false 
                    """,
            nativeQuery = true)
    Page<Exams> getAllExams(Pageable pageable);

    @Query(value = "select * from exams e where e.id = ?1", nativeQuery = true)
    Exams findById(int id);

    int countBySoftDeleteFalse();


    @Query(value = """ 
            
            SELECT e.id, e.title, e.category, e.number_of_questions, e.test_time,\s
                          q.id AS question_id, q.content, q.img, q.video, q.category_id
                   FROM exams e
                   LEFT JOIN exam_questions eq ON e.id = eq.exam_id
                   LEFT JOIN questions q ON eq.question_id = q.id
                   WHERE e.id = ? AND e.soft_delete = FALSE;
            """, nativeQuery = true)
    List<ExamsQuestionDataDto> findByIdUpdate(int id);

    @Modifying
    @Transactional
    @Query(value = """
            DELETE FROM exam_questions
            WHERE exam_id = ?1 AND question_id = ?2
            """, nativeQuery = true)
    void deleteExamsByQuestionsId(int idExams, int idQuestions);


    boolean existsByTitleAndCategory(String title, String category);

    @Query(value = "SELECT e.* FROM exams e " +
            "INNER JOIN exam_set_exam ese ON e.id = ese.exam_id " +
            "WHERE ese.exam_set_id = :examSetId AND e.soft_delete = FALSE", nativeQuery = true)
    List<Exams> findExamsByExamSetId(@Param("examSetId") Integer examSetId);
    @Query(value = "SELECT e.id, e.title, COUNT(r.id) as total_participants, " +
            "SUM(CASE WHEN r.total_score > 8 THEN 1 ELSE 0 END) as above_eight, " +
            "(SUM(CASE WHEN r.total_score > 8 THEN 1 ELSE 0 END) * 100.0 / NULLIF(COUNT(r.id), 0)) as percentage_above_eight " +
            "FROM exams e LEFT JOIN results r ON e.id = r.exam_id " +
            "WHERE e.soft_delete = FALSE " +
            "GROUP BY e.id, e.title " +
            "ORDER BY percentage_above_eight DESC", nativeQuery = true)
    List<Object[]> getExamStatistics();
}
