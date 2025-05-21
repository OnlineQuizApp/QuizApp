package com.example.project_module6.service;

import com.example.project_module6.dto.ExamsDto;

import com.example.project_module6.model.ExamQuestions;
import com.example.project_module6.model.Exams;
import com.example.project_module6.model.Questions;

import com.example.project_module6.repository.IExamsQuestionRepository;
import com.example.project_module6.repository.IExamsRepository;
import com.example.project_module6.repository.IQuestionsRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
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
    public void addExamsRandom(ExamsDto examsDto) {
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
    public Exams updateExamsRandom(int id, ExamsDto examsDto) {
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
               examsQuestionRepository.deleteByExamId(exams.getId());// xóa câu hỏi cũ trong bảng trung gian
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
              return exams;
           }
       }
       return null;
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

    @Override
    public Exams addExams(ExamsDto examsDto) {
        Integer numberOfQuestions = examsDto.getNumberOfQuestions();
        Integer countQuestions = questionsRepository.countQuestions(examsDto.getCategory());
        if (countQuestions < numberOfQuestions) {
            throw new IllegalArgumentException("Không đủ câu hỏi! Hiện chỉ có " + countQuestions + " câu.");
        }else {
            Exams exams =new Exams();
            BeanUtils.copyProperties(examsDto,exams);
            examsRepository.save(exams);
            return exams;
        }

    }

    @Override
    @Transactional
    public void confirmExams(Integer examId, List<Integer> questionsId) {
        Exams exams = examsRepository.findById(examId).orElse(null);
        if (exams!=null){
            List<Questions> questionsList = questionsRepository.findQuestionsByCategory(exams.getCategory());
            if (!questionsList.isEmpty()&&!questionsId.isEmpty()){
                List<Questions> selectQuestions= questionsList.stream()
                        .filter(q ->questionsId.contains(q.getId()))//lọc ra những id câu hỏi nào trùng với danh sách id câu hỏi thêm vào đề
                        .collect(Collectors.toList());
                int numberOfQuestions=exams.getNumberOfQuestions();
                if (selectQuestions.size()!=numberOfQuestions){
                    throw new IllegalArgumentException("Số câu hỏi chọn không trùng với số lượng câu hỏi của đề yêu cầu!");
                }
                examsQuestionRepository.deleteByExamId(examId);
                double score = 10.0/numberOfQuestions;
                for (Questions newQuestions:selectQuestions){
                    ExamQuestions examQuestions =  new ExamQuestions();
                       examQuestions.setExam(exams);
                       examQuestions.setQuestion(newQuestions);
                       examQuestions.setScore(score);
                       examsQuestionRepository.save(examQuestions);
                }
            }
        }
    }

    @Override
    public boolean updateExams(int id, ExamsDto examsDto, List<Integer> questionsId) {
       Exams exams = examsRepository.findById(id);
       if (exams!=null){
           List<Questions> questionsList=questionsRepository.findQuestionsByCategory(exams.getCategory());
           if (!questionsList.isEmpty()&&!questionsId.isEmpty()){
               List<Questions> questionsSelect=questionsList.stream().filter(q->questionsId.contains(q.getId())).collect(Collectors.toList());
               int numberOfQuestions=exams.getNumberOfQuestions();
               if (questionsSelect.size()!=numberOfQuestions){
                   throw new IllegalArgumentException("Số câu hỏi chọn không trùng với số lượng câu hỏi của đề yêu cầu!");
               }
           }
       }
       return false;
    }
}
