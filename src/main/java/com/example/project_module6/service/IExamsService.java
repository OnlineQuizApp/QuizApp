package com.example.project_module6.service;

import com.example.project_module6.dto.ExamsDto;
import com.example.project_module6.model.Exams;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IExamsService {
    Page<Exams> getAlExams(Pageable pageable);
    Page<Exams> searchExamsByCategory(String category,Pageable pageable);
    void addExamsRandom(ExamsDto examsDto);
    Exams updateExamsRandom(int id,ExamsDto examsDto);

    boolean deleteExams(int id);
    Exams addExams(ExamsDto examsDto);
    void confirmExams(Integer examID, List<Integer> questionsId);
    boolean updateExams(int id,ExamsDto examsDto,List<Integer> questionsId);

}
