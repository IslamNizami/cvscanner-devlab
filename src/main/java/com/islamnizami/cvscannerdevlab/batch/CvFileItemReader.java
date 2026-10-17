package com.islamnizami.cvscannerdevlab.batch;

import org.springframework.batch.infrastructure.item.ItemReader;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

public class CvFileItemReader implements ItemReader<File> {

    private Iterator<File> fileIterator;

    public CvFileItemReader(String directoryPath){
        File dir = new File(directoryPath);
        if(dir.exists() && dir.isDirectory()){
            File[] files = dir.listFiles((d,name)->
                    name.toLowerCase().endsWith(".pdf") || name.toLowerCase().endsWith(".docx"));
            if(files != null){
                this.fileIterator = Arrays.asList(files).iterator();
            }
        }
        if(this.fileIterator == null){
            this.fileIterator = Collections.emptyIterator();
        }
    }

    @Override
    public File read(){
        if(fileIterator != null && fileIterator.hasNext()){
            return fileIterator.next();
        }
        return null;
    }

}
