package com.board.aop;

import java.util.Collections;
import java.util.List;

import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.interceptor.MatchAlwaysTransactionAttributeSource;
import org.springframework.transaction.interceptor.RollbackRuleAttribute;
import org.springframework.transaction.interceptor.RuleBasedTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionInterceptor;

// @Configuration : Configuration을 클래스에 적용하고 @Bean을 해당 클래스의 메소드에 적용하면 @Autowired로 빈을 부를 수 있음
@Configuration
public class TransactionAspect {

	// transactionManager : DBConfiguration 클래스에 빈으로 등록한 platformTransactionManger 객체임
	@Autowired
	private TransactionManager transactionManager;

	// EXPRESSION : 포인트컷, 비즈니스 로직을 수행하는 모든 ServiceImpl 클래스의 모든 메소드를 의미
	private static final String EXPRESSION = "execution(* com.board..service.*Impl.*(..))";

	@Bean
	public TransactionInterceptor transactionAdvice() {
		
		// rollbackRules : 트랜잭션에서 롤백을 수행하는 규칙
		/* RollbackRuleAttribute 생성자의 인자로 Exception 클래스를 지정함 -> 자바에서 모든 예외는 Exception 클래스를 상속받기 때문에
		 * 어떠한 예외가 발생하던 무조건 롤백이 수행됨
		 */
		List<RollbackRuleAttribute> rollbackRules = Collections.singletonList(new RollbackRuleAttribute(Exception.class));

		RuleBasedTransactionAttribute transactionAttribute = new RuleBasedTransactionAttribute();
		transactionAttribute.setRollbackRules(rollbackRules);
		// setName 메소드 : 트랜잭션의 이름을 설정
		transactionAttribute.setName("*");

		MatchAlwaysTransactionAttributeSource attributeSource = new MatchAlwaysTransactionAttributeSource();
		attributeSource.setTransactionAttribute(transactionAttribute);

		return new TransactionInterceptor(transactionManager, attributeSource);
	}

	@Bean
	public Advisor transactionAdvisor() {

		// pointcut : AOP의 포인트컷을 설정함 / EXPRESSION에 지정한 ServiceImpl 클래스의 모든 메소드를 대상으로 설정함
		AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
		pointcut.setExpression(EXPRESSION);

		return new DefaultPointcutAdvisor(pointcut, transactionAdvice());
	}

}