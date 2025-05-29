package com.example.project_module6.service;

import com.example.project_module6.dto.*;

import com.example.project_module6.model.*;
import com.example.project_module6.repository.IExamSetExamsRepository;
import com.example.project_module6.repository.IExamSetRepository;
import com.example.project_module6.repository.IExamsRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class ExamSetService implements IExamSetService {
    @Autowired
    private IExamSetRepository examSetRepository;
    @Autowired
    private IExamsRepository examsRepository;
    @Autowired
    private IExamSetExamsRepository examSetExamsRepository;


    @Override
    public Page<ExamSets> getAllExamSet(Pageable pageable) {
        return examSetRepository.findAllExamSet(pageable);
    }

    @Override
    public Page<ExamSets> getAllExamSetByName(String name, Pageable pageable) {
        return examSetRepository.findAllExamSetByName("%" + name + "%", pageable);
    }

    @Override
    @Transactional
    public ExamSets createExamSet(ExamSetDto examSetDto) {
        List<ExamSets> examSetsList = examSetRepository.getAllExamSet();
        for (ExamSets examSets : examSetsList) {
            if (examSets.getName().equals(examSetDto.getName())) {
                throw new IllegalArgumentException("Bộ đề thi này đã có trong hệ thống");
            }
        }
        ExamSets examSets = new ExamSets();
        BeanUtils.copyProperties(examSetDto, examSets);
        ExamSets saveExamSet = examSetRepository.saveAndFlush(examSets);
        return saveExamSet;
    }

    @Override
    @Transactional
    public void confirmExamsSetCreate(Integer examSetId, List<Integer> examsId) {
        ExamSets examSets = examSetRepository.findById(examSetId).
                orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bộ đề với ID: " + examSetId));
        if (examSets != null) {
            List<Exams> exams = examsRepository.findAll();
            if (!exams.isEmpty() && !examsId.isEmpty()) {
                List<Exams> examsList = exams.stream()
                        .filter(q -> examsId.contains(q.getId()))//lọc ra những id đề nào trùng với danh sách id đề thêm vào
                        .collect(Collectors.toList());
                examSetExamsRepository.deleteByExamSetId(examSetId);
                for (Exams exams1 : examsList) {
                    ExamSetExam examSets1 = new ExamSetExam();
                    examSets1.setExam(exams1);
                    examSets1.setExamSet(examSets);
                    exams1.setExitsExamSetExam(true);
                    examsRepository.save(exams1);
                    examSetExamsRepository.save(examSets1);
                }
            }
        }
    }

    @Override
    public void confirmExamsSetUpdate(Integer examSetId, List<Integer> examsId) {
        ExamSets examSets = examSetRepository.findById(examSetId).orElse(null);
        if (examSets != null) {
            List<Exams> examsList = examsRepository.findAll();
            if (!examsList.isEmpty() && !examsId.isEmpty()) {
                List<ExamSetExam> examSetExamByExamId = examSetExamsRepository.findExamSetExamByExam_Id(examSetId); // lấy danh sách đề thi theo id đề
                Set<Integer> existingExamId = examSetExamByExamId.stream()// lưu id vào set
                        .map(es -> es.getExam().getId())
                        .collect(Collectors.toSet());
                List<Exams> selectExam = examsList.stream()
                        .filter(q -> examsId.contains(q.getId()) && !existingExamId.contains(q.getId()))//lọc ra những id câu hỏi nào trùng với danh sách id câu hỏi thêm vào đề
                        .collect(Collectors.toList());
                for (Exams exams : selectExam) {
                    ExamSetExam examSets1 = new ExamSetExam();
                    examSets1.setExam(exams);
                    examSets1.setExamSet(examSets);
                    exams.setExitsExamSetExam(true);
                    examsRepository.save(exams);
                    examSetExamsRepository.saveAndFlush(examSets1);
                }
            }
        }
    }

    @Override
    public ExamSets updateExamSet(int id, ExamSetDto examSetDto) {
        ExamSets examSets = examSetRepository.findById(id);
        if (examSets != null) {
            List<ExamSets> examSetsList = examSetRepository.getAllExamSet();
            for (ExamSets examSets1 : examSetsList) {
                if (examSetDto.getName().equals(examSets1.getName())&&examSetDto.getId()!=examSets1.getId()) {
                    throw new IllegalArgumentException("Bộ đề này đã có trong hệ thống");
                }
            }
            examSets.setName(examSetDto.getName());
            examSets.setCreationDate(examSetDto.getCreationDate());
            ExamSetExam examSetExam = new ExamSetExam();
            examSetExam.setExamSet(examSets);
            examSetRepository.save(examSets);
        }

        return examSets;
    }

    @Override
    public void deleteExamSet(int id) {
        ExamSets exams = examSetRepository.findById(id);
        if (exams!=null){
            exams.setSoftDelete(true);
            examSetRepository.save(exams);
            List<ExamSetExam> examSetsList= examSetExamsRepository.findByExamSet_Id(id);
            for (ExamSetExam examSetExam:examSetsList){
                Exams exams1 =examSetExam.getExam();
                int countExams = examSetExamsRepository.countByExam_Id(exams1.getId());
                if (countExams==0){
                    exams1.setExitsExamSetExam(false);
                    examsRepository.save(exams1);
                }

            }

        }
    }

    @Override
    @Modifying
    @Transactional
    public boolean deleteExamByExamSetId(int examSetId, int examId) {
        ExamSets exams = examSetRepository.findById(examSetId);
        if (exams!=null){
            Exams exams1= examsRepository.findById(examId);
            examSetExamsRepository.deleteExamByExamSet(examSetId,examId);
            int countExams = examSetExamsRepository.countByExam_Id(examId);
            if (countExams==0){
                if (exams1!=null){
                    exams1.setExitsExamSetExam(false);
                    examsRepository.save(exams1);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public List<ExamSetDetailDto> detailExamSet(int id) {
        List<ExamSetDetailDataDto> examSetDetailDataDto = examSetRepository.detailExamSet(id);
        if (!examSetDetailDataDto.isEmpty()){
            List<ExamSetDetailDto> result = new ArrayList<>();
            for (ExamSetDetailDataDto examSetDetailDataDto1: examSetDetailDataDto){
                ExamSetDetailDto examSetDetailDto = new ExamSetDetailDto();
                examSetDetailDto.setId(examSetDetailDataDto1.getExamSetId());
                examSetDetailDto.setName(examSetDetailDataDto1.getExamSetName());
                examSetDetailDto.setImg(examSetDetailDataDto1.getExamSetImg());
                examSetDetailDto.setCreationDate(examSetDetailDataDto1.getCreationDate());
                List<ExamsDto> exams = new ArrayList<>();
                String examRaw = examSetDetailDataDto1.getExams();
                if (examRaw!=null&&!examRaw.isEmpty()){
                    String[] exam = examRaw.split(",");
                    for (String e :exam){
                        if (e.contains("-")){
                            String[] parts = e.split("-");
                            exams.add(new ExamsDto(Integer.parseInt(parts[0]), parts[1],parts[2], Integer.parseInt(parts[3]), parts[4]));
                        }
                    }
                }
                examSetDetailDto.setExams(exams);
                result.add(examSetDetailDto);
            }
            return result;
        }
        return null;
    }
}
