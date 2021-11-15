package com.board.controller;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.board.constant.Method;
import com.board.domain.AttachDTO;
import com.board.domain.BoardDTO;
import com.board.service.BoardService;
import com.board.util.UiUtils;

@Controller
public class BoardController extends UiUtils {

	@Autowired
	private BoardService boardService;

	@GetMapping(value = "/board/write.do")
	public String openBoardWrite(@ModelAttribute("params") BoardDTO params, @RequestParam(value = "idx", required = false) Long idx, Model model) {
		if (idx == null) {
			model.addAttribute("board", new BoardDTO());
		} else {
			BoardDTO board = boardService.getBoardDetail(idx);
			if (board == null || "Y".equals(board.getDeleteYn())) {
				return showMessageWithRedirect("없는 게시글이거나 이미 삭제된 게시글입니다.", "/board/list.do", Method.GET, null, model);
			}
			model.addAttribute("board", board);

			List<AttachDTO> fileList = boardService.getAttachFileList(idx);
			model.addAttribute("fileList", fileList);
		}

		// Criteria [currentPageNo=1, recordsPerPage=10, pageSize=10, searchKeyword=null, searchType=null]
		System.out.println("controller params :: " + params);
		
		return "board/write";
	}

	@PostMapping(value = "/board/register.do")
	public String registerBoard(final BoardDTO params, final MultipartFile[] files, Model model) {
		
		// BoardController params:: Criteria [currentPageNo=1, recordsPerPage=10, pageSize=10, searchKeyword=null, searchType=null]
		System.out.println("BoardController params:: " + params);
		
		Map<String, Object> pagingParams = getPagingParams(params);
		try {
			boolean isRegistered = boardService.registerBoard(params, files);
			if (isRegistered == false) {
				return showMessageWithRedirect("게시글 등록에 실패하였습니다.", "/board/list.do", Method.GET, pagingParams, model);
			}
		} catch (DataAccessException e) {
			return showMessageWithRedirect("데이터베이스 처리 과정에 문제가 발생하였습니다.", "/board/list.do", Method.GET, pagingParams, model);

		} catch (Exception e) {
			return showMessageWithRedirect("시스템에 문제가 발생하였습니다.", "/board/list.do", Method.GET, pagingParams, model);
		}

		return showMessageWithRedirect("게시글 등록이 완료되었습니다.", "/board/list.do", Method.GET, pagingParams, model);
	}

	// Model : 컨트롤러에서 화면(View)으로 데이터를 전달하는 데 사용되는 인터페이스
	@GetMapping(value = "/board/list.do")
	public String openBoardList(@ModelAttribute("params") BoardDTO params, Model model) {
		List<BoardDTO> boardList = boardService.getBoardList(params);
		model.addAttribute("boardList", boardList);

		return "board/list";
	}

	// @ModelAttribute를 이용하면 파라미터로 전달받은 객체를 자동으로 뷰까지 전달할 수 있음
	// 이전 페이지 정보가 담긴 Criteria 클래스를 상속받는 BoardDTO 커맨드 객체를 뷰로 전달
	@GetMapping(value = "/board/view.do")
	public String openBoardDetail(@ModelAttribute("params") BoardDTO params, @RequestParam(value = "idx", required = false) Long idx, Model model) {
		if (idx == null) {
			return showMessageWithRedirect("올바르지 않은 접근입니다.", "/board/list.do", Method.GET, null, model);
		}

		BoardDTO board = boardService.getBoardDetail(idx);
		if (board == null || "Y".equals(board.getDeleteYn())) {
			return showMessageWithRedirect("없는 게시글이거나 이미 삭제된 게시글입니다.", "/board/list.do", Method.GET, null, model);
		}
		model.addAttribute("board", board);
		
		boardService.cntPlus(idx);

		// fileList : 특정 게시글에 등록된 파일 목록을 조회하는 getAttachFileList 메소드의 실행 결과를 뷰로 전달
		List<AttachDTO> fileList = boardService.getAttachFileList(idx); // 추가된 로직
		model.addAttribute("fileList", fileList); // 추가된 로직

		return "board/view";
	}

