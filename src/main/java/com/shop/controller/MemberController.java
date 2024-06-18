package com.shop.controller;


import com.shop.dto.MemberFormDto;
import com.shop.entity.Member;
import com.shop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@RequestMapping("/members")
@Controller
@RequiredArgsConstructor
public class MemberController {

    public static final String MEMBER_FORM_PAGE_STRING = "member/memberForm";
    private final MemberService memberService;

    private final PasswordEncoder passwordEncoder;

    @GetMapping(value = "/new")
    public String memberForm(Model model) {
        model.addAttribute("memberFormDto", new MemberFormDto());
        return MEMBER_FORM_PAGE_STRING;
    }


    // 폼 데이터를 받아 유효성 검사를 수행하고 결과를 모델에 추가하여 회원을 생성하는 메소드
    @PostMapping(value = "/new")
    // 사용자가 제출한 폼 데이터를 MemberFormDto 객체에 바인딩하고, 해당 객체가 유효성 검사를 받아야 함을 나타낸다.
    // bindingResult는 @Valid 어노테이션에 의해 수행된 유효성 검사 결과를 담고 있다.
    // model 객체는 컨트롤러가 뷰로 데이터를 전달할 때 사용된다.
    public String memberFormSubmit(@Valid MemberFormDto memberFormDto, BindingResult bindingResult, Model model) {

        //유효성 검사에서 오류가 있는지 확인
        if (bindingResult.hasErrors()) {
            return MEMBER_FORM_PAGE_STRING;
        }
        try {
            // MemberFormDto를 사용하여 Member 객체를 생성하고 비밀번호를 암호화
            Member member = Member.createMember(memberFormDto, passwordEncoder);
            memberService.saveMember(member);
        } catch (IllegalStateException e) {
            // 예외가 발생하면 오류 메시지를 모델에 추가하고 폼 페이지로 돌아감
            model.addAttribute("errorMessage", e.getMessage());
            return MEMBER_FORM_PAGE_STRING;
        }
        return "redirect:/"; // 회원 생성이 성공하면 회원 목록 페이지로 리디렉션
    }


    @GetMapping(value = "/")
    public String main() {
        return "main";
    }

   @GetMapping(value="/login")
    public String loginMember() {
     return "member/memberLoginForm";
   }

   @GetMapping(value="/login/error")
    public String loginMemberError(Model model) {
         model.addAttribute("loginErrorMsg", "아이디 또는 비밀번호를 확인해주세요.");
         return "member/memberLoginForm";
   }




}
