package com.board.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// @Controller
// @RestController는 클래스 레벨에 선언 가능
// @RestController가 붙은 컨트롤러의 모든 메소드는 자동으로 @ResponseBody가 적용되는 것으로 생각할 수 있음
@RestController
public class TestController {

	// 메소드에 @ResponseBody가 붙게 되면 스프링의 메시지 컨버터(MessageConverter)에 의해 화면(HTML)이 아닌, 
	// 리턴 타입에 해당하는 데이터 자체를 리턴하게 됨
	/*
	@GetMapping(value = "/message")
	@ResponseBody // public @ResponseBody String testByResponseBody()와 같이 리턴 타입 좌측에 지정 가능
	public String testByResponseBody() {
		String message = "안녕하세요. 잠시 후에 화면에서 만나요!";
		return message;
	}
	*/
	
	/*
	@GetMapping(value = "/members")
	@ResponseBody // public @ResponseBody String testByResponseBody()와 같이 리턴 타입 좌측에 지정 가능
	public Map<Integer, Object> testByResponseBody() {

		Map<Integer, Object> members = new HashMap<>();

		for (int i = 1; i <= 20; i++) {
			Map<String, Object> member = new HashMap<>();
			member.put("idx", i);
			member.put("nickname", i + "길동");
			member.put("height", i + 20);
			member.put("weight", i + 30);
			members.put(i, member);
		}

		return members;
		// 화면 반환 결과 : {"1":{"nickname":"1길동","weight":31,"idx":1,"height":21},"2":{"nickname":"2길동","weight":32,"idx":2,"height":22}, ...
	}
	*/
	
	@GetMapping(value = "/members")
	public Map<Integer, Object> testByResponseBody() {

		Map<Integer, Object> members = new HashMap<>();

		for (int i = 1; i <= 20; i++) {
			Map<String, Object> member = new HashMap<>();
			member.put("idx", i);
			member.put("nickname", i + "길동");
			member.put("height", i + 20);
			member.put("weight", i + 30);
			members.put(i, member);
		}

		return members;
	}
}