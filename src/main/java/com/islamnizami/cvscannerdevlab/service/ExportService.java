package com.islamnizami.cvscannerdevlab.service;

import com.islamnizami.cvscannerdevlab.model.entity.Candidate;
import com.opencsv.CSVWriter;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.util.List;

@Service
public class ExportService {

    public byte[] exportCandidatesToCsv(List<Candidate> candidates) {
        try (StringWriter sw = new StringWriter();
             CSVWriter csvWriter = new CSVWriter(sw)) {

            String[] header = {
                    "ID", "Full Name", "Email", "Years of Experience",
                    "Technical Skills", "Preferred Job Type", "Location"
            };
            csvWriter.writeNext(header);

            for (Candidate c : candidates) {
                String[] data = {
                        String.valueOf(c.getId()),
                        c.getFullName() != null ? c.getFullName() : "",
                        c.getEmail() != null ? c.getEmail() : "",
                        c.getYearsOfExperience() != null ? String.valueOf(c.getYearsOfExperience()) : "0",
                        c.getTechnicalSkills() != null ? c.getTechnicalSkills() : "",
                        c.getPreferredJobType() != null ? c.getPreferredJobType() : "",
                        c.getPreferredLocation() != null ? c.getPreferredLocation() : ""
                };
                csvWriter.writeNext(data);
            }
            return sw.toString().getBytes();

        } catch (Exception e) {
            throw new RuntimeException("Error while generating CSV file", e);
        }
    }
}