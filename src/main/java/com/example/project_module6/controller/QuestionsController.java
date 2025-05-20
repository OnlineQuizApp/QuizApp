package com.example.project_module6.controller;

import com.example.project_module6.dto.QuestionDtoResponse;
import com.example.project_module6.dto.QuestionsDto;
import com.example.project_module6.model.Answers;
import com.example.project_module6.model.Categorys;
import com.example.project_module6.model.Questions;
import com.example.project_module6.service.ICloudinaryService;
import com.example.project_module6.service.IQuestionService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@CrossOrigin(value = "*")
@RequestMapping("/api/questions")
public class QuestionsController {
    @Autowired
    private IQuestionService questionService;
    @Autowired
    private ICloudinaryService cloudinaryService;

    @GetMapping("")
    public ResponseEntity<Page<?>> getAllQuestions(@RequestParam(defaultValue = "", required = false)
                                                   String content,
                                                   @RequestParam(defaultValue = "", required = false)
                                                   String category,
                                                   @PageableDefault(size = 5)
                                                   Pageable pageable) {

        Page<Questions> questions;
        if (content != null && !content.trim().isEmpty()) {
            questions = questionService.searchQuestionByQuestionContent(content, pageable);
            System.out.println("------search-content---------" + questions);
        } else if (category!=null &&!category.trim().isEmpty()){
            questions = questionService.searchQuestionByCategory(category,pageable);
            System.out.println("-------search-category--------" + questions);
        }else {
            questions = questionService.findAllQuestions(pageable);
            System.out.println("-------findAll--------" + questions);
        }
        if (questions.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(questions, HttpStatus.OK);
    }

    @PostMapping("/upload-file-excel")
    public ResponseEntity<?> uploadFileExcel(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return new ResponseEntity<>("File Không được để trống! ", HttpStatus.NOT_FOUND);
        }
        System.out.println("Tên file: " + file.getOriginalFilename());
        try {
            questionService.readAndWriteFile(file);
            return new ResponseEntity<>("Thêm File Excel Thành Công! ", HttpStatus.OK);
        } catch (RuntimeException e) {
            // Lỗi do file không có câu hỏi mới
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File này đã được thêm, không có câu hỏi mới nào.");
        } catch (Exception e) {
            return new ResponseEntity<>("Lỗi khi xử lý file Excel: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/upload-file-img", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadFileImg(@RequestParam ("file") MultipartFile file,
                                           @RequestParam ("categoryId") int categoryId,
                                           @RequestParam ("answers") String answers,
                                           @RequestParam ("content") String content
                                           ) {
        try {
            QuestionsDto questions = new QuestionsDto();
            String img = cloudinaryService.uploadImage(file);
            ObjectMapper mapper = new ObjectMapper();
            List<Answers> answersList = mapper.readValue(  // part từ JSON sang đối tượng
                    answers,
                    new TypeReference<List<Answers>>() {
                    }
            );
            questions.setImg(img);
            questions.setCategory(new Categorys(categoryId));
            questions.setAnswers(answersList);
            questions.setContent(content);
            questionService.createQuestions(questions);
            return new ResponseEntity<>("Thêm File hình ảnh Thành Công! ", HttpStatus.OK);
        } catch (Exception e) {
            System.out.println("answers raw JSON: " + answers);
            return new ResponseEntity<>("Lỗi khi xử lý hình ảnh: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping(value = "/upload-video", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadVideo( @RequestParam ("file") MultipartFile file,
                                           @RequestParam ("categoryId") int categoryId,
                                           @RequestParam ("answers") String answers,
                                           @RequestParam ("content") String content
                                          ) {
        try {
            QuestionsDto questions = new QuestionsDto();
            String video = cloudinaryService.uploadVideo(file);
            ObjectMapper mapper = new ObjectMapper();
            List<Answers> answersList = mapper.readValue(  // part từ JSON sang đối tượng
                    answers,
                    new TypeReference<List<Answers>>() {
                    }
            );
            questions.setVideo(video);
            questions.setCategory(new Categorys(categoryId));
            questions.setAnswers(answersList);
            questions.setContent(content);
            questionService.createQuestions(questions);
            return new ResponseEntity<>("Thêm Video Thành Công! ", HttpStatus.OK);
        } catch (Exception e) {
            System.out.println("answers raw JSON: " + answers);
            return new ResponseEntity<>("Lỗi khi xử lý hình ảnh: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("")
    public ResponseEntity<?> createQuestions(@Valid @RequestBody QuestionsDto questionsDto,BindingResult bindingResult ) {
        if (bindingResult.hasErrors()) {
            // Trả về danh sách lỗi
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        questionService.createQuestions(questionsDto);
        return new ResponseEntity<>("Thêm Mới Câu Hỏi  Thành Công!", HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateQuestions(@PathVariable("id") int id,
                                             @RequestParam (value = "file", required = false) MultipartFile file,
                                             @RequestParam ("categoryId") int categoryId,
                                             @RequestParam ("answers") String answers,
                                             @RequestParam ("content") String content) {

        Questions questions = questionService.findById(id);
        try {

            if (questions != null) {
                QuestionsDto questionsDto = new QuestionsDto();
                if (file != null && !file.isEmpty()) {
                    String imgUrl = cloudinaryService.uploadImage(file);
                    questionsDto.setImg(imgUrl);
                }
                ObjectMapper mapper = new ObjectMapper();
                List<Answers> answersList = mapper.readValue(  // part từ chuỗi JSON sang đối tượng
                        answers,
                        new TypeReference<List<Answers>>() {
                        }
                );
                questionsDto.setCategory(new Categorys(categoryId));
                questionsDto.setContent(content);
                questionsDto.setAnswers(answersList);
                questionService.updateQuestion(id, questionsDto);
                return new ResponseEntity<>("Chỉnh Sửa Thành Công!", HttpStatus.OK);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteQuestions(@PathVariable("id") int id) {
        Questions questions = questionService.findById(id);
        if (questions != null) {
            questionService.deleteQuestion(id);
            return new ResponseEntity<>("Xoá Thành Công!", HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detailQuestions(@PathVariable("id") int id) {
        QuestionDtoResponse questionsDto = questionService.findByIdDetail(id);
        if (questionsDto != null) {
            return new ResponseEntity<>(questionsDto, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}
