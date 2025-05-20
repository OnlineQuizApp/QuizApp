package com.example.project_module6.repository;

import com.example.project_module6.dto.QuestionDetailDataDto;
import com.example.project_module6.model.Questions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IQuestionsRepository extends JpaRepository<Questions,Integer> {
    Questions findById(int id);
    void deleteById(int id);
    @Query(value = "select q.*,c.name from questions q join categorys c on q.category_id=c.id where q.soft_delete=false and q.content like ?1"
    ,countQuery = "select q.*,c.name from questions q join categorys c on q.category_id=c.id where q.soft_delete=false and q.content like ?1"
    ,nativeQuery = true)
    Page<Questions> searchQuestionByQuestionContent(String content, Pageable pageable);

    @Query(value = "select q.*,c.name from questions q join categorys c on q.category_id=c.id where q.soft_delete=false and c.name = ?1"
            ,countQuery = "select q.*,c.name from questions q join categorys c on q.category_id=c.id where q.soft_delete=false and c.name = ?1"
            ,nativeQuery = true)
    Page<Questions> searchQuestionByCategory(String category, Pageable pageable);

    @Query(value = "SELECT q.*,c.name FROM questions q join categorys c on q.category_id=c.id where q.soft_delete=false ORDER BY q.id DESC",
            countQuery = "SELECT q.*,c.name FROM questions q join categorys c on q.category_id=c.id where q.soft_delete=false ORDER BY q.id DESC",
            nativeQuery = true)
    Page<Questions> getAllQuestions(Pageable pageable);

    @Query(value = """

            SELECT q.id,
            q.content AS questionsContent,
            q.img,
             q.category_id as categoryId,
            c.name as categoryName,
            group_concat(a.id) as answersIds,
            group_concat(a.content) AS answersContents,
            group_concat(a.correct ) as corrects
            FROM questions q\s
            join answers a on q.id=a.question_id join
            categorys c on c.id = q.category_id
            WHERE q.soft_delete = false and q.id=?1 group by q.id
                 """,nativeQuery = true)
    QuestionDetailDataDto findQuestionsById(Integer id);


    /// lấy random câu hỏi
    @Query(value = """
            SELECT q.*
                   FROM questions q
                   JOIN categorys c ON q.category_id = c.id
                   WHERE c.name like ?1
                   ORDER BY RAND()
                   LIMIT ?2
                   """,nativeQuery = true)
    List<Questions> findRandomQuestions(String category,int numberQuestions);
    ///  đếm số lượng câu hỏi
    @Query(value = """
           select count(*) from questions q 
           join categorys c on q.category_id = c.id
           where c.name like ?1 and q.soft_delete=false      
                 """,nativeQuery = true)
    Integer countQuestions(String category);

    boolean existsByContent(String content);

}
