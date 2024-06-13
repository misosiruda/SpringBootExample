package com.shop.dto;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class MemberFormDto {
    @NotBlank(message = "이름은 필수 입력값 입니다.")
    private String name;  //회원이름

    @NotEmpty(message = "이메일 은 필수 입력값 입니다.")
    @Email(message = "이메일 형식 으로 입력해주세요.")
    private String email;  //이메일

    @NotEmpty(message = "비밀번호 는 필수 입력값 입니다.")
    @Length(min=4, max = 16, message = "비밀번호 4자이상, 16자 이하로 입력해주세요.")
    private String password;  //비밀번호

    @NotEmpty(message = "주소는 필수 입력값 입니다.")
    private String address;  //주소
}
