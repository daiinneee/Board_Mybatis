package com.board.paging;

import lombok.Getter;
import lombok.Setter;

// 페이징 정보(화면 하단 페이지 번호) 계산용 클래스
@Getter
@Setter
public class PaginationInfo {

	/** 페이징 계산에 필요한 파라미터들이 담긴 클래스 */
	// 페이지 번호 계산에 필요한 Criteria 클래스의 멤버 변수들에 대한 정보를 가지는 변수
	// ??? 어떻게 가져온거
	private Criteria criteria;

	/** 전체 데이터 개수 */
	private int totalRecordCount;

	/** 전체 페이지 개수 */
	// ex. 전체 데이터의 개수가 300개이고, 페이지당 출력할 데이터의 개수가 10개라고 가정했을 때 30페이지를 의미
	private int totalPageCount;

	/** 페이지 리스트의 첫 페이지 번호 */
	// pageSize가 10이고, currentPageNo이 3이라고 가정했을 때 1페이지를 의미
	private int firstPage;

	/** 페이지 리스트의 마지막 페이지 번호 */
	// pageSize가 10이고, currentPageNo이 3이라고 가정했을 때 10페이지를 의미
	private int lastPage;

	/** SQL의 조건절에 사용되는 첫 RNUM */
	// LIMIT 구문의 첫번째 값에 사용되는 변수
	private int firstRecordIndex;

	/** SQL의 조건절에 사용되는 마지막 RNUM */
	// 오라클과 같이 LIMIT 구문이 존재하지 않고, 인라인뷰를 사용해야 하는 데이터베이스에서 사용 / 현재 MySQL 사용하기 때문에 사용 X
	private int lastRecordIndex;

	/** 이전 페이지 존재 여부 */
	// 이전 페이지가 존재하는 지를 구분하는 용도로 사용됨
	// ex. currentPageNo이 13이라면 이전 페이지에 해당하는 1~10까지의 페이지가 있기 때문에 true가 됨
	//     만약 currentPageNo이 1~10 사이라면 false가 됨
	private boolean hasPreviousPage;

	/** 다음 페이지 존재 여부 */
	// ex. pageSize가 10일 때 lastPage가 25이고, currentPageNo이 13이라면, 11~20페이지 사이에 있기 때문에 true가 됨
	//     만약 currentPageNo이 21~25 사이라면 false가 됨
	private boolean hasNextPage;

	// 생성자
	public PaginationInfo(Criteria criteria) {
		if (criteria.getCurrentPageNo() < 1) {
			criteria.setCurrentPageNo(1);
		}
		if (criteria.getRecordsPerPage() < 1 || criteria.getRecordsPerPage() > 100) {
			criteria.setRecordsPerPage(10);
		}
		if (criteria.getPageSize() < 5 || criteria.getPageSize() > 20) {
			criteria.setPageSize(10);
		}

		this.criteria = criteria;
	}

	// setTotalRecordCoount 메소드 : 파라미터로 넘어온 전체 데이터 개수를 
	// PaginationInfo 클래스의 전체 데이터 개수에 저장함, 전체 데이터 개수가 1개 이상이면 페이지 번호를 계산하는 calculation 메소드를 실
	public void setTotalRecordCount(int totalRecordCount) {
		this.totalRecordCount = totalRecordCount;

		if (totalRecordCount > 0) {
			calculation();
		}
	}

	private void calculation() {

		/* 전체 페이지 수 (현재 페이지 번호가 전체 페이지 수보다 크면 현재 페이지 번호에 전체 페이지 수를 저장) */
		totalPageCount = ((totalRecordCount - 1) / criteria.getRecordsPerPage()) + 1;
		if (criteria.getCurrentPageNo() > totalPageCount) {
			criteria.setCurrentPageNo(totalPageCount);
		}

		/* 페이지 리스트의 첫 페이지 번호 */
		firstPage = ((criteria.getCurrentPageNo() - 1) / criteria.getPageSize()) * criteria.getPageSize() + 1;

		/* 페이지 리스트의 마지막 페이지 번호 (마지막 페이지가 전체 페이지 수보다 크면 마지막 페이지에 전체 페이지 수를 저장) */
		lastPage = firstPage + criteria.getPageSize() - 1;
		if (lastPage > totalPageCount) {
			lastPage = totalPageCount;
		}

		/* SQL의 조건절에 사용되는 첫 RNUM */
		firstRecordIndex = (criteria.getCurrentPageNo() - 1) * criteria.getRecordsPerPage();

		/* SQL의 조건절에 사용되는 마지막 RNUM */
		lastRecordIndex = criteria.getCurrentPageNo() * criteria.getRecordsPerPage();

		/* 이전 페이지 존재 여부 */
		hasPreviousPage = firstPage != 1;

		/* 다음 페이지 존재 여부 */
		hasNextPage = (lastPage * criteria.getRecordsPerPage()) < totalRecordCount;
	}

}