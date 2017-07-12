package com.marklogic.spring.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.*;
import org.springframework.batch.item.jms.JmsItemReader;
import org.springframework.batch.item.support.PassThroughItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import org.springframework.jms.core.JmsTemplate;

import java.util.List;

@EnableBatchProcessing
@Import(MessagingConfig.class)
public class JmsJobConfig {

    //Rename this private variable
    private final String JOB_NAME = "yourJob";

    /**
     * The JobBuilderFactory and Step parameters are injected via Spring
     * @param jobBuilderFactory injected from the @EnableBatchProcessing annotation
     * @param step injected from the step method in this class
     * @return Job bean
     */
    @Bean
    public Job job(JobBuilderFactory jobBuilderFactory, Step step) {
        return jobBuilderFactory.get(JOB_NAME).start(step).build();
    }


    @Bean
    @JobScope
    public Step step(
            StepBuilderFactory stepBuilderFactory,
            JmsTemplate jmsTemplate) {

        JmsItemReader<String> reader = new JmsItemReader<String>();
        reader.setJmsTemplate(jmsTemplate);
        reader.setItemType(String.class);

        ItemProcessor<String, String> processor = new PassThroughItemProcessor<String>();

        ItemWriter<String> writer = new ItemWriter<String>() {

            @Override
            public void write(List<? extends String> items) throws Exception {
                for (String item: items) {
                    System.out.println("SPRING BATCH: " + item);
                }
            }
        };
        return stepBuilderFactory.get("step1")
                .<String, String>chunk(10)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
}