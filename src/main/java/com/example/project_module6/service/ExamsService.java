package com.example.project_module6.service;

import com.example.project_module6.dto.ExamResponseDto;
import com.example.project_module6.dto.ExamsDto;

import com.example.project_module6.model.ExamQuestions;
import com.example.project_module6.model.Exams;
import com.example.project_module6.model.Questions;

import com.example.project_module6.model.Results;
import com.example.project_module6.repository.IExamsQuestionRepository;
import com.example.project_module6.repository.IExamsRepository;
import com.example.project_module6.repository.IQuestionsRepository;
import com.example.project_module6.repository.IResultRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
    @Autowired
    private IResultRepository resultRepository;
    @Override
    public Page<Exams> getAlExams(Pageable pageable) {
        return examsRepository.getAllExams(pageable);
    }

    @Override
    public Page<Exams> searchExamsByCategory(String category, Pageable pageable) {
        return examsRepository.searchExamsByCategory("%"+category+"%",pageable);
    }

    @Override
    public Page<Exams> searchExamsByTitle(String title, Pageable pageable) {
        return examsRepository.searchExamsByTitle("%"+title+"%",pageable);
    }

    @Override
    public void addExamsRandom(ExamsDto examsDto) {
        Exams exams =new Exams();
        BeanUtils.copyProperties(examsDto,exams);
        List<Exams> exams1 = examsRepository.getAllExams();
        for (Exams exams2 :exams1){
            if (exams.getTitle().equals(exams2.getTitle())&&exams.getCategory().equals(exams2.getCategory())){
                throw new IllegalArgumentException("Đề thi này đã có trong hệ thống");
            }
        }
        examsRepository.save(exams);
        Integer numberOfQuestions = examsDto.getNumberOfQuestions();
        Integer countQuestions = questionsRepository.countQuestions(exams.getCategory());
        if (countQuestions < numberOfQuestions) {
            throw new IllegalArgumentException("Không đủ câu hỏi cho danh mục bạn chọn ! Hiện chỉ có " + countQuestions + " câu cho danh mục đó");
        }else {
            List<Questions> questions = questionsRepository.findRandomQuestions(exams.getCategory(),numberOfQuestions);
            if (!questions.isEmpty()){
                double score = 10.0/numberOfQuestions;
                for (Questions question : questions){
                    ExamQuestions examQuestions = new ExamQuestions();
                    examQuestions.setQuestion(question);
                    examQuestions.setExam(exams);
                    examQuestions.setScore(score);
                    question.setExitsExamsId(true);
                    questionsRepository.save(question);
                    examsQuestionRepository.save(examQuestions);
                }
            }
        }

    }
    @Transactional
    @Override
    public Exams updateExamsRandom(int id, ExamsDto examsDto) {
       Exams exams = examsRepository.findById(id);
       if (exams!=null){
           Integer numberOfQuestions = examsDto.getNumberOfQuestions();
           Integer countQuestions = questionsRepository.countQuestions(exams.getCategory());
           System.out.println("Số lượng câu hỏi hiện có: " + countQuestions);
           if (countQuestions<numberOfQuestions){
               throw new IllegalArgumentException("Không đủ câu hỏi cho danh mục bạn chọn ! Hiện chỉ có " + countQuestions + " câu cho danh mục đó");
           }else {
               List<Exams> exams1 = examsRepository.getAllExams();
               for (Exams exams2 :exams1){
                   if (examsDto.getTitle().equals(exams2.getTitle())&&examsDto.getCategory().equals(exams2.getCategory())){
                       throw new IllegalArgumentException("Đề thi này đã có trong hệ thống");
                   }
               }
               exams.setCategory(examsDto.getCategory());
               exams.setTitle(examsDto.getTitle());
               exams.setTestTime(examsDto.getTestTime());
               exams.setNumberOfQuestions(examsDto.getNumberOfQuestions());
               exams.setSoftDelete(examsDto.isSoftDelete());
               List<ExamQuestions> oldExamQuestions = examsQuestionRepository.findByExam_Id(id);
               for (ExamQuestions eq : oldExamQuestions) {
                   Questions oldQ = eq.getQuestion();
                   oldQ.setExitsExamsId(false);
                   questionsRepository.save(oldQ);
               }
               examsQuestionRepository.deleteByExamId(exams.getId());// xóa câu hỏi cũ trong bảng trung gian
               List<Questions> questionsList = questionsRepository.findRandomQuestions(exams.getCategory(),numberOfQuestions);
               double score = 10.0/numberOfQuestions;
               for (Questions questions:questionsList){
                   ExamQuestions examQuestions =  new ExamQuestions();
                   examQuestions.setExam(exams);
                   examQuestions.setQuestion(questions);
                   examQuestions.setScore(score);
                   examsQuestionRepository.save(examQuestions);
               }
               examsRepository.save(exams);
              return exams;
           }
       }
       return null;
    }

    @Override
    @Transactional
    public boolean deleteExams(int id) {
        Exams exams = examsRepository.findById(id);
        if (exams!=null){
            exams.setSoftDelete(true);
            examsRepository.save(exams);
            List<ExamQuestions> oldExamQuestions = examsQuestionRepository.findByExam_Id(id);
            for (ExamQuestions eq : oldExamQuestions) {
                Questions oldQ = eq.getQuestion();
                int countQuestions = examsQuestionRepository.countByQuestion_Id(oldQ.getId());
                if (countQuestions==0){
                    oldQ.setExitsExamsId(false);
                    questionsRepository.save(oldQ);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public Exams addExams(ExamsDto examsDto) {
        Integer numberOfQuestions = examsDto.getNumberOfQuestions();
        Integer countQuestions = questionsRepository.countQuestions(examsDto.getCategory());
        if (countQuestions < numberOfQuestions) {
            throw new IllegalArgumentException("Không đủ câu hỏi cho danh mục bạn chọn ! Hiện chỉ có " + countQuestions + " câu cho danh mục đó");
        } else {
            List<Exams> exams1 = examsRepository.getAllExams();
            for (Exams exams2 :exams1){
                if (examsDto.getTitle().equals(exams2.getTitle())&&examsDto.getCategory().equals(exams2.getCategory())){
                    throw new IllegalArgumentException("Đề thi này đã có trong hệ thống");
                }
            }
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
                List<ExamQuestions> oldExamQuestions = examsQuestionRepository.findByExam_Id(examId);
                for (ExamQuestions eq : oldExamQuestions) {
                    Questions oldQ = eq.getQuestion();
                    oldQ.setExitsExamsId(false);
                    questionsRepository.save(oldQ);
                }
                examsQuestionRepository.deleteByExamId(examId);
                double score = 10.0/numberOfQuestions;
                for (Questions newQuestions:selectQuestions){
                    ExamQuestions examQuestions =  new ExamQuestions();
                       examQuestions.setExam(exams);
                       examQuestions.setQuestion(newQuestions);
                       examQuestions.setScore(score);
                        newQuestions.setExitsExamsId(true);
                       examsQuestionRepository.save(examQuestions);
                }
            }
        }
    }
    @Override
    @Transactional
    public void confirmExamsUpdate(Integer examId, List<Integer> questionsId) {
        Exams exams = examsRepository.findById(examId).orElse(null);
        if (exams!=null){
            List<Exams> exams1 = examsRepository.getAllExams();
            for (Exams exams2 :exams1){
                if (exams.getTitle().equals(exams2.getTitle())&&exams.getCategory().equals(exams2.getCategory())&&exams.getId()!=exams2.getId()){
                    throw new IllegalArgumentException("Đề thi này đã có trong hệ thống");
                }
            }
            List<Questions> questionsList = questionsRepository.findQuestionsByCategory(exams.getCategory());
            if (!questionsList.isEmpty()&&!questionsId.isEmpty()){
                List<ExamQuestions> existingExamQuestions = examsQuestionRepository.findByExam_Id(examId);
                Set<Integer> existingQuestionIds = existingExamQuestions.stream()
                        .map(eq -> eq.getQuestion().getId())
                        .collect(Collectors.toSet()); // lưu các câu hỏi đã có trong đề vào set

                List<Questions> selectQuestions= questionsList.stream()
                        .filter(q ->questionsId.contains(q.getId()) && !existingQuestionIds.contains(q.getId()))//lọc ra những id câu hỏi nào trùng với danh sách id câu hỏi thêm vào đề
                        .collect(Collectors.toList());
                int newTotal = existingQuestionIds.size() + selectQuestions.size();
                exams.setNumberOfQuestions(newTotal);
                double score = 10.0/newTotal;
                examsRepository.save(exams);
                for (Questions newQuestions:selectQuestions){
                    ExamQuestions examQuestions =  new ExamQuestions();
                    examQuestions.setExam(exams);
                    examQuestions.setQuestion(newQuestions);
                    newQuestions.setExitsExamsId(true);
                    questionsRepository.save(newQuestions);
                    examsQuestionRepository.save(examQuestions);
                }
                List<ExamQuestions> allExamQuestions = examsQuestionRepository.findByExam_Id(examId);
                for (ExamQuestions eq : allExamQuestions) {
                    eq.setScore(score);
                    examsQuestionRepository.save(eq);
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

    @Override
    public List<ExamResponseDto> getAllExamsWithStatus(Integer userId, Pageable pageable) {
        Page<Exams> exams = examsRepository.getAllExams(pageable);
        List<ExamResponseDto> response = new ArrayList<>();

        int index = pageable.getPageNumber() * pageable.getPageSize() + 1;

        for (Exams exam : exams) {
            Optional<Results> result = resultRepository.findByUserIdAndExamId(userId, exam.getId());

            String status = result.isPresent() ? "Đã thi" : "Chưa thi";
            String action = result.isPresent() ? "Xem kết quả" : "Làm bài thi";

            response.add(new ExamResponseDto(exam.getId(),index++, exam.getTitle(), exam.getCategory(), status, action));
        }

        return response;
    }

    @Override
    public int countBySoftDeleteFalse() {
        return examsRepository.countBySoftDeleteFalse();
    }

    @Override
    public void deleteQuestionOfExams(int idExams, int idQuestions) {
        Exams exams = examsRepository.findById(idExams);
        if (exams!=null){
            exams.setNumberOfQuestions(exams.getNumberOfQuestions()-1);
            examsRepository.save(exams);
            examsRepository.deleteExamsByQuestionsId(idExams,idQuestions);
            Questions q = questionsRepository.findById(idQuestions);
            int count = examsQuestionRepository.countByQuestion_Id(idQuestions);
            if (count==0){
                if (q!=null){
                    q.setExitsExamsId(false);
                    questionsRepository.save(q);
                }
            }
        }
    }
}
