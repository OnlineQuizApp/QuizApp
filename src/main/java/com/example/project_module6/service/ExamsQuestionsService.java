package com.example.project_module6.service;

import com.example.project_module6.dto.AnswersDto;
import com.example.project_module6.dto.ExamsQuestionDataDto;
import com.example.project_module6.dto.ExamsQuestionsResponseDto;
import com.example.project_module6.dto.QuestionsDto;
import com.example.project_module6.model.Answers;
import com.example.project_module6.model.Questions;
import com.example.project_module6.repository.IExamsQuestionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
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
                responseDto.setId(dto.getExamsId());
                responseDto.setNumberQuestions(dto.getNumberOfQuestions());
                responseDto.setCategory(dto.getCategory());
                responseDto.setTitle(dto.getTitle());
                responseDto.setTestTime(dto.getTestTime());
                if (dto.getImg()!=null){
                    responseDto.setImg(dto.getImg());
                }
                if (dto.getVideo()!=null){
                    responseDto.setVideo(dto.getVideo());
                }
                List<AnswersDto> answers = new ArrayList<>();
                String answersRaw = dto.getAnswers();
                if (answersRaw != null && !answersRaw.isEmpty()) {
                    String[] a = answersRaw.split(",");
                    for (String answersDto : a) {
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
                List<QuestionsDto> questions = new ArrayList<>(); //  tạo danh sách chứa các câu hỏi
                Set<String> seenQuestions = new HashSet<>(); // tạo danh sách để kiểm tra nếu có câu hỏi trùng lặp
                int questionIndex = 0; //  chỉ số câu hỏi  để tính vị trí bắt đầu của 4  đáp án
                String questionRow = dto.getQuestionsContent();
                if (questionRow != null && !questionRow.isEmpty()) {
                    String[] question = questionRow.split(",");
                    for (int i = 0; i < question.length; i++) {
                        String questionText = question[i]; // lấy nội dung câu hỏi
                        if (seenQuestions.contains(questionText)&&seenQuestions.size()==1) {
                            continue; // kiểm tra nếu đã có câu hỏi đó rồi thì bỏ qua
                        }
                        seenQuestions.add(questionText); // nếu chưa có câu hỏi đó rồi thì thêm vào
                        List<AnswersDto> subAnswers = new ArrayList<>(); // tạo danh sách đáp án cho mỗi câu hỏi
                        int start = questionIndex * 4;  // tính vị trị bắt đầu để  Lấy 4 đáp án cho câu hỏi thứ i
                        if (start >= answers.size()) {
                            System.out.println("Không đủ đáp án cho câu: " + questionText);
                            continue;
                        }
                        int end = Math.min(start + 4, answers.size()); // lấy tối đa 4 đáp án, Math.min tránh lỗi nếu không đủ 4 đáp án
                        for (int j = start; j < end; j++) {
                            subAnswers.add(answers.get(j));
                        }
                        questions.add(new QuestionsDto(questionText, subAnswers,responseDto.getImg(),responseDto.getVideo()));
                        questionIndex++; //  chỉ tăng chỉ số để tính 4 đáp án kế tiếp khi câu hỏi được thêm vào
                    }
                }
                if ((questionRow == null || questionRow.isEmpty()) && responseDto.getImg() != null && !answers.isEmpty()) {
                    int numberOfQuestions = (int) Math.ceil(answers.size() / 4.0);
                    for (int i = 0; i < numberOfQuestions; i++) {
                        List<AnswersDto> subAnswers = new ArrayList<>();
                        int start = questionIndex * 4; // tính vị trị bắt đầu để  Lấy 4 đáp án cho câu hỏi thứ i
                        if (start >= answers.size()) {
                            System.out.println(" Không đủ đáp án cho câu: " + questionIndex);
                            continue;
                        }
                        int end = Math.min(start + 4, answers.size());
                        for (int j = start; j < end; j++) {
                            subAnswers.add(answers.get(j));
                        }
                        // Tạo câu hỏi chỉ với img, không gán content
                        questions.add(new QuestionsDto(null, subAnswers, responseDto.getImg(),responseDto.getVideo()));
                        questionIndex++;
                    }
                }
                responseDto.setQuestions(questions);

                return responseDto;
            });
        }
        return null;
    }

    @Override
    public List<ExamsQuestionsResponseDto> detailExamsQuestionsUpdate(int id) {
        List<ExamsQuestionDataDto> dataDtoList = examsQuestionRepository.detailExamsQuestionsUpdate(id);
        if (!dataDtoList.isEmpty()) {
            List<ExamsQuestionsResponseDto> result = new ArrayList<>();
            for (ExamsQuestionDataDto dto : dataDtoList) {
                ExamsQuestionsResponseDto responseDto = new ExamsQuestionsResponseDto();
                responseDto.setId(dto.getExamsId());
                responseDto.setNumberQuestions(dto.getNumberOfQuestions());
                responseDto.setCategory(dto.getCategory());
                responseDto.setTitle(dto.getTitle());
                responseDto.setTestTime(dto.getTestTime());

                if (dto.getImg() != null) {
                    responseDto.setImg(dto.getImg());
                }
                if (dto.getVideo() != null) {
                    responseDto.setVideo(dto.getVideo());
                }

                List<AnswersDto> answers = new ArrayList<>();
                String answersRaw = dto.getAnswers();
                if (answersRaw != null && !answersRaw.isEmpty()) {
                    String[] a = answersRaw.split(",");
                    for (String answersDto : a) {
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
                Set<String> seenQuestions = new HashSet<>();
                int questionIndex = 0;
                String questionRow = dto.getQuestionsContent();
                if (questionRow != null && !questionRow.isEmpty()) {
                    String[] question = questionRow.split(",");
                    for (int i = 0; i < question.length; i++) {
                        String questionText = question[i];
                        if (seenQuestions.contains(questionText)) {
                            continue;
                        }
                        seenQuestions.add(questionText);
                        List<AnswersDto> subAnswers = new ArrayList<>();
                        int start = questionIndex * 4;
                        if (start >= answers.size()) {
                            System.out.println("Không đủ đáp án cho câu: " + questionText);
                            continue;
                        }
                        int end = Math.min(start + 4, answers.size());
                        for (int j = start; j < end; j++) {
                            subAnswers.add(answers.get(j));
                        }
                        int idQuestions =dto.getQuestionsId();
                        questions.add(new QuestionsDto(idQuestions,questionText, subAnswers, responseDto.getImg(), responseDto.getVideo()));
                        questionIndex++;
                    }
                }

                if ((questionRow == null || questionRow.isEmpty()) && responseDto.getImg() != null && !answers.isEmpty()) {
                    int numberOfQuestions = (int) Math.ceil(answers.size() / 4.0);
                    for (int i = 0; i < numberOfQuestions; i++) {
                        List<AnswersDto> subAnswers = new ArrayList<>();
                        int start = questionIndex * 4;
                        if (start >= answers.size()) {
                            System.out.println("Không đủ đáp án cho câu: " + questionIndex);
                            continue;
                        }
                        int end = Math.min(start + 4, answers.size());
                        for (int j = start; j < end; j++) {
                            subAnswers.add(answers.get(j));
                        }
                        int idQuestions =dto.getQuestionsId();
                        questions.add(new QuestionsDto(idQuestions,null, subAnswers, responseDto.getImg(), responseDto.getVideo()));
                        questionIndex++;
                    }
                }
                responseDto.setQuestions(questions);
                result.add(responseDto);
            }
            return result;
        }

        return null;
    }


    @Override
    public List<ExamsQuestionDataDto> getExamsQuestions(int id) {
        List<ExamsQuestionDataDto> dataDtoList = examsQuestionRepository.getExamsQuestions(id);
        return dataDtoList;
    }

}
