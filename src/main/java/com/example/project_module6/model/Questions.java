    package com.example.project_module6.model;


    import jakarta.persistence.*;
    import lombok.*;


    @Entity
    @AllArgsConstructor
    @NoArgsConstructor
   @Getter
    @Setter
    public class Questions {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private int id;
        private String content;
        private String img;
        private String video;
        @Column(name = "soft_delete")
        private boolean softDelete=false;
        @ManyToOne
        @JoinColumn(name = "category_id", nullable = false)
        private Categorys category;

    }
