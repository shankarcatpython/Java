package com.GenAIsolutions.ProofofConcept.Config;

import com.GenAIsolutions.ProofofConcept.Entity.Vehicle;
import com.GenAIsolutions.ProofofConcept.service.DataService;
import com.GenAIsolutions.ProofofConcept.service.VehicleService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.List;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    @Autowired
    private DataService dataService;

    @Autowired
    private VehicleService vehicleService;

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
        Step step1 = stepBuilderFactory.get("ETL-file-load")
                .tasklet((contribution, chunkContext) -> {
                    dataService.exportDataToJson();
                    return RepeatStatus.FINISHED;
                }).build();

        Step step2 = stepBuilderFactory.get("ETL-vehicle-export")
                .<Vehicle, Vehicle>chunk(10)
                .reader(vehicleItemReader())
                .writer(vehicleItemWriter())
                .transactionManager(batchTransactionManager)
                .build();

        return jobBuilderFactory.get("ETL-Load")
                .incrementer(new RunIdIncrementer())
                .start(step1)
                .next(step2)
                .repository(jobRepository)
                .build();
    }

    @Bean
    public ItemReader<Vehicle> vehicleItemReader() {
        return new ItemReader<Vehicle>() {
            private List<Vehicle> vehicleList = null;
            private int nextVehicleIndex;

            @Override
            public Vehicle read() throws Exception {
                if (vehicleList == null) {
                    vehicleList = vehicleService.findAll();
                    System.out.println("Loaded " + vehicleList.size() + " vehicles from database."); // Check if vehicles are loaded
                }

                Vehicle nextVehicle = null;

                if (nextVehicleIndex < vehicleList.size()) {
                    nextVehicle = vehicleList.get(nextVehicleIndex);
                    nextVehicleIndex++;
                } else {
                    System.out.println("No more vehicles to read."); // Check if reader reaches the end of the list
                }

                return nextVehicle;
            }
        };
    }
    @Bean
    public ItemWriter<Vehicle> vehicleItemWriter() {
        return new FlatFileItemWriterBuilder<Vehicle>()
                .name("vehicleItemWriter")
                .resource(new FileSystemResource("output/vehicles.csv"))
                .lineAggregator(new DelimitedLineAggregator<Vehicle>() {
                    {
                        setDelimiter(",");
                        setFieldExtractor(new BeanWrapperFieldExtractor<Vehicle>() {
                            {
                                setNames(new String[]{"id", "manufacturer", "model", "price"}); // Adjust as per your Vehicle fields
                            }
                        });
                    }
                })
                .build();
    }
}
