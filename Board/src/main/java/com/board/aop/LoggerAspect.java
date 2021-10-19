package com.board.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/*
 * @Bean은 개발자가 제어할 수 없는 외부 라이브러리를 빈으로 등록할 때 사용하고,
 * @Component는 개발자가 직접 정의한 클래스를 빈으로 등록할 때 사용
 */
@Component // 스프링 컨테이너에 빈으로 등록하기 위한 어노테이션
@Aspect // AOP 기능을 하는 클래스의 클래스 레벨에 지정하는 어노테이션
public class LoggerAspect {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	// Advice의 종류 중 하나, Around는 메소드의 호출 자체를 제어할 수 있기 때문에 Advice 중 가장 강력한 기능
	// @Around : Target 메소드 호출 이전과 이후 모두 적용
	// execution으로 시작하는 구문은 포인트컷을 지정하는 문법
	/*
	 * 1. * com.board..controller.*Controller.*(..)
	 *    => com.board 패키지의 모든 하위 패키지 중 controller로 시작하는 패키지에서 xxController와 같은 패턴의 이름을 가진 클래스에서
	 *       파라미터가 0개 이상인 메소드를 의미
	 * 2. * com.board..service.*Impl.*(..)
	 *    => com.board 패키지의 모든 하위 패키지 중 service로 시작하는 패키지에서 xxServiceImpl과 같은 패턴의 이름을 가진 클래스에서
	 *       파라미터가 0개 이상인 메소드를 의미
	 * 3. * com.board..mapper.*Mapper.*(..)
	 * 	  => com.board 패키지에서 모든 하위 패키지 중 mapper로 시작하는 패키지에서 xxxMapper와 같은 패턴의 이름을 가진 인터페이스에서
	 *       파라미터가 0개 이상인 메소드를 의미
	 *       
	 */
	
	/*
	 * 전체 로직
	 * 메소드에 대한 정보를 가지고 있는 Signature 객체에 담겨 있는
	 * 파일명을 포함한 전체 패키지 경로를 name에 담아
	 * 어떤 클래스의 어떤 메소드를 호출하는지를 로그에 출력
	 */
	@Around("execution(* com.board..controller.*Controller.*(..)) or execution(* com.board..service.*Impl.*(..)) or execution(* com.board..mapper.*Mapper.*(..))")
	public Object printLog(ProceedingJoinPoint joinPoint) throws Throwable {

		String type = "";
		String name = joinPoint.getSignature().getDeclaringTypeName();

		if (name.contains("Controller") == true) {
			type = "Controller ===> ";

		} else if (name.contains("Service") == true) {
			type = "ServiceImpl ===> ";

		} else if (name.contains("Mapper") == true) {
			type = "Mapper ===> ";
		}
		
		// ex. name :: com.board.controller.BoardController
		System.out.println("name :: " + name);

		// ex. joinPoint.getSignature().getName() : openBoardList
		logger.debug(type + name + "." + joinPoint.getSignature().getName() + "()");
		return joinPoint.proceed();
	}

}