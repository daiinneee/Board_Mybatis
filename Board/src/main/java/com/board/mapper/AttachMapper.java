package com.board.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.board.domain.AttachDTO;

@Mapper
public interface AttachMapper {

	// 파일 정보를 저장하는 INSERT 쿼리를 호출
	public int insertAttach(List<AttachDTO> attachList);

	// 파라미터로 전달받은 파일 번호(idx)에 해당하는 파일의 상세 정보를 조회
	// 업로드에는 필요하지 않지만, 다운로드 처리에는 필요
	public AttachDTO selectAttachDetail(Long idx);

	// 파일 삭제
	// 게시글, 댓글과 마찬가지로 실제로 레코드를 삭제하지 않고 
	// 특정 게시글에 포함된 모든 파일의 삭제 여부 상태 값만 'Y'에서 'N'으로 업데이트
	public int deleteAttach(Long boardIdx);

	// 특정 게시글에 포함된 파일 목록을 조회하는 SELECT 쿼리를 호출
	public List<AttachDTO> selectAttachList(Long boardIdx);

	// 특정 게시글에 포함된 파일 개수를 조회하는 SELECT 쿼리를 호출
	public int selectAttachTotalCount(Long boardIdx);
	
	// 게시글을 삭제 취소(undelete) 처리하는 메소드
	public int undeleteAttach(List<Long> idxs);

}