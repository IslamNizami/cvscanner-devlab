
package com.islamnizami.cvscannerdevlab.controller;

import com.islamnizami.cvscannerdevlab.model.entity.Candidate;
import com.islamnizami.cvscannerdevlab.repository.CandidateRepository;
import com.islamnizami.cvscannerdevlab.repository.CandidateSpecification;
import com.islamnizami.cvscannerdevlab.service.ExportService;
import com.islamnizami.cvscannerdevlab.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/cv")
@RequiredArgsConstructor
public class CvController {

    private final FileStorageService fileStorageService;
    private final JobOperator jobOperator;
    private final Job cvProcessingJob;
    private final CandidateRepository candidateRepository;
    private final ExportService exportService;

    @PostMapping(value = "/upload",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> uploadAndProcessCvZip(
            @RequestParam("file") MultipartFile file) {

        Map<String, Object> response = new HashMap<>();

        try {
            String batchDir = fileStorageService.storeAndExtractZip(file);

            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("batchDir", batchDir)
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();

            JobExecution execution =
                    jobOperator.start(cvProcessingJob, jobParameters);

            response.put("message", "Job submitted successfully.");
            response.put("jobExecutionId", execution.getId());
            response.put("status", execution.getStatus().toString());

            return ResponseEntity.accepted().body(response);

        } catch (Exception e) {
            response.put("error",
                    "Failed to process batch job: " + e.getMessage());

            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/candidates")
    public ResponseEntity<List<Candidate>> getFilteredCandidates(
            @RequestParam(required = false) String skill,
            @RequestParam(required = false) Integer minExperience,
            @RequestParam(required = false) String jobType) {

        Specification<Candidate> spec = CandidateSpecification.filterCandidates(skill, minExperience, jobType);
        return ResponseEntity.ok(candidateRepository.findAll(spec));
    }

    @GetMapping(value = "/export/csv", produces = "text/csv")
    public ResponseEntity<byte[]> exportCandidatesCsv(
            @RequestParam(required = false) String skill,
            @RequestParam(required = false) Integer minExperience,
            @RequestParam(required = false) String jobType) {

        Specification<Candidate> spec = CandidateSpecification.filterCandidates(skill, minExperience, jobType);
        List<Candidate> candidates = candidateRepository.findAll(spec);

        byte[] csvData = exportService.exportCandidatesToCsv(candidates);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=filtered_candidates.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvData);
    }
}