package com.board.domain;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardDTO extends CommonDTO{

	/** 번호 (PK) */
	private Long idx;

	/** 제목 */
	private String title;

	/** 내용 */
	private String content;

	/** 작성자 */
	private String writer;

	/** 조회 수 */
	private int viewCnt;

	/** 공지 여부 */
	private String noticeYn;

	/** 비밀 여부 */
	private String secretYn;
	
	/** 파일 변경 여부 */
	// 파일의 추가, 삭제, 변경이 일어났을 때 특정 로직을 실행하기 위해 추가된 멤버 변수
	private String changeYn;

	/** 파일 인덱스 리스트 */
	// 파일의 추가, 삭제, 변경이 일어났을 때 기존에 등록되어 있던, 즉 상태가 변하지 않은 파일의 번호(PK)를 의미
	private List<Long> fileIdxs;
	
}