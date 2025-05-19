package com.example.project_module6.repository;

import com.example.project_module6.model.Categorys;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICategoryRepository extends JpaRepository<Categorys,Integer> {
    Categorys findByName(String name);
}
