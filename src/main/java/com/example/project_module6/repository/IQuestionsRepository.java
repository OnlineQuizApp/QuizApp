package com.example.project_module6.repository;

import com.example.project_module6.dto.QuestionDetailDataDto;
import com.example.project_module6.model.Questions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface IQuestionsRepository extends JpaRepository<Questions,Integer> {
    Questions findById(int id);
    void deleteById(int id);
    @Query(value = "select q.*,c.name from questions q join categorys c on q.category_id=c.id where q.soft_delete=false and c.name like ?1"
    ,countQuery = "select q.*,c.name from questions q join categorys c on q.category_id=c.id where q.soft_delete=false and c.name like ?1"
    ,nativeQuery = true)
    Page<Questions> searchQuestionByCategory(String category, Pageable pageable);

    @Query(value = "SELECT q.*,c.name FROM questions q join categorys c on q.category_id=c.id where q.soft_delete=false",
            countQuery = "SELECT q.*,c.name FROM questions q join categorys c on q.category_id=c.id where q.soft_delete=false",
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
//    @Query(value = "delete from questions q where q.id  = ?1",nativeQuery = true)
//    void deleteQuestions(int id);
}
