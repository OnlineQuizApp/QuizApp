package com.example.project_module6.service;

import com.example.project_module6.dto.AnswersDto;
import com.example.project_module6.dto.ExamsQuestionDataDto;
import com.example.project_module6.dto.ExamsQuestionsResponseDto;
import com.example.project_module6.dto.QuestionsDto;
import com.example.project_module6.model.Answers;
import com.example.project_module6.model.Questions;
import com.example.project_module6.repository.IExamsQuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ExamsQuestionsService implements IExamsQuestionsService {
    @Autowired
    private IExamsQuestionRepository examsQuestionRepository;

    @Override
    public Page<ExamsQuestionsResponseDto> detailExamsQuestions(int id, Pageable pageable) {
        Page<ExamsQuestionDataDto> dataDtoList = examsQuestionRepository.detailExamsQuestions(id, pageable);
        if (!dataDtoList.isEmpty()){
            return dataDtoList.map(dto -> {
                ExamsQuestionsResponseDto responseDto = new ExamsQuestionsResponseDto();
                responseDto.setId(dto.getId());
                responseDto.setNumberQuestions(dto.getNumberOfQuestions());
                responseDto.setCategory(dto.getCategory());
                responseDto.setTitle(dto.getTitle());
                responseDto.setTestTime(dto.getTestTime());
                List<AnswersDto> answers = new ArrayList<>();
                String answersRaw = dto.getAnswers();
                if (answersRaw != null && !answersRaw.isEmpty()) {
                    String[] a = answersRaw.split(",");
                    for (String answersDto : a) {
                        System.out.println("Ansers:------------"+answersDto);
                        if (answersDto.contains("-")) {
                            String[] parts = answersDto.split("-");
                            if (parts.length == 2) {
                                try {
                                    boolean isCorrect = "1".equals(parts[1].trim());
                                    answers.add(new AnswersDto(parts[0], isCorrect));
                                } catch (Exception e) {
                                    System.out.println("Lỗi khi parse boolean: " + answersDto);
                                }
                            } else {
                                System.out.println("Lỗi định dạng answersDto (không chia được thành 2 phần): " + answersDto);
                            }
                        } else {
                            System.out.println("Lỗi định dạng answersDto (không chứa dấu '-'): " + answersDto);
                        }
                    }
                }
                List<QuestionsDto> questions = new ArrayList<>();
                String questionRow = dto.getQuestionsContent();
                if (questionRow != null && !questionRow.isEmpty()) {
                    String[] question = questionRow.split(",");
                    for (int i = 0; i < question.length; i++) {
                        String questionText = question[i]; // lấy nội dung câu hỏi
                        List<AnswersDto> subAnswers = new ArrayList<>(); // tạo danh sách đáp án cho mỗi câu hỏi
                        int start = i * 4;  // Lấy 4 đáp án cho câu hỏi thứ i
                        int end = Math.min(start + 4, answers.size()); // lấy tối đa 4 đáp án, Math.min tránh lỗi nếu không đủ 4 đáp án
                        for (int j = start; j < end; j++) {
                            subAnswers.add(answers.get(j));
                        }
                        questions.add(new QuestionsDto(questionText, subAnswers));
                    }
                }
                responseDto.setQuestions(questions);

                return responseDto;
            });
        }
        return null;
    }

    @Override
    public List<ExamsQuestionDataDto> getExamsQuestions(int id) {
        List<ExamsQuestionDataDto> dataDtoList = examsQuestionRepository.getExamsQuestions(id);
        return dataDtoList;
    }
}
