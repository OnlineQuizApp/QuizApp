package com.example.project_module6.service;

import com.example.project_module6.dto.QuestionDtoResponse;
import com.example.project_module6.dto.QuestionsDto;
import com.example.project_module6.model.Questions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface IQuestionService {
    boolean createQuestions(QuestionsDto questionsDto);
    void readAndWriteFile(MultipartFile file);
    boolean updateQuestion(int id, QuestionsDto questionsDto);
    boolean deleteQuestion(int id);
    Page<Questions> searchQuestionByCategory(int category, Pageable pageable);
    Page<Questions> findAllQuestions(Pageable pageable);
    QuestionDtoResponse findByIdDetail(int id);
    Questions findById(int id);
}
