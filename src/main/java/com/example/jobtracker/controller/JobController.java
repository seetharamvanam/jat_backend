package com.example.jobtracker.controller;

import com.example.jobtracker.dto.JobApplicationRequestDTO;
import com.example.jobtracker.dto.JobApplicationResponseDTO;
import com.example.jobtracker.service.JobService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jobs")
@AllArgsConstructor
public class JobController {
final JobService jobService;

        @GetMapping
        public ResponseEntity<?> getAllJobs(@RequestParam(required = false) String Status){
            return ResponseEntity.ok().body(jobService.getAllJobs(Status));
        }

        @PostMapping
        public ResponseEntity<?> createJob(@RequestBody JobApplicationRequestDTO requestDTO){
            try{
                JobApplicationResponseDTO response = jobService.createJob(requestDTO);
                return ResponseEntity.status(201).body(response);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }
}
