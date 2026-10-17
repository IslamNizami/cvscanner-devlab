package com.islamnizami.cvscannerdevlab.repository;

import com.islamnizami.cvscannerdevlab.model.entity.Candidate;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class CandidateSpecification {

    public static Specification<Candidate> filterCandidates(String skill, Integer minExperience, String jobType) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (skill != null && !skill.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("technicalSkills")),
                        "%" + skill.toLowerCase() + "%"));
            }

            if (minExperience != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("yearsOfExperience"), minExperience));
            }

            if (jobType != null && !jobType.trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(
                        criteriaBuilder.lower(root.get("preferredJobType")),
                        jobType.toLowerCase()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}