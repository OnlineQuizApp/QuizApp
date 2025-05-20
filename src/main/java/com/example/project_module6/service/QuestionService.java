package com.example.project_module6.service;

import com.example.project_module6.dto.AnswersDto;
import com.example.project_module6.dto.QuestionDetailDataDto;
import com.example.project_module6.dto.QuestionDtoResponse;
import com.example.project_module6.dto.QuestionsDto;
import com.example.project_module6.model.*;

import com.example.project_module6.repository.IAnswersRepository;
import com.example.project_module6.repository.ICategoryRepository;
import com.example.project_module6.repository.IQuestionsRepository;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionService implements IQuestionService {
    @Autowired
    private IQuestionsRepository questionsRepository;
    @Autowired
    private IAnswersRepository answersRepository;
    @Autowired
    private ICategoryRepository categoryRepository;

    @Override
    public boolean createQuestions(QuestionsDto questionsDto) {
        Questions questions = new Questions();

        BeanUtils.copyProperties(questionsDto, questions);
        if (questionsDto.getContent()!=null){
            questions.setContent(questionsDto.getContent());
        }
        if (questionsDto.getCategory()!=null){
            questions.setCategory(questionsDto.getCategory());
        }
        if (questionsDto.getImg()!=null){
            questions.setImg(questionsDto.getImg());
        }
        if (questionsDto.getVideo()!=null){
            questions.setVideo(questionsDto.getVideo());
        }
        questionsRepository.save(questions);
        List<Answers> answersList = questionsDto.getAnswers();
        if (!answersList.isEmpty()){
            for (Answers answers:answersList){
                answers.setQuestion(questions);
                answersRepository.save(answers);
            }
        }
        return true;
    }


    @Override
    public void readAndWriteFile(MultipartFile file) {
        DataFormatter formatter = new DataFormatter();
        boolean hasNewQuestion = false;
        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            Questions currentQuestion = null;
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                // Lấy ô chứa "Câu 1", "Câu 2", để biết bắt đầu câu hỏi mới (cột C – index 2)
                Cell questionIndexCell = row.getCell(2);
                // Lấy nội dung câu hỏi (cột D – index 3)
                Cell questionContentCell = row.getCell(3);
                Cell answerContentCell = row.getCell(5);  // cột G (nội dung đáp án)
                Cell isCorrectCell = row.getCell(6);  // cột đáp án đúng / sai

                if (questionIndexCell != null && questionContentCell != null) {
                    // Tạo câu hỏi mới

                    String questionContent= questionContentCell.getStringCellValue().trim();
                    if (questionContent.isEmpty()){
                        continue;
                    }
                    boolean questionCheck=questionsRepository.existsByContent(questionContent);
                    if (questionCheck){
                        currentQuestion=null;
                        continue;
                    }
                    currentQuestion = new Questions();
                    currentQuestion.setContent(questionContentCell.getStringCellValue());
                    // Đọc category_id từ cột E (index 4)
                    Cell categoryIdCell = row.getCell(4);
                    // Đọc name từ cột H (index 7)
                    Cell categoryNameCell = row.getCell(7);

                    if (categoryIdCell != null) {
                        int categoryId = (int) categoryIdCell.getNumericCellValue();
                        Categorys category = categoryRepository.findById(categoryId).orElse(null);
                        if (category != null) {
                            currentQuestion.setCategory(category);
                        }else {
                            Categorys categorys = new Categorys();
                            if (categoryNameCell!=null){
                                categorys.setName(formatter.formatCellValue(categoryNameCell));
                            }
                            categoryRepository.save(categorys);
                            currentQuestion.setCategory(categorys);
                        }
                    }
                    // Lưu câu hỏi vào DB
                    currentQuestion = questionsRepository.save(currentQuestion);
                    hasNewQuestion = true;
                    continue;
                }

                // Nếu đang trong câu hỏi hiện tại, đọc đáp án (từ cột F, G, H)
                if (answerContentCell != null && isCorrectCell != null) {

                    String answerText = formatter.formatCellValue(answerContentCell);
                    if (answerText == null || answerText.trim().isEmpty()) {
                        continue;
                    }
                    if (currentQuestion!=null){
                        Answers answer = new Answers();
                        answer.setQuestion(currentQuestion);
                        answer.setContent(answerText);
                        answer.setCorrect(isCorrectCell.getBooleanCellValue());
                        answersRepository.save(answer);
                    }

                }
            }
            if (!hasNewQuestion) {
                throw new RuntimeException("File này đã được thêm, không có câu hỏi mới nào.");
            }
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi xử lý file Excel: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean updateQuestion(int id, QuestionsDto questionsDto) {
        Questions questions = questionsRepository.findById(id);
        if (questions != null) {
            questions.setContent(questionsDto.getContent());
            questions.setCategory(questionsDto.getCategory());

            // 1. Xóa tất cả đáp án cũ
            List<Answers> oldAnswers = answersRepository.findByQuestionId(id);
            answersRepository.deleteAll(oldAnswers);

            if (questionsDto.getImg() != null) {
                questions.setImg(questionsDto.getImg());
            }
            // 2. Gán lại đáp án mới từ DTO
            List<Answers> newAnswers = questionsDto.getAnswers().stream().map(a -> {
                Answers answer = new Answers();
                answer.setContent(a.getContent());
                answer.setCorrect(a.isCorrect());
                answer.setQuestion(questions); // Liên kết lại câu hỏi
                return answer;
            }).collect(Collectors.toList());

            // 3. Lưu câu hỏi trước (nếu cần cascade thì không cần bước này)
            questionsRepository.save(questions);

            // 4. Lưu đáp án mới
            answersRepository.saveAll(newAnswers);
            questionsRepository.save(questions);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteQuestion(int id) {
        Questions questions = questionsRepository.findById(id);
        if (questions != null) {
            questions.setSoftDelete(true);
            questionsRepository.save(questions);
            return true;
        }
        return false;
    }

    @Override
    public Page<Questions> searchQuestionByQuestionContent(String content, Pageable pageable) {
        return questionsRepository.searchQuestionByQuestionContent("%"+content+"%", pageable);
    }

    @Override
    public Page<Questions> searchQuestionByCategory(String category, Pageable pageable) {
        return questionsRepository.searchQuestionByCategory(category,pageable);
    }

    @Override
    public Page<Questions> findAllQuestions(Pageable pageable) {
        return questionsRepository.getAllQuestions(pageable);
    }

    @Override
    public QuestionDtoResponse findByIdDetail(int id) {
        QuestionDetailDataDto questionDetailDtoResponse = questionsRepository.findQuestionsById(id);
        QuestionDtoResponse questionDtoResponse = new QuestionDtoResponse();
        questionDtoResponse.setId(questionDetailDtoResponse.getId());
        questionDtoResponse.setContent(questionDetailDtoResponse.getQuestionsContent());
        questionDtoResponse.setImg(questionDetailDtoResponse.getImg());
        Categorys categorys = new Categorys(questionDetailDtoResponse.getCategoryId(),questionDetailDtoResponse.getCategoryName());
        questionDtoResponse.setCategory(categorys);
        List<AnswersDto> answersList = new ArrayList<>();
        String[] idAnswers = questionDetailDtoResponse.getAnswersIds().split(",");
        String[] nameAnswers = questionDetailDtoResponse.getAnswersContents().split(",");
        String[] corrects = questionDetailDtoResponse.getCorrects().split(",");
//        answersList.add(new AnswersDto(Integer.parseInt(idAnswers[0]),nameAnswers[0],Boolean.parseBoolean(corrects[0])));
//        answersList.add(new AnswersDto(Integer.parseInt(idAnswers[1]),nameAnswers[1],Boolean.parseBoolean(corrects[1])));
//        answersList.add(new AnswersDto(Integer.parseInt(idAnswers[2]),nameAnswers[2],Boolean.parseBoolean(corrects[2])));
//        answersList.add(new AnswersDto(Integer.parseInt(idAnswers[3]),nameAnswers[3],Boolean.parseBoolean(corrects[3])));
        for (int i = 0; i < idAnswers.length; i++) {
            boolean isCorrect = corrects[i].equals("1");
            answersList.add(new AnswersDto(Integer.parseInt(idAnswers[i]), nameAnswers[i], isCorrect));
        }
        System.out.println(Arrays.toString(corrects));
        questionDtoResponse.setAnswers(answersList);
        return questionDtoResponse;
    }

    @Override
    public Questions findById(int id) {
        return questionsRepository.findById(id);
    }
}
