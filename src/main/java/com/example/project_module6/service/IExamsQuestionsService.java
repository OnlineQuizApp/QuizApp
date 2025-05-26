package com.example.project_module6.service;

import com.example.project_module6.dto.ExamsQuestionDataDto;
import com.example.project_module6.dto.ExamsQuestionsResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IExamsQuestionsService {
   Page<ExamsQuestionsResponseDto> detailExamsQuestions(int id, Pageable pageable);
   List<ExamsQuestionsResponseDto> detailExamsQuestionsUpdate(int id);
   List<ExamsQuestionDataDto> getExamsQuestions(int id);

}
