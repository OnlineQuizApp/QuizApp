package com.example.project_module6.service;

import com.example.project_module6.model.Categorys;
import com.example.project_module6.repository.ICategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService implements ICategoryService{
    @Autowired
    private ICategoryRepository categoryRepository;

    @Override
    public List<Categorys> getAllCategory() {
       return categoryRepository.findAll();
    }
}
