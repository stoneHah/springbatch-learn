package com.zq.learn.springbatchlearn;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringbatchLearnApplicationTests {

	@Autowired
	private JobLauncher jobLauncher;

	@Autowired
	@Qualifier("importUserJob")
	private Job job;

	@Test
	public void contextLoads() throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
		jobLauncher.run(job, newExecution());
	}

	@Test
	public void runJob(){

	}

	private JobParameters newExecution() {
		Map<String, JobParameter> parameters = new HashMap<>();

		JobParameter parameter = new JobParameter(new Date());
		parameters.put("currentTime", parameter);

		return new JobParameters(parameters);
	}

}
