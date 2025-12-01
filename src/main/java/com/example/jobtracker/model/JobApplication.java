package com.example.jobtracker.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "job_application_details")
public class JobApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "company_name", nullable = false)
    private String companyName;
    @Column(name = "job_title", nullable = false)
    private String jobTitle;
    @Column(name = "job_url", nullable = false, columnDefinition = "TEXT")
    private String jobUrl;
    @Enumerated(EnumType.STRING)
    @Column(name="status")
    private ApplicationStatus status;
    @Column(name = "date_applied")
    private LocalDate dateApplied;
    public JobApplication(String companyName, String jobTitle, String jobURL, ApplicationStatus status, LocalDate dateApplied){
        this.companyName = companyName;
        this.jobTitle = jobTitle;
        this.jobUrl = jobURL;
        this.status =status;
        this.dateApplied = dateApplied;
    }
}
