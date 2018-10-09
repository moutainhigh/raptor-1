package com.mo9.raptor.config;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.Resource;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

/**
 * @author wtwei .
 * @date 2018/9/14 .
 * @time 17:52 .
 */
@EnableAspectJAutoProxy(proxyTargetClass = true)
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "riskdbEntityManagerFactory",
        transactionManagerRef = "entityManagerFactoryRiskdb",
        basePackages = {"com.mo9.raptor.riskdb.repo"}) //设置Repository所在位置
public class RiskDBDataSourceConfig {

    @Resource
    @Qualifier("riskdbDataSource")
    private DataSource riskdbDataSource;


    @Bean(name = "riskdbEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean riskdbEntityManagerFactory(EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(riskdbDataSource)
                .packages("com.mo9.raptor.riskdb.entity")
                .persistenceUnit("riskdb")
                .build();
    }


    @Bean(name = "entityManagerFactoryRiskdb")
    public PlatformTransactionManager barTransactionManager(
            @Qualifier("riskdbEntityManagerFactory") EntityManagerFactory riskEntityManagerFactory) {
        return new JpaTransactionManager(riskEntityManagerFactory);
    }

}

