package com.example.project_module6.repository;

import com.example.project_module6.dto.ExamSetDetailDataDto;
import com.example.project_module6.model.ExamSets;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
    public interface IExamSetRepository extends JpaRepository<ExamSets, Integer> {

    @Modifying
    @Transactional
    @Query(value = """
               delete from exam_sets es where es.id=?1
                   """,nativeQuery = true)
    void deleteExamByID(int id);
    @Query(value = """
            select *from exam_sets es where es.soft_delete=false and es.name like ?1
              """, countQuery = """
            select *from exam_sets es where es.soft_delete=false and es.name like ?1
            """
            , nativeQuery = true)
    Page<ExamSets> findAllExamSetByName(String name, Pageable pageable);

    @Query(value = """
            select 
            es.id,
            es.name,
            es.img,
            es.creation_date,
            es.soft_delete
            from exam_sets es
            join exam_set_exam ese on es.id=ese.exam_set_id
            where es.soft_delete=false
            and es.id in(select ese.exam_set_id from exam_set_exam ese)
            ORDER BY es.id DESC
              """, countQuery = """
            select * from exam_sets es where es.soft_delete=false 
            """
            , nativeQuery = true)
    Page<ExamSets> findAllExamSet(Pageable pageable);

    @Query(value = """
            select *from exam_sets es where es.soft_delete=false
              """
            , nativeQuery = true)
    List<ExamSets> getAllExamSet();
    @Query(value = """
                    select *from  exam_sets es where  es.id=?1
                    """,nativeQuery = true)
    ExamSets findById(int id);

    @Query(value = """
                select es.id AS exam_set_id,
                es.name AS exam_set_name,
                es.img as exam_set_img,
                es.creation_date,
                GROUP_CONCAT(
                CONCAT(e.id,'-',e.title,'-', e.category,'-', e.number_of_questions,'-', e.test_time)) AS exams
                from exam_set_exam exe join exams e on  e.id=exe.exam_id
                join exam_sets es on exe.exam_set_id=es.id WHERE es.id = ?1 group by es.id;
                  """,nativeQuery = true)
    List<ExamSetDetailDataDto> detailExamSet(int id);






}
