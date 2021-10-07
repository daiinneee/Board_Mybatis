package com.board.util;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;

import com.board.constant.Method;
import com.board.paging.Criteria;

/* 컨트롤러에서 메시지를 전달하는 방법 */
@Controller
public class UiUtils {

	public String showMessageWithRedirect(@RequestParam(value = "message", required = false) String message,
										  @RequestParam(value = "redirectUri", required = false) String redirectUri,
										  @RequestParam(value = "method", required = false) Method method,
										  @RequestParam(value = "params", required = false) Map<String, Object> params, Model model) {

		// message : 사용자에게 전달할 메시지를 의미
		model.addAttribute("message", message); 
		/* redirectUri : 리다이렉트 할 URI를 의미
		 * 시나리오
		 * 1. 게시글 작성
		 * 2. "게시글 등록이 완료되었습니다." 메시지를 사용자에게 전달
		 * 3. 게시글 리스트 페이지로 리다이렉트	
		 */
		model.addAttribute("redirectUri", redirectUri); 
		// 앞에서 추가한 Method Enum 클래스에 선언한 HTTP 요청 메소드
		model.addAttribute("method", method); 
		/* 화면(View)으로 전달할 파라미터
		 * 파라미터 개수는 어떤 페이지인지에 따라 달라질 수 있으므로
		 * 여러 가지 데이터를 Key, Value 형태로 담을 수 있는 Map을 사용
		 * 예를 들어, 페이징 처리가 되어있다고 가정해 보도록 하겠다.
		 * 3페이지에서 30번 게시글을 수정했으면
		 * 수정이 완료된 다음에도 다시 3페이지에 머물러야 함
		 * 이때, 이전 페이지의 정보를 params에 담아 리다이렉트 하면 3페이지로 돌아갈 수 있음
		 */
		model.addAttribute("params", params); 

		return "utils/message-redirect";
	}
	
	// Criteria 클래스의 모든 멤버 변수(이전 페이지 정보)를 Map에 Key, Value 형태로 담아 리턴하는 메소드
	// 해당 메소드는 GET 방식이 아닌 POST 방식의 처리에서만 사용
	public Map<String, Object> getPagingParams(Criteria criteria) {

		Map<String, Object> params = new LinkedHashMap<>();
		params.put("currentPageNo", criteria.getCurrentPageNo());
		params.put("recordsPerPage", criteria.getRecordsPerPage());
		params.put("pageSize", criteria.getPageSize());
		params.put("searchType", criteria.getSearchType());
		params.put("searchKeyword", criteria.getSearchKeyword());

		return params;
	}

}