package com.GenAIsolutions.ProofofConcept.Config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DataSourceConfig {

    @Bean(name = "metadataDataSourceProperties")
    @ConfigurationProperties("spring.datasource.metadata")
    public DataSourceProperties metadataDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean(name = "metadataDataSource")
    public DataSource metadataDataSource(@Qualifier("metadataDataSourceProperties") DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder().build();
    }

    @Bean(name = "dataDataSourceProperties")
    @ConfigurationProperties("spring.datasource.data")
    public DataSourceProperties dataDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "dataDataSource")
    public DataSource dataDataSource(@Qualifier("dataDataSourceProperties") DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder().build();
    }

    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(@Qualifier("dataDataSource") DataSource dataDataSource) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataDataSource);
        em.setPackagesToScan("com.GenAIsolutions.ProofofConcept.entity");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        // Configure Hibernate properties for PostgreSQL
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect"); // Use PostgreSQL dialect
        properties.put("hibernate.hbm2ddl.auto", "update");
        em.setJpaPropertyMap(properties);

        return em;
    }

    @Bean(name = "customTransactionManager")
    public JpaTransactionManager customTransactionManager(@Qualifier("entityManagerFactory") LocalContainerEntityManagerFactoryBean entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory.getObject());
    }

    @Bean(name = "batchTransactionManager")
    public DataSourceTransactionManager batchTransactionManager(@Qualifier("metadataDataSource") DataSource metadataDataSource) {
        return new DataSourceTransactionManager(metadataDataSource);
    }
}
