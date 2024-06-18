package com.shop.entity;

import com.shop.constant.Role;
import com.shop.repository.MemberRepository;
import com.shop.service.MemberService;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Transactional
@Log4j2
class MemberTest {
    @Autowired
    MemberRepository memberRepository;

    @Autowired
    private MemberService memberService;

    @PersistenceContext
    EntityManager em;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("Auditiong 테스트")
    @Commit
    void auditiongTest() {
        //멤버 객체 생성시 권한과 이메일 설정
        Member newMember = new Member();
        newMember.setName("강산이");
        newMember.setAddress("안산");
        newMember.setEmail("kangsan@gmail.com");
        newMember.setPassword(passwordEncoder.encode("1234"));
        newMember.setRole(Role.ADMIN);
        memberRepository.save(newMember);
        em.flush();
        em.clear();

        Member member = memberRepository.findById(newMember.getId()).orElseThrow(EntityNotFoundException::new);

        log.info("---------------------------------");
        log.info("resister time : " + member.getRegTime());
        log.info("update time : " + member.getUpdateTime());
        log.info("create member : " + member.getCreatedBy());
        log.info("modify member : " + member.getModifiedBy());
        log.info("---------------------------------");

    }
    
}