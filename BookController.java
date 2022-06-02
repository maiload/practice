package com.ez.prac.book.controller;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ez.prac.book.model.BookDTO;
import com.ez.prac.book.model.BookService;
import com.ez.prac.common.PaginationInfo;
import com.ez.prac.common.SearchVO;
import com.ez.prac.common.Utility;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/book")
@RequiredArgsConstructor
public class BookController {
	private static Logger logger
	=LoggerFactory.getLogger(BookController.class);
	
	private final BookService bookService;
	
	//http://localhost:9091/prac/book/bookWrite.do
	@GetMapping("/bookWrite.do")
	public String write_get() {
		logger.info("글쓰기 화면");
		
		return "/book/bookWrite";
	}
	
	@PostMapping("/bookWrite.do")
	public String write_post(@ModelAttribute BookDTO dto) {
		logger.info("글 등록 처리, 파라미터 dto={}", dto); 
		
		int cnt=bookService.insertBook(dto);
		logger.info("글 등록 결과, cnt={}",cnt);
		
		return "redirect:/book/bookList.do";
	}
	
	@RequestMapping("/bookList.do")
	public String list_post(@ModelAttribute SearchVO searchVo,
			Model model) {
		logger.info("페이징 화면, 파라미터 searchVO={}",searchVo);
		
		//[1] PaginationInfo 생성
		PaginationInfo pagingInfo=new PaginationInfo();
		pagingInfo.setBlockSize(Utility.BLOCKSIZE);
		pagingInfo.setRecordCountPerPage(Utility.RECORD_COUNT);
		pagingInfo.setCurrentPage(searchVo.getCurrentPage());
		
		//[2] searchVo에 페이징 처리 관련 변수의 값 셋팅
		searchVo.setFirstRecordIndex(pagingInfo.getFirstRecordIndex());
		searchVo.setRecordCountPerPage(Utility.RECORD_COUNT);
		
		List<BookDTO> list=bookService.selectAll(searchVo);
		logger.info("글 목록 조회 결과, list.size={}", list.size());
		
		//totalRecord개수 구하기
		int totalRecord=bookService.getTotalRecord(searchVo);
		logger.info("글 목록 totalRecord={}", totalRecord);
		
		pagingInfo.setTotalRecord(totalRecord);
		
		model.addAttribute("list", list);
		model.addAttribute("pagingInfo", pagingInfo);
		
		return "/book/bookList";
	}
	
	@GetMapping("/bookDetail.do")
	public String detail(@RequestParam(defaultValue = "0") int no,
			Model model) {
		logger.info("상세보기 화면, 파라미터 no={}",no);
		
		BookDTO dto=bookService.selectByNo(no);
		logger.info("상세보기 처리, dto={}",dto);
		
		model.addAttribute("dto",dto);
		return "/book/bookDetail";
	}
}
