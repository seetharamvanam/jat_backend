package com.example.jobtracker.service;

import com.example.jobtracker.dto.JobApplicationRequestDTO;
import com.example.jobtracker.dto.JobApplicationResponseDTO;
import com.example.jobtracker.model.ApplicationStatus;
import com.example.jobtracker.model.JobApplication;
import com.example.jobtracker.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JobService {
    final JobRepository repository;
    final JobScraperService scraperService;

    public JobApplicationResponseDTO createJob(JobApplicationRequestDTO requestDTO){
        if (requestDTO.getJobUrl() != null && !requestDTO.getJobUrl().isEmpty()) {
            JobApplicationRequestDTO scrapedData = scraperService.scrapeDetails(requestDTO.getJobUrl());
            if (requestDTO.getCompanyName() == null || requestDTO.getCompanyName().isEmpty()) {
                requestDTO.setCompanyName(scrapedData.getCompanyName());
            }
            if (requestDTO.getJobTitle() == null || requestDTO.getJobTitle().isEmpty()) {
                requestDTO.setJobTitle(scrapedData.getJobTitle());
            }
        }
          validateRequestDTO(requestDTO);
          JobApplication savedJob = repository.save(new JobApplication(
                   requestDTO.getCompanyName(),
                   requestDTO.getJobTitle(),
                   requestDTO.getJobUrl(),
                   requestDTO.getStatus(),
                   requestDTO.getDateApplied()));
          return mapToDTO(savedJob);
    }

    public void validateRequestDTO(JobApplicationRequestDTO requestDTO){
        if(requestDTO.getCompanyName() == null ||requestDTO.getCompanyName().isEmpty()){
            throw new IllegalArgumentException("Error : Company Name is required");
        }
        if(requestDTO.getJobTitle() == null ||requestDTO.getJobTitle().isEmpty()){
            throw new IllegalArgumentException("Error : Job Title is required");
        }
        if(requestDTO.getJobUrl() == null || requestDTO.getJobUrl().isEmpty()){
            throw new IllegalArgumentException("Error : Job Url is required");
        }
        if (requestDTO.getStatus() == null){
            throw new IllegalArgumentException("Error : Status is required");
        }
    }

    public List<JobApplicationResponseDTO> getAllJobs(String Status){
        List<JobApplicationResponseDTO> returnList = new ArrayList<>();
        if(Status == null || Status.isEmpty()){
           return repository.findAll().stream().map(this::mapToDTO).toList();
       }else{
            try{
                ApplicationStatus statusEnum = ApplicationStatus.valueOf(Status.toUpperCase());
                return repository.findByStatus(statusEnum)
                        .stream().map(this::mapToDTO).toList();
            }catch (IllegalArgumentException e){
                return new ArrayList<>();
            }
        }
    }

    private JobApplicationResponseDTO mapToDTO(JobApplication job){
        return new JobApplicationResponseDTO(job.getId(),
                job.getCompanyName(),
                job.getJobTitle(),
                job.getJobUrl(),
                job.getStatus(),
                job.getDateApplied());
    }

}
