package com.islamnizami.cvscannerdevlab.service;

import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class DocumentParsingService {

    private final Tika tika;

    public String extractTextFromFile(File file) throws IOException{
        try{
            return tika.parseToString(file);
        }catch (Exception e){
            throw new IOException("Failed to parse document using Tika: ",e);
        }
    }



}
