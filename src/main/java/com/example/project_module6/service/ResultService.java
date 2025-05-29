package com.example.project_module6.service;

import com.example.project_module6.dto.QuestionDetailDTO;
import com.example.project_module6.dto.ResultDTO;
import com.example.project_module6.dto.ResultDetailDTO;
import com.example.project_module6.repository.IResultRepository;
import com.example.project_module6.repository.IUserAnswersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ResultService implements IResultService{
    @Autowired
    private IResultRepository resultRepository;

    @Autowired
    private IUserAnswersRepository userAnswerRepository;
    @Override
    public List<ResultDTO> getResultsByUsername(String username) {
        List<Object[]> results = resultRepository.findResultsByUsername(username);
        List<ResultDTO> resultDTOs = new ArrayList<>();
        for (Object[] result : results) {
            ResultDTO dto = new ResultDTO();
            dto.setId((Integer) result[0]);
            dto.setExamId((Integer) result[1]);
            dto.setExamTitle((String) result[2]);
            dto.setNumberOfQuestions((Integer) result[3]);
            dto.setTotalScore((Double) result[4]);
            dto.setSubmittedAt(result[5].toString());
            resultDTOs.add(dto);
        }
        return resultDTOs;
    }
    @Override
    public ResultDetailDTO getResultDetails(Integer resultId) {
        ResultDetailDTO dto = new ResultDetailDTO();

        // Fetch result and exam info
        List<Object[]> resultInfo = resultRepository.findResultById(resultId);
        if (resultInfo.isEmpty()) {
            throw new RuntimeException("Result not found");
        }
        Object[] resultData = resultInfo.get(0);
        dto.setResultId((Integer) resultData[0]);
        dto.setExamTitle((String) resultData[2]);
        dto.setNumberOfQuestions((Integer) resultData[3]);
        dto.setTotalScore((Double) resultData[4]);

        // Fetch question details
        List<Object[]> userAnswers = userAnswerRepository.findUserAnswersByResultId(resultId);
        List<QuestionDetailDTO> questionDTOs = new ArrayList<>();
        int correctAnswers = 0;
        for (Object[] ua : userAnswers) {
            QuestionDetailDTO qDto = new QuestionDetailDTO();
            qDto.setQuestionId((Integer) ua[0]);
            qDto.setContent((String) ua[1]);
            qDto.setUserAnswer(ua[2] != null ? (String) ua[3] : "Không chọn");
            qDto.setCorrectAnswer((String) ua[4]);
            if (ua[2] != null && ua[3].equals(ua[4])) {
                correctAnswers++;
            }
            questionDTOs.add(qDto);
        }
        dto.setQuestions(questionDTOs);
        dto.setCorrectAnswers(correctAnswers);
        return dto;
    }
}
