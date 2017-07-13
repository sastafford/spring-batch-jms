package com.marklogic.spring.batch;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {JobRunnerContext.class, MockMessagingConfig.class, JmsJobConfig.class})
public class JmsJobTest {

    @Autowired
    protected JobLauncherTestUtils jobLauncherTestUtils;

    @Test
    public void executeJmsJobTest() throws Exception {
        JobParameters params = jobLauncherTestUtils.getUniqueJobParameters();
        JobExecution jobExec = jobLauncherTestUtils.launchJob(params);
        Assert.assertEquals(BatchStatus.COMPLETED, jobExec.getStatus());
    }

}
