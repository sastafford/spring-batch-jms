package com.marklogic.spring.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Date;

public class App {

    public int run() throws Exception {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(JmsJobConfig.class);
        ctx.refresh();
        JobLauncher launcher = ctx.getBean(JobLauncher.class);
        Job job = ctx.getBean(Job.class);
        JobParametersBuilder jpb = new JobParametersBuilder();
        jpb.addString("output_collections", "getting-started");
        jpb.addString("id", new Date().toString());

        JobExecution jobExec = launcher.run(job, jpb.toJobParameters());
        return jobExec.getStatus().ordinal();


    }

    public static void main(String[] args) throws Exception {
        System.exit(new App().run());
    }
}
