package com.example.project_module6.service;

import com.example.project_module6.dto.ExamsDto;
import com.example.project_module6.model.Exams;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IExamsService {
    Page<Exams> getAlExams(Pageable pageable);
    Page<Exams> searchExamsByCategory(String category,Pageable pageable);
    void addExamsRandom(ExamsDto examsDto);
    boolean updateExams(int id,ExamsDto examsDto);
    boolean deleteExams(int id);
    void addExams(ExamsDto examsDto);
    void confirmExams(ExamsDto examsDto);

}
