package spring5_webmvc_study.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RegistController {
	@Autowired
	private MemberRegisterService memberRegisterService;

	@RequestMapping("/register/step1")
	public String handlStep1() {
		return "register/step1";
	}

	@PostMapping("/register/step2")
	public String handlStep2(@RequestParam(value = "agree", defaultValue = "false") Boolean agree, Model model) {
		if (!agree) {
			return "register/step1";
		}
		model.addAttribute("registerRequest", new RegisterRequest());
		return "register/step2";
	}

	@GetMapping("/register/step2")
	public String handlStep2() {
		return "redirect:/register/step1";
	}

	@PostMapping("/register/step3")
	public String handlStep3(RegisterRequest regReq) {
//		System.out.println(regReq);
		try {
			memberRegisterService.regist(regReq);
			return "register/step3";
		} catch (DuplicateMemberException e) {
			return "register/step2";
		}
	}

}
