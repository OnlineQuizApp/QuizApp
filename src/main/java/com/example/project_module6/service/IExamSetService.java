package com.example.project_module6.service;

import com.example.project_module6.dto.ExamSetDetailDto;
import com.example.project_module6.dto.ExamSetDto;
import com.example.project_module6.dto.ExamsDto;
import com.example.project_module6.model.ExamSets;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IExamSetService {
    Page<ExamSets> getAllExamSet(Pageable pageable);
    Page<ExamSets> getAllExamSetByName(String name, Pageable pageable);
    ExamSets createExamSet(ExamSetDto examSetDto);
    void confirmExamsSetCreate(Integer examSetId, List<Integer> examsId);
    void confirmExamsSetUpdate(Integer examSetId, List<Integer> examsId);
    ExamSets updateExamSet(int id,ExamSetDto examSetDto);
    void deleteExamSet(int id);
    boolean deleteExamByExamSetId(int examSetId,int examId);
    List<ExamSetDetailDto> detailExamSet(int id);

}
