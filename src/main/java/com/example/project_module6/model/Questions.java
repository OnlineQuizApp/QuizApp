    package com.example.project_module6.model;


    import jakarta.persistence.*;
    import lombok.AllArgsConstructor;
    import lombok.Data;
    import lombok.NoArgsConstructor;



    @Entity
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public class Questions {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private int id;
        private String content;
        private String img;
        @Column(name = "soft_delete")
        private boolean softDelete=false;
        @ManyToOne
        @JoinColumn(name = "category_id", nullable = false)
        private Categorys category;

    }