	// @ModelAttribute : openBoardDetail 메소드와 마찬가지로 이전 페이지 정보가 담겨있는
	//                   Criteria 클래스를 상속받는 BoardDTO 커맨트 객체를 화면(View)으로 전달
	@PostMapping(value = "/board/delete.do")
	public String deleteBoard(@ModelAttribute("params") BoardDTO params, @RequestParam(value = "idx", required = false) Long idx, Model model) {
		if (idx == null) {
			return showMessageWithRedirect("올바르지 않은 접근입니다.", "/board/list.do", Method.GET, null, model);
		}
		
		// pagingParams : UiUtils 클래스에 추가한 getPagingParams 메소드를 호출
		// 				  pagingParams에 담긴 이전 페이지 정보를 showMessageRedirect 메소드의 인자로 전달
		Map<String, Object> pagingParams = getPagingParams(params);
		try {
			boolean isDeleted = boardService.deleteBoard(idx);
			if (isDeleted == false) {
				return showMessageWithRedirect("게시글 삭제에 실패하였습니다.", "/board/list.do", Method.GET, pagingParams, model);
			}
		} catch (DataAccessException e) {
			return showMessageWithRedirect("데이터베이스 처리 과정에 문제가 발생하였습니다.", "/board/list.do", Method.GET, pagingParams, model);

		} catch (Exception e) {
			return showMessageWithRedirect("시스템에 문제가 발생하였습니다.", "/board/list.do", Method.GET, pagingParams, model);
		}

		return showMessageWithRedirect("게시글 삭제가 완료되었습니다.", "/board/list.do", Method.GET, pagingParams, model);
	}
	
	// httpServletResponse : 파라미터로 선언된 해당 객체는 사용자로부터 들어오는 모든 요청 정보를 처리하는 
	//				         HttpServletResponse 클래스의 반대 개념
	@GetMapping("/board/download.do")
	public void downloadAttachFile(@RequestParam(value = "idx", required = false) final Long idx, Model model, HttpServletResponse response) {

		if (idx == null) throw new RuntimeException("올바르지 않은 접근입니다.");

		AttachDTO fileInfo = boardService.getAttachDetail(idx);
		// 파일 번호(idx)가 파라미터로 넘어오지 않는 경우, 예외 발생
		// fileInfo에는 앞에서 추가한 getAttachDetail 메소드의 실행 결과가 담기게 됨
		// 파일 객체가 null 이거나 삭제 여부가 'Y'로 설정된 파일인 경우, 예외를 발생시켜 로직 종료
		if (fileInfo == null || "Y".equals(fileInfo.getDeleteYn())) {
			throw new RuntimeException("파일 정보를 찾을 수 없습니다.");
		}

		// uploadDate : fileInfo에 담긴 파일 등록일(insertTime)을 기준으로 yymmdd 형태로 포매팅한 날짜
		// ex. 파일 등록일이 2021-10-05일 이라면 파일 업로드 경로("C:/develop/upload/211005")에서 211005를 의미
		String uploadDate = fileInfo.getInsertTime().format(DateTimeFormatter.ofPattern("yyMMdd"));
		// uploadPath : 파일 업로드 경로
		// Paths를 이용하면 File.separator를 빈번히 사용하지 않아도 OS의 Path를 구분할 수 있음
		String uploadPath = Paths.get("C:", "develop", "upload", uploadDate).toString();

		// DB에 저장된 원본 파일명을 의미
		String filename = fileInfo.getOriginalName();
		// 업로드 경로에 저장된 파일 객체를 의미
		File file = new File(uploadPath, fileInfo.getSaveName());

		try {
			// data : 해당 변수에는 FileUtils 클래스의 readFileToByteArray 메소드의 호출 결과(byte[])를 저장
			// 	      readFileToByteArray 메소드는 업로드도니 파일 정보(file)를 파라미터로 전달받아서 
			//		  실제 파일 데이터를 byte[] 형태로 변환하는 역할
			//		  *주의 : FileUtils 클래스는 우리가 생성한 클래스가 아닌, org.apache.commons.io 패키지의 FileUtils 클래스!
			byte[] data = FileUtils.readFileToByteArray(file);
			// response 객체의 헤더에 콘텐츠 타입, 사이즈, 형태 등을 설정
			response.setContentType("application/octet-stream");
			response.setContentLength(data.length);
			response.setHeader("Content-Transfer-Encoding", "binary");
			// 파일명은 반드시 UTF-8로 인코딩
			response.setHeader("Content-Disposition", "attachment; fileName=\"" + URLEncoder.encode(filename, "UTF-8") + "\";");

			// byte[] 형태의 파일 정보를 이용해서 response에 작성
			// getOutStream().write 메서드가 실행되면 파일 다운로드가 시작
			response.getOutputStream().write(data);
			// getOutputStream().flush 메서드가 실행되면 파일 다운로드가 완료
			response.getOutputStream().flush();
			// getOutputStream().close 메서드가 실행되면 버퍼를 정리하고 닫음
			response.getOutputStream().close();

		} catch (IOException e) {
			throw new RuntimeException("파일 다운로드에 실패하였습니다.");

		} catch (Exception e) {
			throw new RuntimeException("시스템에 문제가 발생하였습니다.");
		}
	}
}