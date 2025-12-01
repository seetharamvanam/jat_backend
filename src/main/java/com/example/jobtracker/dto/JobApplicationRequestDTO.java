package com.example.jobtracker.dto;

import com.example.jobtracker.model.ApplicationStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JobApplicationRequestDTO {
    private String companyName;
    private String jobTitle;
    private String jobUrl;
    private ApplicationStatus status;
    private LocalDate dateApplied;
}
