package com.board.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.board.domain.AttachDTO;
import com.board.exception.AttachFileException;

// 게시글, 댓글의 경우는 Mapper 영역의 작업을 완료한 다음, 서비스 영역을 처리함
// 파일 업로드/다운로드 처리는 여러 곳에서 공통으로 사용될 수 있는 기능
// 그렇기 때문에 페이징 때와 마찬가지로 공통 클래스를 추가해서 사용

// @Bean과 달리, 개발자가 직접 작성한 클래스를 스프링 컨테이너에 등록하는 데 사용
@Component
public class FileUtils {
	
	/** 오늘 날짜 */
	// 업로드 경로(uploadPath)에 포함되는 오늘 날짜를 의미
	private final String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"));

	/** 업로드 경로 */
	// 최종적으로 파일이 업로드되는 경로
	// "C:\develop\ upload\2210914"과 같은 패턴의 디렉터리(폴더)가 생성 / 21년 9월 14일 기준
	/* Paths.get 메소드
	 * 보통, 윈도우와 리눅스 OS의 경로를 구분할 때 File.separator를 이용해서
	 * "C:" + File.separator + "develop" + File.separator와 같이 처리하고는 했었음
	 * Paths.get 메서드를 이용하면 파라미터로 전달한 여러 개의 문자열을 하나로 연결해서 OS에 해당하는 패턴으로 경로를 리턴해줌
	 */
	private final String uploadPath = Paths.get("C:", "develop", "upload", today).toString();

	/**
	 * 서버에 생성할 파일명을 처리할 랜덤 문자열 반환
	 * @return 랜덤 문자열
	 */
	private final String getRandomString() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}

	/**
	 * 서버에 첨부 파일을 생성하고, 업로드 파일 목록 반환
	 * @param files    - 파일 Array
	 * @param boardIdx - 게시글 번호
	 * @return 업로드 파일 목록
	 */
	// MultipartFile[] 타입의 files에는 업로드할 파일의 정보가 담겨있음
	// boardIdx는 파일을 등록할 게시글 번호를 의미
	public List<AttachDTO> uploadFiles(MultipartFile[] files, Long boardIdx) {

		/* 업로드 파일 정보를 담을 비어있는 리스트 */
		// tb_attach 테이블에 저장할 업로드 파일 정보를 담을 비어있는 리스트를 생성
		List<AttachDTO> attachList = new ArrayList<>();

		/* uploadPath에 해당하는 디렉터리가 존재하지 않으면, 부모 디렉터리를 포함한 모든 디렉터리를 생성 */
		// dir 객체 변수에 업로드 경로를 의미하는 uploadPath 정보를 저장
		File dir = new File(uploadPath);
		// if문에서는 uploadPath에 해당하는 디렉터리가 존재하지 않는 경우,
		// uploadPath의 develop부터 today까지의 모든 디렉터리를 생성함
		if (dir.exists() == false) {
			dir.mkdirs(); // mkdir과 mkdirs는 확연히 다른 기능!
		}

		/* 파일 개수만큼 forEach 실행 */
		// 파일을 생성하고, 파일 정보를 리스트에 저장하는 가장 핵심이 되는 로직
		for (MultipartFile file : files) {
			if (file.getSize() < 1) {
				continue;
			}
			try {
				/* 파일 확장자 */
				final String extension = FilenameUtils.getExtension(file.getOriginalFilename());
				/* 서버에 저장할 파일명 (랜덤 문자열 + 확장자) */
				final String saveName = getRandomString() + "." + extension;

				/* 업로드 경로에 saveName과 동일한 이름을 가진 파일 생성 */
				// target이라는 이름으로, 업로드 경로와 파일명이 담긴 파일 객체를 생성
				File target = new File(uploadPath, saveName);
				// transferTo 메소드를 이용해서 target에 담긴 파일 정보에 해당하는 파일을 생성
				file.transferTo(target);

				/* 파일 정보 저장 */
				// tb_attach 테이블에 파일 정보를 저장하기 위해 AttachDTO 객체에 파일 정보를 담고,
				// attachList에 파일 정보를 추가
				AttachDTO attach = new AttachDTO();
				attach.setBoardIdx(boardIdx);
				attach.setOriginalName(file.getOriginalFilename());
				attach.setSaveName(saveName);
				attach.setSize(file.getSize());

				/* 파일 정보 추가 */
				attachList.add(attach);

			} catch (IOException e) {
				throw new AttachFileException("[" + file.getOriginalFilename() + "] failed to save file...");

			} catch (Exception e) {
				throw new AttachFileException("[" + file.getOriginalFilename() + "] failed to save file...");
			}
		} // end of for

		// 작업이 마무리되면 파일 정보를 담은 attachList를 반환
		return attachList;
	}

}
