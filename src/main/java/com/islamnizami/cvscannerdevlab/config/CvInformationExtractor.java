package com.islamnizami.cvscannerdevlab.config;

import com.islamnizami.cvscannerdevlab.model.entity.Candidate;
import org.apache.xmlbeans.impl.regex.Match;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class CvInformationExtractor {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}");

    private static final Pattern EXPERIENCE_PATTERN = Pattern.compile("(\\d+)\\+?\\s*(years|yrs)(?:\\s*of)?\\s*experience", Pattern.CASE_INSENSITIVE);

    private static final List<String> SKILLS_DICTIONARY = List.of(
            "Java", "Spring Boot", "Python", "React", "Angular", "SQL", "PostgreSQL","MySQL","MongoDB",
            "Docker", "Kubernetes", "AWS", "Azure", "JavaScript", "TypeScript", "C++", "C#","Redis","Grafana","Kafka","RabbitMQ","Api"
    );

    public Candidate extractCandidateInfo(String rawText, String fileName){
        Candidate candidate = Candidate.builder()
                .originalFileName(fileName)
                .build();

        if(rawText == null || rawText.trim().isEmpty()){
            return candidate;
        }

        Matcher emailMatcher = EMAIL_PATTERN.matcher(rawText);
        if(emailMatcher.find()){
            candidate.setEmail(emailMatcher.group());
        }

        String[] lines = rawText.split("\\r?\\n");
        for(String line: lines){
            String trimmed = line.trim();
            if(!trimmed.isEmpty() && trimmed.length() > 2 && trimmed.length() < 50 && !trimmed.contains("@")){
                candidate.setFullName(trimmed);
                break;
            }
        }

        Matcher experienceMatcher = EXPERIENCE_PATTERN.matcher(rawText);
        if(experienceMatcher.find()){
            try{
                candidate.setYearsOfExperience(Integer.parseInt(experienceMatcher.group(1)));
            }catch (NumberFormatException e){}
        }

        String lowerRawText = rawText.toLowerCase();
        String foundSkills = SKILLS_DICTIONARY.stream()
                .filter(skill -> lowerRawText.contains(skill.toLowerCase()))
                .collect(Collectors.joining(", "));
        candidate.setTechnicalSkills(foundSkills);

        if(lowerRawText.contains("remote")){
            candidate.setPreferredJobType("Remote");
        }else if(lowerRawText.contains("hybrid")){
            candidate.setPreferredJobType("Hybrid");
        }else{
            candidate.setPreferredJobType("On-site");
        }

        if(lowerRawText.contains("relocate")){
            candidate.setPreferredLocation("Open to Relocation");
        }

        return candidate;
    }
}
