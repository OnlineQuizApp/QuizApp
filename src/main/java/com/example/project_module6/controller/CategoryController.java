package com.example.project_module6.controller;

import com.example.project_module6.model.Categorys;
import com.example.project_module6.service.ICategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
@CrossOrigin(value = "*")
public class CategoryController {
    @Autowired
    private ICategoryService categoryService;

    @GetMapping("")
    public ResponseEntity<List<Categorys>> getAllCategory() {
        List<Categorys> categorysList = categoryService.getAllCategory();
        if (!categorysList.isEmpty()) {
            return new ResponseEntity<>(categorysList, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    @PostMapping("")
    public ResponseEntity<?> createCategory(@Valid@RequestBody Categorys categorys, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            // Trả về danh sách lỗi
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
       categoryService.createCategory(categorys);
        return new ResponseEntity<>("Thêm danh mục câu hỏi thành công!", HttpStatus.OK);
    }

}
