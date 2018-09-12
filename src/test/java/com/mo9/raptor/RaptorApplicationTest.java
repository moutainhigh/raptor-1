package com.mo9.raptor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy
@SpringBootApplication(scanBasePackages = {"com.mo9.raptor.*"} , exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
@EnableAutoConfiguration
public class RaptorApplicationTest {
	public static void main(String[] args) {
		SpringApplication.run(RaptorApplicationTest.class, args);

//		ConditionValue value = new ConditionValue(Mode.SCOPE, Scope.LOAN_CURRENCY.name());
//
//		Condition condition = new Condition(Scope.PAY_CURRENCY, value, ConditionOperator.NOT_EQUAL);

//		ConditionValue compositeValue = new ConditionValue(Mode.VALUE, CurrencyEnum.LBA.name());
//		Condition compositeCondition = new Condition(Scope.PAY_CURRENCY, compositeValue, ConditionOperator.EQUAL);
//
		//condition = condition.or(compositeCondition);
//
//		String conditionJson = JSON.toJSONString(compositeCondition);
//
//		compositeCondition = JSON.parseObject(conditionJson, Condition.class);
//
//		Situation situation = new Situation();
//		situation.put(Scope.PAY_CURRENCY, new ConditionValue(Mode.VALUE, CurrencyEnum.LBA.name()));
//		situation.put(Scope.LOAN_CURRENCY, new ConditionValue(Mode.VALUE, CurrencyEnum.BNB.name()));
//
//		try {
//			boolean re = compositeCondition.verify(situation);
//			System.out.println(re);
//		} catch (InvalidConditionException e) {
//			e.printStackTrace();
//		}
	}
}
