package com.board.constant;

/* Enum은 상수를 처리하는 목적으로 사용되는 클래스 */
public enum Method {
	// 지금까지 사용했던 HTTP 요청 메소드는 데이터 조회 목적으로 사용되는 GET,
	// 데이터베이스에 데이터를 저장할 때 사용되는 POST 총 두가지
	// PUT, PATCH, DELETE는 REST API 방식에서 사용되는 메소드
	GET, POST, PUT, PATCH, DELETE

}
