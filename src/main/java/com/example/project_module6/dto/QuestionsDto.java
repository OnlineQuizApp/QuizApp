    package com.example.project_module6.dto;

    import com.example.project_module6.model.Answers;
    import com.example.project_module6.model.Categorys;
    import com.example.project_module6.model.ExamQuestions;
    import com.example.project_module6.model.UserAnswers;
    import lombok.AllArgsConstructor;
    import lombok.Data;
    import lombok.NoArgsConstructor;

    import java.util.List;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public class QuestionsDto {
        private int id;
        private String content;
        private String img;
        private List<AnswersDto>examAnswers;
        private List<Answers> answers;
        private Categorys category;
        private boolean softDelete=false;

        public QuestionsDto(String content) {
            this.content = content;
        }

        public QuestionsDto(String content, List<AnswersDto> examAnswers) {
            this.content = content;
            this.examAnswers = examAnswers;
        }
    }
