package com.board.service;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.board.domain.CommentDTO;
import com.board.mapper.CommentMapper;

@Service
public class CommentServiceImpl implements CommentService {

	@Autowired
	private CommentMapper commentMapper;

	@Override // INSERT와 UPDATE를 하나의 메소드로 처리
	public boolean registerComment(CommentDTO params) {
		int queryResult = 0;

		// 댓글 번호(idx)가 파라미터에 포함되어 있지 않으면 INSERT 실행
		if (params.getIdx() == null) {
			queryResult = commentMapper.insertComment(params);
		} else {
			queryResult = commentMapper.updateComment(params);
		}

		return (queryResult == 1) ? true : false;
	}

	@Override
	public boolean deleteComment(Long idx) {
		int queryResult = 0;

		CommentDTO comment = commentMapper.selectCommentDetail(idx);

		// 댓글의 상세 정보를 조회해서 정상적으로 사용 중인 댓글인 경우에만 삭제를 진행
		if (comment != null && "N".equals(comment.getDeleteYn())) {
			queryResult = commentMapper.deleteComment(idx);
		}

		return (queryResult == 1) ? true : false;
	}

	// 특정 게시글에 포함된 댓글이 1개 이상이면 댓글 목록을 반환
	@Override
	public List<CommentDTO> getCommentList(CommentDTO params) {
		List<CommentDTO> commentList = Collections.emptyList();

		int commentTotalCount = commentMapper.selectCommentTotalCount(params);
		if (commentTotalCount > 0) {
			commentList = commentMapper.selectCommentList(params);
		}

		System.out.println("commentService의 commentList :: " + commentList);
		return commentList;
	}

}