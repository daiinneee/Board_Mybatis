package com.board.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.board.domain.CommentDTO;

// @Mapper : 해당 인터페이스가 데이터베이스와 통신하는 인터페이스임을 의미
@Mapper
public interface CommentMapper {

	// 댓글을 삽입하는 INSERT 쿼리를 호출
	public int insertComment(CommentDTO params);

	// 파라미터로 전달받은 댓글 번호(idx)에 해당하는 댓글의 상세 내용을 조회
	// 사실, 댓글은 리스트 형식으로 출력하기 때문에 상세 내용은 딱히 필요 X
	// 댓글 삭제 처리에 해당 메소드를 사용
	public CommentDTO selectCommentDetail(Long idx);

	// 댓글을 수정하는 UPDATE 쿼리를 호출
	public int updateComment(CommentDTO params);

	// 댓글을 삭제하는 메소드
	// DELETE 쿼리를 호출하지 않고, UPDATE 쿼리를 호출해서 delete_yn 컬럼의 상태를 'Y'로 지정
	public int deleteComment(Long idx);

	// 특정 게시글에 포함된 댓글 조회하는 SELECT 쿼리를 호출
	public List<CommentDTO> selectCommentList(CommentDTO params);

	// 특정 개시글에 포함된 댓글 개수를 조회하는 SELECT 쿼리를 호출
	public int selectCommentTotalCount(CommentDTO params);

}