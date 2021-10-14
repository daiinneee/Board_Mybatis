package com.board.service;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import com.board.domain.AttachDTO;
import com.board.domain.BoardDTO;
import com.board.mapper.AttachMapper;
import com.board.mapper.BoardMapper;
import com.board.paging.PaginationInfo;
import com.board.util.FileUtils;

@Service
public class BoardServiceImpl implements BoardService {

	@Autowired
	private BoardMapper boardMapper;
	
	@Autowired
	private AttachMapper attachMapper;

	@Autowired
	private FileUtils fileUtils;

	@Override
	public boolean registerBoard(BoardDTO params) {

		int queryResult = 0;
		
		// 게시글 생성
		if (params.getIdx() == null) {
			queryResult = boardMapper.insertBoard(params);
		} else { // 게시글 수정
			queryResult = boardMapper.updateBoard(params);

			// 파일이 추가, 삭제, 변경된 경우
			// 게시글이 수정되는 시점에 파일이 추가, 삭제, 변경되었으면 기존의 파일을 모두 삭제 처리
			if ("Y".equals(params.getChangeYn())) {
				attachMapper.deleteAttach(params.getIdx());

				// fileIdxs에 포함된 idx를 가지는 파일의 삭제여부를 'N'으로 업데이트
				/* 기존에 특정 게시글에 포함되어 있던 파일이 유지되는 경우를 의미
				 * ex. 100번 게시글에 A, B, C라는 파일이 등록되어 있던 상태에서
				 *     B와 C를 삭제하고, 다시 게시글을 등록하면 모든 파일을 삭제 처리함
				 *     하지만, filesIdx에는 A 파일에 대한 idx(파일 번호)가 포함되어 있기 때문에
				 *     해당 파일을 삭제 취소(undelete) 처리함
				 * 	   결론적으로 A, B, C 모두 delete_yn 컬럼의 값이 'Y'로 업데이트 되고,
				 * 	   A는 다시 delete_yn 컬럼의 값이 'N'으로 업데이트 됨
				 */
				if (CollectionUtils.isEmpty(params.getFileIdxs()) == false) {
					attachMapper.undeleteAttach(params.getFileIdxs());
				}
			}
		}

		return (queryResult > 0);
	}
	
	@Override
	public boolean registerBoard(BoardDTO params, MultipartFile[] files) {
		int queryResult = 1;

		if (registerBoard(params) == false) {
			return false;
		}

		/* 사용자가 새로운 게시글을 등록한다고 가정했을 때 게시글 번호는 MySQL의 AUTO_INCREMENT 속성에 의해
		 * 자동으로 증가됨 그러나 57번 라인의 uploadFiles 메소드로 전달하는 params의 idx는 게시글이 생성된 이후에도
		 * NULL 값이 담기게 됨, 파일이 없이 게시글만 생성되는 경우에는 상관이 없지만 새로 등록되는 게시글엘 파일이 포함되어 있다면
		 * 게시글 번호를 전달할 수 없음 이럴 때, MyBatis의 useGeneratedKeys, keyProperty 속성을 이용할 수 있음
		 * 
		 * useGeneratedKeys, keyProperty
		 * 해당 속성을 true로 지정하면 INSERT 쿼리의 실행과 동시에 생성된 PK가
		 * 파라미터로 전달된 객체, 즉 BoardDTO 클래스의 객체인 params의 게시글 번호(idx)에 담기게 됨   
		 */
		List<AttachDTO> fileList = fileUtils.uploadFiles(files, params.getIdx());
		if (CollectionUtils.isEmpty(fileList) == false) {
			queryResult = attachMapper.insertAttach(fileList);
			if (queryResult < 1) {
				queryResult = 0;
			}
		}

		return (queryResult > 0); // 이렇게 하면 T/F 반환?
	}

	@Override
	public BoardDTO getBoardDetail(Long idx) {
		return boardMapper.selectBoardDetail(idx);
	}

	@Override
	public boolean deleteBoard(Long idx) {
		int queryResult = 0;

		BoardDTO board = boardMapper.selectBoardDetail(idx);
		
		// 조회한 게시글이 null이 아니고, 삭제된 상태가 아닐 때 실행
		if (board != null && "N".equals(board.getDeleteYn())) {
			queryResult = boardMapper.deleteBoard(idx);
		}

		return (queryResult == 1) ? true : false;
	}

	@Override
	public List<BoardDTO> getBoardList(BoardDTO params) {
		List<BoardDTO> boardList = Collections.emptyList();

		int boardTotalCount = boardMapper.selectBoardTotalCount(params);

		PaginationInfo paginationInfo = new PaginationInfo(params);
		paginationInfo.setTotalRecordCount(boardTotalCount);

		params.setPaginationInfo(paginationInfo);

		if (boardTotalCount > 0) {
			boardList = boardMapper.selectBoardList(params);
		}

		return boardList;
	}
	
	@Override
	public List<AttachDTO> getAttachFileList(Long boardIdx) {

		// 파일 개수 조회
		int fileTotalCount = attachMapper.selectAttachTotalCount(boardIdx);
		// 파일 개수가 1개 이상이면 boardIdx에 해당하는 게시글에 포함된 파일 리스트를 리턴
		if (fileTotalCount < 1) {
			return Collections.emptyList();
		}
		return attachMapper.selectAttachList(boardIdx);
	}
	
	@Override
	public AttachDTO getAttachDetail(Long idx) {
		return attachMapper.selectAttachDetail(idx);
	}
	
	@Override
	public boolean cntPlus(Long idx) {
		return boardMapper.cntPlus(idx);
	}

}