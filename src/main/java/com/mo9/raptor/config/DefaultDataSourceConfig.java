package com.mo9.raptor.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Map;

/**
 * @author wtwei .
 * @date 2018/9/14 .
 * @time 17:52 .
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef="primaryEntityManagerFactory",
        transactionManagerRef="entityManagerFactoryPrimary",
        basePackages= { "com.mo9.raptor.repository", "com.mo9.raptor.engine.repository", "com.mo9.risk.app.repo" }) //设置Repository所在位置
public class DefaultDataSourceConfig {

    @Resource
    @Qualifier("primaryDataSource")
    private DataSource primaryDataSource;


    @Bean(name = "primaryEntityManagerFactory")
    @Primary
    public LocalContainerEntityManagerFactoryBean primaryEntityManagerFactory(EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(primaryDataSource)
                .packages("com.mo9.raptor.entity", "com.mo9.raptor.engine.entity", "com.mo9.risk.app.entity")
                .persistenceUnit("primary")
                .build();
    }

    @Bean(name = "entityManagerFactoryPrimary")
    @Primary
    public PlatformTransactionManager barTransactionManager(
            @Qualifier("primaryEntityManagerFactory") EntityManagerFactory primaryEntityManagerFactory) {
        return new JpaTransactionManager(primaryEntityManagerFactory);
    }

}
