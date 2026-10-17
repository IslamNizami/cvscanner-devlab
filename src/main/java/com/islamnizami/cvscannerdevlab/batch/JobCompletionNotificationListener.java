package com.islamnizami.cvscannerdevlab.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class JobCompletionNotificationListener implements JobExecutionListener {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public void afterJob(JobExecution jobExecution) {
        if(jobExecution.getStatus() == BatchStatus.COMPLETED){
            log.info("!!! Batch JOB Completed Successfully !!!");
            log.info("Batch ID: {}", jobExecution.getJobParameters().getString("batchDir"));
            sendEmailNotification(jobExecution,"Success");
        }else if(jobExecution.getStatus() == BatchStatus.FAILED){
            log.error("BATCH JOB FAILED with exceptions: {}", jobExecution.getAllFailureExceptions());
        }
    }

    public void sendEmailNotification(JobExecution jobExecution, String status){
        try{
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo("islamronaldo046@gmail.com");
            message.setSubject("CV Scanner Batch Job Status: " + status);
            message.setText("The CV processing batch job (ID: " + jobExecution.getId() + ") finished with status: " + status + ".\n" +
                    "Read Count: " + jobExecution.getStepExecutions().iterator().next().getReadCount() + "\n" +
                    "Write Count: " + jobExecution.getStepExecutions().iterator().next().getWriteCount() + "\n" +
                    "Skip Count: " + jobExecution.getStepExecutions().iterator().next().getSkipCount());

            mailSender.send(message);
        }catch (Exception e){
            log.error("Failed to send email notification",e);
        }
    }
}
