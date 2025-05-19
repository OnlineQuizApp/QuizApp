package com.example.project_module6.dto;

import java.sql.Time;


public interface ExamsQuestionDataDto {
     int getId();
     String getTitle();
     String getCategory();
     int getNumberOfQuestions();
     Time getTestTime();
     String getAnswers();
     String getQuestionsContent();

}
