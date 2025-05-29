package com.example.project_module6.controller;

import com.example.project_module6.dto.ResultDTO;
import com.example.project_module6.dto.ResultDetailDTO;
import com.example.project_module6.service.IResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/results")
public class ResultController {
    @Autowired
    private IResultService resultService;

    @GetMapping("/user/username/{username}")
    public ResponseEntity<List<ResultDTO>> getResultsByUsername(@PathVariable String username) {
        List<ResultDTO> results = resultService.getResultsByUsername(username);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/{resultId}/details")
    public ResponseEntity<ResultDetailDTO> getResultDetails(@PathVariable Integer resultId) {
        ResultDetailDTO resultDetail = resultService.getResultDetails(resultId);
        return ResponseEntity.ok(resultDetail);
    }
}
