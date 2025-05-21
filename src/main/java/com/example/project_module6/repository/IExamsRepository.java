package com.example.project_module6.repository;

import com.example.project_module6.model.Exams;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IExamsRepository extends JpaRepository<Exams,Integer> {

    ///  tìm kiếm  đề thi
    @Query(value = """
                SELECT * FROM exams e WHERE e.soft_delete = false and e.category like ?1
                   """,
      countQuery = """
                SELECT * FROM exams e WHERE e.soft_delete = false and e.category like ?1
                 """,
    nativeQuery = true)
    Page<Exams> searchExamsByCategory(String category,Pageable pageable);

    @Query(value = """
                   select * from exams e where e.soft_delete=false
                   """,
      countQuery = """
                   select * from exams e where e.soft_delete=false
                   """,
      nativeQuery = true)
    Page<Exams> getAllExams(Pageable pageable);

    @Query(value = "select * from exams e where e.id = ?1",nativeQuery = true)
    Exams findById(int id);
    int countBySoftDeleteFalse();



}
