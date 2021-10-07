package com.board.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.board.domain.AttachDTO;
import com.board.domain.BoardDTO;

public interface BoardService {

	public boolean registerBoard(BoardDTO params);
	
	// 오버로딩 : 파라미터의 타입 또는 개수를 다르게 해서 메소드를 선언하는 것
	public boolean registerBoard(BoardDTO params, MultipartFile[] files);

	public BoardDTO getBoardDetail(Long idx);

	public boolean deleteBoard(Long idx);

	public List<BoardDTO> getBoardList(BoardDTO params);
	
	// 파일 리스트를 조회하는 메소드
	public List<AttachDTO> getAttachFileList(Long boardIdx);
	
	// 파일의 상세 정보를 조회하는 메소드
	public AttachDTO getAttachDetail(Long idx);

}