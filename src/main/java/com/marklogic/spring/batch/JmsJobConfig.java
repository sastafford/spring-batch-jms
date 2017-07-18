package com.marklogic.spring.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.*;
import org.springframework.batch.item.jms.JmsItemReader;
import org.springframework.context.annotation.Bean;

import org.springframework.jms.core.JmsTemplate;

import javax.jms.Message;
import javax.jms.TextMessage;
import java.util.List;

@EnableBatchProcessing
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

        JmsItemReader<Message> reader = new JmsItemReader<Message>();
        reader.setJmsTemplate(jmsTemplate);
        reader.setItemType(Message.class);

        ItemProcessor<Message, String> processor = new ItemProcessor<Message, String>() {
            @Override
            public String process(Message item) throws Exception {
                String text = null;
                if (item instanceof TextMessage) {
                    TextMessage textMessage = (TextMessage) item;
                    text = textMessage.getText();
                    System.out.println("Received: " + text);
                } else {
                    System.out.println("Received: " + item);
                }
                return text;
            }
        };

        ItemWriter<String> writer = new ItemWriter<String>() {

            @Override
            public void write(List<? extends String> items) throws Exception {
                for (String item: items) {
                    System.out.println("SPRING BATCH: " + item);
                }
            }
        };
        return stepBuilderFactory.get("step1")
                .<Message, String>chunk(10)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .readerIsTransactionalQueue()
                .build();
    }
}
