package com.board.paging;

import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Criteria {

	/** 현재 페이지 번호 */
	// 화면을 처리할 때 페이징 정보를 계산하는 용도로 사용되는 변수
	private int currentPageNo;

	/** 페이지당 출력할 데이터 개수 */
	// 화면 처리에서 페이징 정보 계산에 사용됨
	private int recordsPerPage;

	/** 화면 하단에 출력할 페이지 사이즈 */
	// ex. 10으로 지정하면 1~10까지의 페이지가 보이게 됨
	private int pageSize;

	/** 검색 키워드 */
	// 원하는 데이터를 찾기 위해 페이지를 일일이 넘기는건 불가능 -> 검색 키워드 필요 -> 동적 SQL을 처리하면서 사용됨
	private String searchKeyword;

	/** 검색 유형 */
	// searchKeyword와 함께 사용됨 / 게시글의 제목, 내용, 작성자 중 하나 또는 전체로 LIKE 검색 가능
	private String searchType;

	// 생성자
	// 기본값으로 현재 페이지 번호는 1로, 페이지당 출력할 데이터 개수와 하단에 출력할 페이지 개수는 10으로 지정함
	public Criteria() {
		this.currentPageNo = 1;
		this.recordsPerPage = 10;
		this.pageSize = 10;
	}
	
	/** makeQuerySting 메소드 */
	// Criteria 클래스의 멤버 변수들을 쿼리 스트링 형태로 반환함
	// 스프링에서 제공해주는 UriComponents 클래스를 사용하면 URI를 더욱 효율적으로 처리할 수 있음
	public String makeQueryString(int pageNo) {

		UriComponents uriComponents = UriComponentsBuilder.newInstance()
				.queryParam("currentPageNo", pageNo)
				.queryParam("recordsPerPage", recordsPerPage)
				.queryParam("pageSize", pageSize)
				.queryParam("searchType", searchType)
				.queryParam("searchKeyword", searchKeyword)
				.build()
				.encode();
		System.out.println("호출 결과 : " + uriComponents.toUriString());
		return uriComponents.toUriString();
	}

//	@Override
//	public String toString() {
//		return "Criteria [currentPageNo=" + currentPageNo + ", recordsPerPage=" + recordsPerPage + ", pageSize="
//				+ pageSize + ", searchKeyword=" + searchKeyword + ", searchType=" + searchType + "]";
//	}
//	
	

}