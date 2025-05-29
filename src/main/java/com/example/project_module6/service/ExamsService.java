package com.example.project_module6.service;

import com.example.project_module6.dto.*;

import com.example.project_module6.model.*;

import com.example.project_module6.repository.*;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
    @Autowired
    private IAnswersRepository answersRepository;
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private IRatingPointRepository ratingPointRepository;
    @Autowired
    private IUserAnswersRepository userAnswersRepository;
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

    // Lấy danh sách đề thi chưa bị xóa
    @Override
    public List<ExamsDto> getAllExams() {
        return examsRepository.getAllExams().stream().map(exam -> {
            ExamsDto dto = new ExamsDto();
            dto.setId(exam.getId());
            dto.setTitle(exam.getTitle());
            dto.setCategory(exam.getCategory());
            dto.setNumberOfQuestions(exam.getNumberOfQuestions());
            dto.setTestTime(exam.getTestTime());
            return dto;
        }).collect(Collectors.toList());
    }
    // Lấy chi tiết đề thi
    @Override
    public ExamsDto getExamById(int id) {
        Exams exam = examsRepository.findById(id);
        if(exam==null){
            throw new RuntimeException("Exam not found");
        }
        if (exam.isSoftDelete()) {
            throw new RuntimeException("Exam is deleted");
        }
        ExamsDto dto = new ExamsDto();
        dto.setId(exam.getId());
        dto.setTitle(exam.getTitle());
        dto.setCategory(exam.getCategory());
        dto.setNumberOfQuestions(exam.getNumberOfQuestions());
        dto.setTestTime(exam.getTestTime());
        return dto;
    }
    @Override
    public List<QuestionDTO> getExamQuestions(int examId) {
        List<ExamQuestions> examQuestions = examsQuestionRepository.findByExam_Id(examId);
        return examQuestions.stream().map(eq -> {
            Questions question = questionsRepository.findById(eq.getQuestion().getId());
            if(question==null){
                throw new RuntimeException("Question not found");
            }
            if (question.isSoftDelete()) {
                throw new RuntimeException("Question is deleted");
            }
            QuestionDTO questionDTO = new QuestionDTO();
            questionDTO.setId(question.getId());
            questionDTO.setContent(question.getContent());
            questionDTO.setImg(question.getImg());
            questionDTO.setCategoryId(question.getCategory().getId());
            List<Answers> answers = answersRepository.findByQuestionId(question.getId());
            List<AnswersDto> answerDTOs = answers.stream().map(answer -> {
                AnswersDto answerDTO = new AnswersDto();
                answerDTO.setId(answer.getId());
                answerDTO.setContent(answer.getContent());
                answerDTO.setCorrect(answer.isCorrect());
                return answerDTO;
            }).collect(Collectors.toList());
            questionDTO.setAnswers(answerDTOs);
            return questionDTO;
        }).filter(dto -> dto != null).collect(Collectors.toList());
    }

    // Xử lý nộp bài (chưa đăng nhập)
    @Override
    public ExamResultDTO submitExam(SubmitExamRequest request) {
        Exams exam = examsRepository.findById(request.getExamId());
        if(exam==null){
            throw new RuntimeException("Exam not found");
        }
        List<ExamQuestions> examQuestions = examsQuestionRepository.findByExam_Id(exam.getId());

        int correctAnswer = 0;
        double totalScore = 0.0;

        for (UserAnswerDTO userAnswer : request.getUserAnswers()) {
            ExamQuestions examQuestion = examQuestions.stream()
                    .filter(eq -> eq.getQuestion().getId() == userAnswer.getQuestionId())
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Question not in exam"));
            List<Answers> correctAnswers = answersRepository.findByQuestionIdAndCorrectTrue(userAnswer.getQuestionId());
            int correctCount = 0;
            int wrongCount = 0;

            for (Integer selectedAnswerId : userAnswer.getSelectedAnswerIds()) {
                Answers answer = answersRepository.findById(selectedAnswerId)
                        .orElseThrow(() -> new RuntimeException("Answer not found"));
                if (answer.isCorrect()) {
                    correctCount++;
                } else {
                    wrongCount++;
                }
            }

            if (correctCount == correctAnswers.size() && wrongCount == 0) {
                // Trường hợp 1: Chọn đúng tất cả đáp án
                totalScore += examQuestion.getScore();
                correctAnswer++;
            } else if (correctCount > wrongCount) {
                // Trường hợp 2: Số đáp án đúng > số đáp án sai
                totalScore += examQuestion.getScore() * ((double) correctCount / correctAnswers.size());
                if (correctCount > 0) {
                    correctAnswer++;
                }
            }
        }

        ExamResultDTO result = new ExamResultDTO();
        result.setCorrectAnswers(correctAnswer);
        result.setTotalQuestions(examQuestions.size());
        result.setTotalScore(totalScore);
        return result;
    }

    // Xử lý nộp bài (đã đăng nhập)
    @Override
    public ExamResultDTO submitExamAuthenticated(SubmitExamRequest request, int userId) {
        ExamResultDTO resultDTO = submitExam(request); // Tính điểm như chưa đăng nhập
        RatingPoints ratingPoint = ratingPointRepository.findByUserId(userId);
        if (ratingPoint == null) {
            ratingPoint = new RatingPoints();
            ratingPoint.setUser(userRepository.findById(userId));
            ratingPoint.setAccumulatedPoints(0); // điểm đầu tiên
        }else if(resultDTO.getTotalScore()>5){
            ratingPoint.setAccumulatedPoints(ratingPoint.getAccumulatedPoints() + 1);
        }
        ratingPointRepository.save(ratingPoint);
        // Lưu kết quả vào bảng results
        Results result = new Results();
        result.setUser(userRepository.findById(userId));
        result.setExam(examsRepository.findById(request.getExamId()));
        result.setTotalScore(resultDTO.getTotalScore());
        result.setRatingPoint(ratingPoint);
        result.setSubmittedAt(LocalDate.now());
        resultRepository.save(result);

        // Lưu đáp án người dùng chọn
        for (UserAnswerDTO userAnswer : request.getUserAnswers()) {
            UserAnswers ua = new UserAnswers();
            ua.setResult(resultRepository.findById(result.getId()));
            ua.setQuestion(questionsRepository.findById(userAnswer.getQuestionId()));
            for (Integer answerId : userAnswer.getSelectedAnswerIds()) {
                Optional<Answers> answerOpt = answersRepository.findById(answerId);
                ua.setAnswer(answerOpt.get());
                userAnswersRepository.save(ua);
            }
        }
        // Cập nhật điểm tích lũy nếu điểm >= 8
//        result.setRatingPoint(ratingPointRepository.findById(ratingPoint.getId()));
//        resultRepository.save(result);
        return resultDTO;
    }
    @Override
    public List<Exams> getExamsByExamSetId(Integer examSetId) {
        return examsRepository.findExamsByExamSetId(examSetId);
    }
    @Override
    public List<ExamStatisticsDTO> getExamStatistics() {
        List<Object[]> results = examsRepository.getExamStatistics();
        return results.stream().map(result -> {
            Long examId = ((Number) result[0]).longValue();
            String examTitle = (String) result[1];
            Long totalParticipants = ((Number) result[2]).longValue();
            Long aboveEight = ((Number) result[3]).longValue();
            Double percentageAboveEight = totalParticipants > 0 ?
                    (aboveEight * 100.0 / totalParticipants) : 0.0;
            return new ExamStatisticsDTO(examId, examTitle, totalParticipants, percentageAboveEight);
        }).collect(Collectors.toList());
    }
}
