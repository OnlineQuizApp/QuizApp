package com.example.project_module6.service;

import com.example.project_module6.dto.ExamsDto;
import com.example.project_module6.dto.QuestionsDto;
import com.example.project_module6.model.ExamQuestions;
import com.example.project_module6.model.Exams;
import com.example.project_module6.model.Questions;
import com.example.project_module6.repository.IAnswersRepository;
import com.example.project_module6.repository.IExamsQuestionRepository;
import com.example.project_module6.repository.IExamsRepository;
import com.example.project_module6.repository.IQuestionsRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class ExamsService implements IExamsService{
    @Autowired
    private IExamsRepository examsRepository;
    @Autowired
    private IQuestionsRepository questionsRepository;
    @Autowired
    private IExamsQuestionRepository examsQuestionRepository;
    @Override
    public Page<Exams> getAlExams(Pageable pageable) {
        return examsRepository.getAllExams(pageable);
    }

    @Override
    public Page<Exams> searchExamsByCategory(String category, Pageable pageable) {
        return examsRepository.searchExamsByCategory("%"+category+"%",pageable);
    }

    @Override
    public void addExams(ExamsDto examsDto) {
        Exams exams =new Exams();
        BeanUtils.copyProperties(examsDto,exams);
        examsRepository.save(exams);
        Integer numberOfQuestions = examsDto.getNumberOfQuestions();
        Integer countQuestions = questionsRepository.countQuestions(exams.getCategory());
        if (countQuestions < numberOfQuestions) {
            throw new IllegalArgumentException("Không đủ câu hỏi! Hiện chỉ có " + countQuestions + " câu.");
        }else {
            List<Questions> questions = questionsRepository.findRandomQuestions(exams.getCategory(),numberOfQuestions);
            if (!questions.isEmpty()){
                double score = 10.0/numberOfQuestions;
                for (Questions question : questions){
                    ExamQuestions examQuestions = new ExamQuestions();
                    examQuestions.setQuestion(question);
                    examQuestions.setExam(exams);
                    examQuestions.setScore(score);
                    examsQuestionRepository.save(examQuestions);
                }
            }
        }

    }
    @Transactional
    @Override
    public boolean updateExams(int id, ExamsDto examsDto) {
       Exams exams = examsRepository.findById(id);
        System.out.println("Tìm thấy exam: " + exams);
       if (exams!=null){
           Integer numberOfQuestions = examsDto.getNumberOfQuestions();

           Integer countQuestions = questionsRepository.countQuestions(exams.getCategory());
           System.out.println("Số lượng câu hỏi hiện có: " + countQuestions);
           if (countQuestions<numberOfQuestions){
               throw new IllegalArgumentException("Không đủ câu hỏi! Hiện chỉ có " + countQuestions + " câu.");
           }else {
               exams.setCategory(examsDto.getCategory());
               exams.setTitle(examsDto.getTitle());
               exams.setTestTime(examsDto.getTestTime());
               exams.setNumberOfQuestions(examsDto.getNumberOfQuestions());
               exams.setSoftDelete(examsDto.isSoftDelete());
               examsQuestionRepository.deleteByExamId(exams.getId()); /// xóa câu hỏi cũ trong bảng trung gian
               System.out.println("Xóa các câu hỏi cũ thành công.");
               List<Questions> questionsList = questionsRepository.findRandomQuestions(exams.getCategory(),numberOfQuestions);
               System.out.println("Số câu hỏi mới random được: " + questionsList.size());
               double score = 10.0/numberOfQuestions;
               for (Questions questions:questionsList){
                   ExamQuestions examQuestions =  new ExamQuestions();
                   examQuestions.setExam(exams);
                   examQuestions.setQuestion(questions);
                   examQuestions.setScore(score);
                   examsQuestionRepository.save(examQuestions);
                   System.out.println("Đã lưu exam và exam_questions mới.");
               }

               examsRepository.save(exams);
               return true;
           }
       }
       return false;
    }

    @Override
    public boolean deleteExams(int id) {
        Exams exams = examsRepository.findById(id);
        if (exams!=null){
            exams.setSoftDelete(true);
            examsQuestionRepository.deleteByExamId(exams.getId());
            examsRepository.save(exams);
            return true;
        }
        return false;
    }
}
