package com.GenAIsolutions.ProofofConcept.Config;

import com.GenAIsolutions.ProofofConcept.service.DataService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    @Autowired
    private DataService dataService;

    @Autowired
    @Qualifier("metadataDataSource")
    private DataSource metadataDataSource;

    @Autowired
    @Qualifier("batchTransactionManager")
    private PlatformTransactionManager batchTransactionManager;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public JobRepository customJobRepository() throws Exception {
        JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
        factory.setDataSource(metadataDataSource);
        factory.setTransactionManager(batchTransactionManager);
        factory.setIsolationLevelForCreate("ISOLATION_SERIALIZABLE");
        factory.setTablePrefix("BATCH_");
        factory.afterPropertiesSet();
        return factory.getObject();
    }

    @Bean
    public Job job(JobRepository jobRepository) {
        Step step = stepBuilderFactory.get("ETL-file-load")
                .tasklet((contribution, chunkContext) -> {
                    dataService.exportDataToJson();
                    return RepeatStatus.FINISHED;
                }).build();

        return jobBuilderFactory.get("ETL-Load")
                .incrementer(new RunIdIncrementer())
                .start(step)
                .repository(jobRepository)
                .build();
    }
}
