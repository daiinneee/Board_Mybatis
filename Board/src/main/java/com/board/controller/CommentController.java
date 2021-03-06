package com.board.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.board.adapter.GsonLocalDateTimeAdapter;
import com.board.domain.CommentDTO;
import com.board.service.CommentService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@RestController
public class CommentController {

	@Autowired
	private CommentService commentService;

	// 게시글의 경우, 하나의 URI로 생성(INSERT)과 수정(UPDATE) 처리가 가능하지만,
	// REST API는 설계 규칙을 지켜야 하기 때문에 URI가 구분되어야 함
	/* 1. "/comments" : 새로운 댓글의 등록
	   2. "/comments/{idx}" : 댓글 테이블의 PK인 댓글 번호(idx)에 해당하는 댓글의 수정을 의미
	   3. RequestMethod.POST : HTTP 요청 메소드 중 POST를 의미, @PostMapping과 유사
	   4. RequestMethod.PATCH : HTTP 요청 메소드 중 PATCH를 의미, @PatchMapping과 유사
	   5. @PatchMapping : Patch를 위한 HTTP(S) 요청 처리를 위한 어노테이션 
	   6. @RequestBody : @ResponseBody와 마찬가지로 REST 방식의 처리에 사용
	      파라미터 앞에 @RequestBody가 지정되면, 파라미터로 전달받은 JSON 문자열을 객체로 변환
	      
	      @RequestBody의 역할
	      1. 클라이언트(사용자)는 게시글 번호, 댓글 내용, 댓글 작성자를 JSON 문자열로 전송
	      2. 서버(컨트롤러)는 JSON 문자열을 파라미터로 전달받음
	      3. @RequestBody는 전달받은 JSON 문자열을 객체로 변환
	      4. 객체로 변환된 JSON은 CommentDTO 클래스의 객체인 params에 매핑(바인딩)됨
	*/
	@RequestMapping(value = { "/comments", "/comments/{idx}" }, method = { RequestMethod.POST, RequestMethod.PATCH })
	public JsonObject registerComment(@PathVariable(value = "idx", required = false) Long idx, @RequestBody final CommentDTO params) {
		
		// registerComment의 params :: Criteria [currentPageNo=1, recordsPerPage=10, pageSize=10, searchKeyword=null, searchType=null]
		System.out.println("registerComment의 params :: " + params);

		// 첫번째로 결과를 저장할 JSON 객체를 생성
		JsonObject jsonObj = new JsonObject();

		try {
			// boolean 타입의 변수인 isResgisterd에는 CommentService의 registerComment 메소드를 실행한 결과를 저장
			boolean isRegistered = commentService.registerComment(params);
			// 댓글의 생성 또는 수정이 실행되면 true를, 실행되지 않으면 false를 저장
			// 메소드의 실행 결과를 "result"라는 이름의 프로퍼티로 JSON 객체에 추가해서 리턴
			jsonObj.addProperty("result", isRegistered);

		} catch (DataAccessException e) {
			jsonObj.addProperty("message", "데이터베이스 처리 과정에 문제가 발생하였습니다.");

		} catch (Exception e) {
			jsonObj.addProperty("message", "시스템에 문제가 발생하였습니다.");
		}

		// registerComment의 jsonObj :: {"result":true}
		System.out.println("registerComment의 jsonObj :: " + jsonObj);
		return jsonObj;
	}

	@GetMapping(value = "/comments/{boardIdx}")
	public JsonObject getCommentList(@PathVariable("boardIdx") Long boardIdx, @ModelAttribute("params") CommentDTO params) {
		
		// getCommentList의 params :: Criteria [currentPageNo=1, recordsPerPage=10, pageSize=10, searchKeyword=null, searchType=null]
		System.out.println("getCommentList의 params :: " + params);

		// 1. JSON 객체를 생성
		JsonObject jsonObj = new JsonObject();

		// 2. getCommentList 메소드를 호출한 결과, 즉 댓글 목록을 commentList에 저장
		List<CommentDTO> commentList = commentService.getCommentList(params);
		
		// commentList 1 :: [Criteria [currentPageNo=1, recordsPerPage=10, pageSize=10, searchKeyword=null, searchType=null]]
		System.out.println("commentList 1 :: " + commentList);
		
		// 3. 등록된 댓글이 1개 이상이면, Gson 라이브러리에서 제공해주는 Gson 클래스의 메소드를 사용해서
		//    commentList에 담긴 댓글을 JsonArray 타입으로 반환하고,
		//    JSON 객체에 "commentList"라는 프로퍼티를 추가해서 리턴
		if (CollectionUtils.isEmpty(commentList) == false) {
			Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new GsonLocalDateTimeAdapter()).create();
			JsonArray jsonArr = gson.toJsonTree(commentList).getAsJsonArray();
			
			// jsonArr :: [{"idx":45,"boardIdx":16378,"content":"안녕","writer":"관리자","deleteYn":"N","insertTime":"2021-11-12T13:32:36","currentPageNo":1,"recordsPerPage":10,"pageSize":10}]
			System.out.println("jsonArr :: " + jsonArr);
			
			/*
			 * { 
			 *   "commentList" : 
			 *     0: {
			 *         "idx" : 20,
			 *         "boardIdx" : 6529,
			 *         "content" : "20번 댓글을 추가합니다!",
			 *         "writer" : "20번 회원",
			 *         "deleteYn" : "N",
			 *         "insertTime" : "2021-11-10T10:42:31",
			 *         "currentPageNo" : 1,
			 *         "recordsPerPage" : 10,
			 *         "pageSize" : 10
			 *     }
			 *     1: {
			 *        "idx" : 19,
			 *         ...
			 *     }
			 * }
			 */
			jsonObj.add("commentList", jsonArr);
			
			// commentList :: [Criteria [currentPageNo=1, recordsPerPage=10, pageSize=10, searchKeyword=null, searchType=null]]
			System.out.println("commentList :: " + jsonObj);
		}

		return jsonObj;
	}
	
	// @DeleteMapping : HTTP 요청 메소드 중 DELETE를 의미
	// 지금은 실제로 댓글을 삭제하지 않지만, URI 구분을 위해 @DeleteMapping을 선언
	
	// @PathVariable : 댓글 리스트 처리에 이용했던 어노테이션으로, REST 방식에서 리소스를 표현한는 데 사용
	// @PathVariable은 URI에 파라미터로 전달받을 변수를 지정할 수 있음
	// "/comments/{idx}" URI의 {idx}는 댓글 번호(PK)를 의미하며, @PathVariable의 idx와 매핑(바인딩)됨
	@DeleteMapping(value = "/comments/{idx}")
	public JsonObject deleteComment(@PathVariable("idx") final Long idx) {
		
		// JSON 객체를 생성
		JsonObject jsonObj = new JsonObject();
		System.out.println("여기까지 왔나요");

		try {
			// CommentService의 deleteComment 메서드의 실행 결과를 JSON 객체에 저장
			boolean isDeleted = commentService.deleteComment(idx);
			jsonObj.addProperty("result", isDeleted);
		  
			// 각 catch 문에 해당되는 예외가 발생하면 예외 메시지를 JSON 객체에 저장
		} catch (DataAccessException e) {
			jsonObj.addProperty("message", "데이터베이스 처리 과정에 문제가 발생하였습니다.");

		} catch (Exception e) {
			jsonObj.addProperty("message", "시스템에 문제가 발생하였습니다.");
		}

		// JSON 객체를 리턴
		return jsonObj;
	}

}