package com.islamnizami.cvscannerdevlab.batch;

import com.islamnizami.cvscannerdevlab.config.CvInformationExtractor;
import com.islamnizami.cvscannerdevlab.model.entity.Candidate;
import com.islamnizami.cvscannerdevlab.service.DocumentParsingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@RequiredArgsConstructor
@Slf4j
public class CvItemProcessor implements ItemProcessor<File, Candidate> {

    private final DocumentParsingService documentParsingService;
    private final CvInformationExtractor cvInformationExtractor;

    @Override
    public @Nullable Candidate process(File file) throws Exception {
        log.info("Processing CV file: {}", file.getName());

        String rawText = documentParsingService.extractTextFromFile(file);

        return cvInformationExtractor.extractCandidateInfo(rawText, file.getName());
    }

}
