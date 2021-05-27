package spring5_webmvc_study.controller;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestMemberController {

	@Autowired
	private MemberDao memberDao;
	
	@Autowired
	private MemberRegisterService service;
	
	@GetMapping("/api/members")
	public List<Member> members(){
		return memberDao.selectAll();
	}
	
//	@GetMapping("/api/members/{id}")
//	public Member member(@PathVariable long id, HttpServletResponse response) throws IOException {
//		Member member = memberDao.selectbyId(id);
//		if(member == null) {
//			response.sendError(HttpServletResponse.SC_NOT_FOUND);
//			return null;
//		}
//		return member;
//	}
	
	@GetMapping("/api/members/{id}")
	public ResponseEntity<Object> member(@PathVariable long id, HttpServletResponse response) throws IOException{
		Member member = memberDao.selectbyId(id);
		if(member == null) {
//			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("no member"));
			throw new MemberNotFoundException();
		}
		return ResponseEntity.status(HttpStatus.OK).body(member);
	}
	
	@ExceptionHandler(MemberNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleNoData(){
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("no member"));
	}
	
	@PostMapping("/api/members")
	public ResponseEntity<Object> member(@RequestBody RegisterRequest regReq, Errors errors, HttpServletResponse response) throws IOException {
		try {
			new RegisterRequestValidator().validate(regReq, errors);
			if(errors.hasErrors()) {
//				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
//				return ResponseEntity.badRequest().build();
				String errorCodes = errors.getAllErrors()
									.stream()
									.map(error -> error.getCodes()[0])
									.collect(Collectors.joining(", "));
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(new ErrorResponse("errorCodes = " + errorCodes));
			}			
			long newMemberId = service.regist(regReq);
//			response.setHeader("Location", "/api/members/" + newMemberId);
//			response.setStatus(HttpServletResponse.SC_CREATED);
			URI uri = URI.create("/api/members/" + newMemberId);
			return ResponseEntity.created(uri).build();
		}catch (DuplicateMemberException e) {
//			response.sendError(HttpServletResponse.SC_CONFLICT);
			return ResponseEntity.status(HttpStatus.CONFLICT).build();
		}
	}	
	
	
	
	
}
