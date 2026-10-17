package com.islamnizami.cvscannerdevlab.batch;

import com.islamnizami.cvscannerdevlab.model.entity.Candidate;
import com.islamnizami.cvscannerdevlab.repository.CandidateRepository;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.data.RepositoryItemWriter;
import org.springframework.batch.infrastructure.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.File;
import java.io.IOException;

@Configuration
@SuppressWarnings({"deprecation", "removal"})
public class BatchConfig {

    @Bean
    @StepScope
    public ItemReader<File> cvItemReader(@Value("#{jobParameters['batchDir']}") String batchDir) {
        return new CvFileItemReader(batchDir);
    }

    @Bean
    public RepositoryItemWriter<Candidate> cvItemWriter(CandidateRepository repository) {
        return new RepositoryItemWriterBuilder<Candidate>()
                .repository(repository)
                .methodName("save")
                .build();
    }

    @Bean
    public Step processCvStep(JobRepository jobRepository,
                              PlatformTransactionManager transactionManager,
                              ItemReader<File> cvItemReader,
                              CvItemProcessor cvItemProcessor,
                              RepositoryItemWriter<Candidate> cvItemWriter) {

        return new StepBuilder("processCvStep", jobRepository)
                .<File, Candidate>chunk(20, transactionManager)
                .reader(cvItemReader)
                .processor(cvItemProcessor)
                .writer(cvItemWriter)
                .faultTolerant()
                .retryLimit(3)
                .retry(IOException.class)
                .skipLimit(100)
                .skip(Exception.class)
                .build();
    }

    @Bean
    public Job cvProcessingJob(JobRepository jobRepository,
                               Step processCvStep,
                               JobCompletionNotificationListener listener) {

        return new JobBuilder("cvProcessingJob", jobRepository)
                .listener(listener)
                .start(processCvStep)
                .build();
    }

}