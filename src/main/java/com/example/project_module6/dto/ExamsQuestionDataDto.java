package com.example.project_module6.dto;

import java.sql.Time;


public interface ExamsQuestionDataDto {
     int getExamsId();
     int getQuestionsId();
     String getTitle();
     String getCategory();
     int getNumberOfQuestions();
     String getImg();
     String getVideo();
     Time getTestTime();
     String getAnswers();
     String getQuestionsContent();

}
