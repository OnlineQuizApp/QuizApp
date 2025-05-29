package com.example.project_module6.service;

import com.example.project_module6.dto.ResultDTO;
import com.example.project_module6.dto.ResultDetailDTO;
import com.example.project_module6.dto.UserFilterRequest;
import com.example.project_module6.model.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IResultService {
    List<ResultDTO> getResultsByUsername(String username);
    ResultDetailDTO getResultDetails(Integer resultId);

}
