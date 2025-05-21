    package com.example.project_module6.dto;

    import com.example.project_module6.model.Answers;
    import com.example.project_module6.model.Categorys;
    import com.example.project_module6.model.ExamQuestions;
    import com.example.project_module6.model.UserAnswers;
    import jakarta.validation.constraints.NotBlank;
    import jakarta.validation.constraints.NotNull;
    import jakarta.validation.constraints.Size;
    import lombok.*;

    import java.util.List;

    @AllArgsConstructor
    @NoArgsConstructor
    @Setter
    @Getter
    public class QuestionsDto {

        private int id;

        @NotBlank(message = "Nội dung câu hỏi không được để trống")
        @Size(max = 1000, message = "Nội dung câu hỏi không được vượt quá 1000 ký tự")
        private String content;

        @Size(max = 255, message = "Link hình ảnh không được vượt quá 255 ký tự")
        private String img;

        @Size(max = 255, message = "Link video không được vượt quá 255 ký tự")
        private String video;

        private List<AnswersDto> examAnswers;

        private List<Answers> answers;

        @NotNull(message = "Danh mục câu hỏi không được để trống")
        private Categorys category;

        private boolean softDelete = false;

        public QuestionsDto(String content) {
            this.content = content;
        }

        public QuestionsDto(String content, List<AnswersDto> examAnswers) {
            this.content = content;
            this.examAnswers = examAnswers;
        }

        public QuestionsDto(String content, List<AnswersDto> examAnswers, String img,String video) {
            this.content = content;
            this.examAnswers = examAnswers;
            this.img = img;
            this.video = video;
        }
    }
