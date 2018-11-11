package com.mo9.raptor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author jyou
 */
@EnableAspectJAutoProxy
@SpringBootApplication(scanBasePackages = {"com.mo9.raptor.*","com.mo9.risk.*"}, exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
public class RaptorApplication {

	public static void main(String[] args) {
		SpringApplication.run(RaptorApplication.class, args);
	}
}
