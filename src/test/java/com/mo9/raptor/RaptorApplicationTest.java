package com.mo9.raptor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy
@SpringBootApplication(scanBasePackages = {"com.mo9.raptor.*"})
@EnableAutoConfiguration
public class RaptorApplicationTest {
	public static void main(String[] args) {
		SpringApplication.run(RaptorApplicationTest.class, args);

	}
}
