package com.example.project_module6.controller;

import com.example.project_module6.dto.*;
import com.example.project_module6.model.ExamSets;
import com.example.project_module6.model.Exams;
import com.example.project_module6.model.Questions;
import com.example.project_module6.repository.IExamSetRepository;
import com.example.project_module6.repository.IExamsRepository;
import com.example.project_module6.service.IExamSetService;
import com.example.project_module6.service.IExamsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@RestController
@CrossOrigin(value = "*")
@RequestMapping("/api/exam-set")
public class ExamSetController {
    @Autowired
    private IExamSetService examSetService;
    @Autowired
    private IExamSetRepository examSetRepository;
    @Autowired
    private IExamsRepository examsRepository;

    @GetMapping("")
    public ResponseEntity<Page<ExamSets>> getAllExamSet(
            @RequestParam(defaultValue = "", required = false)
            String name,
            @PageableDefault(size = 5)
            Pageable pageable){
        Page<ExamSets>examSets;
        if (name==null){
            examSets=examSetService.getAllExamSet(pageable);
        }else {
            examSets=examSetService.getAllExamSetByName(name,pageable);
        }
        if (!examSets.isEmpty()){
            return new ResponseEntity<>(examSets, HttpStatus.OK);
        }else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createExams(@RequestBody ExamSetDto examSetDto){
        try {
            ExamSets examSets = examSetService.createExamSet(examSetDto);
            return new ResponseEntity<>(examSets.getId(),HttpStatus.OK);
        }catch (IllegalArgumentException ex){
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
    @GetMapping("/findById/{id}")
    public ResponseEntity<?> findById(@PathVariable int id) {
        ExamSets examSets = examSetRepository.findById(id);
        return ResponseEntity.ok(examSets);
    }
    @GetMapping("/getAll-exams")
    public ResponseEntity<?> getAllExams() {
        List<Exams> questions = examsRepository.getAllExams();
        return ResponseEntity.ok(questions);
    }
    @PostMapping("/create/confirm/{examSetId}")
    public ResponseEntity<?> createExams(@PathVariable("examSetId") Integer examSetId,
                                         @RequestBody
                                         List<Integer> selectedExamId){
        try {
            examSetService.confirmExamsSetCreate(examSetId,selectedExamId);
            return new ResponseEntity<>("Xác nhận tạo đề thi và tạo câu hỏi thành công.", HttpStatus.OK);
        }catch (IllegalArgumentException ex){
            System.out.println("Bắt được lỗi: " + ex.getMessage());
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
    @PostMapping("/update/confirm/{examSetId}")
    public ResponseEntity<?> confirmExamsUpdateQuestion(@PathVariable("examSetId") Integer examSetId,
                                                        @RequestBody
                                                        List<Integer> selectedExamId){
        try {

            examSetService.confirmExamsSetUpdate(examSetId,selectedExamId);
            return new ResponseEntity<>("Xác nhận tạo đề thi và tạo câu hỏi thành công.", HttpStatus.OK);
        }catch (IllegalArgumentException ex){
            System.out.println("Bắt được lỗi: " + ex.getMessage());
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
    @GetMapping("/detail/{id}")
    public ResponseEntity<List<?>> detailExamSet(@PathVariable("id")int id){
        List<ExamSetDetailDto> examSetDetailDtoList = examSetService.detailExamSet(id);
        if (!examSetDetailDtoList.isEmpty()){
            return new ResponseEntity<>(examSetDetailDtoList,HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateExamsSet(@PathVariable("id")int id,@RequestBody ExamSetDto examsDto){
        try {
            ExamSets exams = examSetService.updateExamSet(id,examsDto);
            return new ResponseEntity<>(exams,HttpStatus.OK);
        }catch (IllegalArgumentException ex){
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @GetMapping("/findByIdForUpdate/{id}")
    public ResponseEntity<?> findByIdForUpdate(@PathVariable int id) {
        try {
            // Lấy thông tin bộ đề
            ExamSets examSets = examSetRepository.findById(id);

            if (examSets == null) {
                return ResponseEntity.badRequest().body("Không tìm thấy bộ đề!");
            }
            List<ExamSetDetailDto> examSetDetailDto = examSetService.detailExamSet(id);
            for (ExamSetDetailDto examSetDetailDto1 :examSetDetailDto){
                System.out.println("ExamLisst"+examSetDetailDto1);
            }
            Map<String, Object> response = new HashMap<>();

                response.put("examSets", examSets);
                response.put("examSetDetailDto", examSetDetailDto);


            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id")int id){
        examSetService.deleteExamSet(id);
        return ResponseEntity.ok("Xoá đề thi thành công!");
    }

    @DeleteMapping("/delete-exams-by-examSet/{idExamSet}/{idExams}")
    public ResponseEntity<?> deleteExamsByExamSetId(@PathVariable("idExamSet")int idExamSet,
                                                    @PathVariable("idExams")int idExams){
        examSetService.deleteExamByExamSetId(idExamSet,idExams);
        return ResponseEntity.ok("Xoá đề thi thành công!");
    }
}
