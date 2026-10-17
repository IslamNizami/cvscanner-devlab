package com.islamnizami.cvscannerdevlab.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "candidates")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Candidate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "email")
    private String email;

    @Column(name = "years_of_experience")
    private Integer yearsOfExperience;

    @Column(name = "technical_skills",columnDefinition = "TEXT")
    private String technicalSkills;

    @Column(name = "preferred_job_type")
    private String preferredJobType;

    @Column(name = "preferred_location")
    private String preferredLocation;

    @Column(name = "original_file_name",nullable = false)
    private String originalFileName;

    @CreationTimestamp
    @Column(name = "processed_at",updatable = false)
    private LocalDateTime processedAt;

}
