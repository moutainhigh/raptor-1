package com.mo9.raptor.config;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
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
@EnableAspectJAutoProxy(proxyTargetClass = true)
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef="riskEntityManagerFactory",
        transactionManagerRef="entityManagerFactoryRisk",
        basePackages= { "com.mo9.raptor.risk.repo" }) //设置Repository所在位置
public class RiskDataSourceConfig {

    @Resource
    @Qualifier("secondaryDataSource")
    private DataSource secondaryDataSource;


    @Bean(name = "riskEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean riskEntityManagerFactory (EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(secondaryDataSource)
                .packages("com.mo9.raptor.risk.entity")
                .persistenceUnit("risk")
                .build();
    }


    @Bean(name = "entityManagerFactoryRisk")
    public PlatformTransactionManager barTransactionManager(
            @Qualifier("riskEntityManagerFactory") EntityManagerFactory riskEntityManagerFactory) {
        return new JpaTransactionManager(riskEntityManagerFactory);
    }

}
