package com.example.project_module6.controller;


import com.example.project_module6.dto.*;

import com.example.project_module6.model.Exams;
import com.example.project_module6.model.Questions;
import com.example.project_module6.model.Users;
import com.example.project_module6.repository.IExamsRepository;
import com.example.project_module6.repository.IQuestionsRepository;
import com.example.project_module6.repository.IUserRepository;
import com.example.project_module6.service.IExamsQuestionsService;
import com.example.project_module6.service.IExamsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@CrossOrigin(value = "*")
@RequestMapping("/api/exams")
public class ExamsController {
    @Autowired
    private IExamsService examsService;
    @Autowired
    private IExamsRepository examsRepository;
    @Autowired
    private IExamsQuestionsService examsQuestionsService;
    @Autowired
    private IQuestionsRepository questionsRepository;
    @Autowired
    private IUserRepository userRepository;

    @GetMapping("")
    public ResponseEntity<Page<?>> getAllExams(@PageableDefault(size = 5) Pageable pageable,
                                               @RequestParam(defaultValue = "", required = false) String category,
                                               @RequestParam(defaultValue = "", required = false) String title) {
        Page<Exams> exams;
        if (category != null && !category.trim().isEmpty()) {
            exams = examsService.searchExamsByCategory(category, pageable);
        } else if (title != null && !title.trim().isEmpty()) {
            exams = examsService.searchExamsByTitle(title, pageable);
        } else {
            exams = examsService.getAlExams(pageable);
        }
        if (exams.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(exams, HttpStatus.OK);
    }

    @PostMapping("")
    public ResponseEntity<?> createExamsRandom(@RequestBody ExamsDto examsDto) {
        try {
            examsService.addExamsRandom(examsDto);
            return new ResponseEntity<>("Thêm mới  đề thi thành công! ", HttpStatus.OK);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateExamsRandom(@PathVariable("id") int id, @RequestBody ExamsDto examsDto) {
        try {
            Exams exams = examsService.updateExamsRandom(id, examsDto);
            return new ResponseEntity<>(exams, HttpStatus.OK);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteExams(@PathVariable("id") int id) {
        try {
            examsService.deleteExams(id);
            return new ResponseEntity<>("Xóa đề thi thành công! ", HttpStatus.OK);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Page<?>> detailExams(@PathVariable("id") int id,
                                               @PageableDefault(size = 1)
                                               Pageable pageable) {
        Page<ExamsQuestionsResponseDto> examsQuestionsResponseDto = examsQuestionsService.detailExamsQuestions(id, pageable);
        if (examsQuestionsResponseDto != null) {
            return new ResponseEntity<>(examsQuestionsResponseDto, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<List<?>> detailExams1(@PageableDefault(size = 1) @PathVariable("id") int id) {
        List<ExamsQuestionDataDto> examsQuestionsResponseDto = examsQuestionsService.getExamsQuestions(id);
        if (examsQuestionsResponseDto != null) {
            return new ResponseEntity<>(examsQuestionsResponseDto, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createExams(@RequestBody ExamsDto examsDto) {
        try {
            Exams exams = examsService.addExams(examsDto);
            return new ResponseEntity<>(exams.getId(), HttpStatus.OK);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PostMapping("/create-confirm/{examId}")
    public ResponseEntity<?> confirmExams(@PathVariable("examId") Integer examId,
                                          @RequestBody
                                          List<Integer> selectedQuestionsId) {
        try {
            System.out.println("danh sách câu hỏi: " + selectedQuestionsId);
            examsService.confirmExams(examId, selectedQuestionsId);
            return new ResponseEntity<>("Xác nhận tạo đề thi và tạo câu hỏi thành công.", HttpStatus.OK);
        } catch (IllegalArgumentException ex) {
            System.out.println("Bắt được lỗi: " + ex.getMessage());
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PostMapping("/update-confirm/{examId}")
    public ResponseEntity<?> confirmExamsUpdateQuestion(@PathVariable("examId") Integer examId,
                                                        @RequestBody
                                                        List<Integer> selectedQuestionsId) {
        try {
            System.out.println("danh sách câu hỏi: " + selectedQuestionsId);
            examsService.confirmExamsUpdate(examId, selectedQuestionsId);
            return new ResponseEntity<>("Xác nhận tạo đề thi và tạo câu hỏi thành công.", HttpStatus.OK);
        } catch (IllegalArgumentException ex) {
            System.out.println("Bắt được lỗi: " + ex.getMessage());
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @GetMapping("/findById/{id}")
    public ResponseEntity<?> findById(@PathVariable int id) {
        Exams questions = examsRepository.findById(id);
        return ResponseEntity.ok(questions);
    }

    @GetMapping("/questions-by-category/{category}")
    public ResponseEntity<?> getQuestionsByCategory(@PathVariable String category) {
        List<Questions> questions = questionsRepository.findQuestionsByCategory(category);
        return ResponseEntity.ok(questions);
    }

    @GetMapping("/findByIdForUpdate/{id}")
    public ResponseEntity<?> findByIdForUpdate(@PathVariable int id) {
        try {
            // Lấy thông tin đề thi
            Exams exam = examsRepository.findById(id);
            // Lấy danh sách câu hỏi và đáp án
            List<ExamsQuestionsResponseDto> questions = examsQuestionsService.detailExamsQuestionsUpdate(id);
            // Kết hợp dữ liệu
            Map<String, Object> response = new HashMap<>();
            if (exam != null && !questions.isEmpty()) {
                response.put("exam", exam);
                response.put("questions", questions);
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }


    @DeleteMapping("/delete-questions-of-exams/{idExams}/{idQuestions}")
    public ResponseEntity<?> deleteQuestionsOfExams(@PathVariable("idExams") int idExams,
                                                    @PathVariable("idQuestions") int idQuestions) {
        examsService.deleteQuestionOfExams(idExams, idQuestions);
        return ResponseEntity.ok("Xoá câu hỏi thành công!");
    }

    // Lấy danh sách đề thi
    @GetMapping("/getAll")
    public ResponseEntity<List<ExamsDto>> getAllExams() {
        return ResponseEntity.ok(examsService.getAllExams());
    }
    // Lấy chi tiết đề thi
    @GetMapping("/1/{id}")
    public ResponseEntity<ExamsDto> getExamById(@PathVariable int id) {
        return ResponseEntity.ok(examsService.getExamById(id));
    }

    // Lấy danh sách câu hỏi của đề thi
    @GetMapping("/1/{id}/questions")
    public ResponseEntity<List<QuestionDTO>> getExamQuestions(@PathVariable int id) {
        return ResponseEntity.ok(examsService.getExamQuestions(id));
    }

    // Nộp bài (chưa đăng nhập)
    @PostMapping("/submit")
    public ResponseEntity<ExamResultDTO> submitExam(@RequestBody SubmitExamRequest request) {
        return ResponseEntity.ok(examsService.submitExam(request));
    }

    // Nộp bài (đã đăng nhập)
    @PostMapping("/submit/authenticated")
    public ResponseEntity<ExamResultDTO> submitExamAuthenticated(@RequestBody SubmitExamRequest request,
                                                                 Authentication authentication) {
        String username = authentication.getName();
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        int userId = user.getId();
        return ResponseEntity.ok(examsService.submitExamAuthenticated(request, userId));
    }
    @GetMapping("/exam-set/{examSetId}")
    public ResponseEntity<List<Exams>> getExamsByExamSetId(@PathVariable Integer examSetId) {
        List<Exams> exams = examsService.getExamsByExamSetId(examSetId);
        return ResponseEntity.ok(exams);
    }
    @GetMapping("/statistics")
    public ResponseEntity<List<ExamStatisticsDTO>> getExamStatistics() {
        List<ExamStatisticsDTO> statistics = examsService.getExamStatistics();
        return ResponseEntity.ok(statistics);
    }
}
