package com.example.project_module6.controller;


import com.example.project_module6.dto.ExamsDto;
import com.example.project_module6.dto.ExamsQuestionDataDto;

import com.example.project_module6.dto.ExamsQuestionsResponseDto;
import com.example.project_module6.model.Exams;
import com.example.project_module6.service.IExamsQuestionsService;
import com.example.project_module6.service.IExamsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(value = "*")
@RequestMapping("/api/exams")
public class ExamsController {
    @Autowired
    private IExamsService examsService;
    @Autowired
    private IExamsQuestionsService examsQuestionsService;
    @GetMapping("")
    public ResponseEntity<Page<?>> getAllExams(@PageableDefault(size = 5)Pageable pageable,
                                               @RequestParam(defaultValue = "",required = false) String category){
       Page<Exams> exams;
       if (category!=null&&!category.trim().isEmpty()){
           exams=examsService.searchExamsByCategory(category,pageable);
       }else {
           exams = examsService.getAlExams(pageable);
       }
       if (exams.isEmpty()){
           return new ResponseEntity<>(HttpStatus.NOT_FOUND);
       }
        return new ResponseEntity<>(exams, HttpStatus.OK);
    }
    @PostMapping("")
    public ResponseEntity<?> createExamsRandom(@RequestBody ExamsDto examsDto){
        try {
            examsService.addExamsRandom(examsDto);
            return new ResponseEntity<>("Thêm mới  đề thi thành công! ",HttpStatus.OK);
        }catch (IllegalArgumentException ex){
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateExams(@PathVariable("id")int id,@RequestBody ExamsDto examsDto){
        try {
           examsService.updateExams(id,examsDto);
            return new ResponseEntity<>("Chỉnh sửa đề thi thành công! ",HttpStatus.OK);
        }catch (IllegalArgumentException ex){
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteExams(@PathVariable("id")int id){
        try {
           examsService.deleteExams(id);
            return new ResponseEntity<>("Xóa đề thi thành công! ",HttpStatus.OK);
        }catch (IllegalArgumentException ex){
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<Page<?>> detailExams(@PathVariable("id")int id,
                                               @PageableDefault(size = 1)
                                               Pageable pageable){
        Page<ExamsQuestionsResponseDto> examsQuestionsResponseDto = examsQuestionsService.detailExamsQuestions(id,pageable);
        if (examsQuestionsResponseDto!=null){
            return new ResponseEntity<>(examsQuestionsResponseDto,HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<List<?>> detailExams1(@PageableDefault(size = 1) @PathVariable("id")int id){
        List<ExamsQuestionDataDto> examsQuestionsResponseDto = examsQuestionsService.getExamsQuestions(id);
        if (examsQuestionsResponseDto!=null){
            return new ResponseEntity<>(examsQuestionsResponseDto,HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
