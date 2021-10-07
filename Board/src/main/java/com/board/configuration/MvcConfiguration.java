package com.board.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.board.interceptor.LoggerInterceptor;

// LoggerInterceptor 클래스를 빈(Bean)으로 등록하기
@Configuration
public class MvcConfiguration implements WebMvcConfigurer{

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new LoggerInterceptor())
		.excludePathPatterns("/css/**", "/fonts/**", "/plugin/**", "/scripts/**");
		/* 추가적으로 addPathPatterns, excludePathPatterns 메서드를 이용해서
		 * 특정 패턴의 주소(URI)를 추가 또는 제외할 수 있음
		 * 우리는 src/main/resources 디렉터리의 static 폴더에 포함된
		 * 정적 리소스 파일을 제외하기 위해 excludePathPatterns 메서드를 사용
		 */
	}
	
	// 파일 업로드 처리를 위한 빈을 추가
	@Bean
	public CommonsMultipartResolver multipartResolver() {
		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
		multipartResolver.setDefaultEncoding("UTF-8"); // 파일 인코딩 설정
		multipartResolver.setMaxUploadSizePerFile(5 * 1024 * 1024); // 파일당 업로드 크기 제한 (5MB)
		return multipartResolver;
	}

}
