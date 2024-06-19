package com.shop.controller;


import com.shop.dto.OrderHistDto;
import com.shop.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;
import java.util.Optional;


@Controller
@RequiredArgsConstructor
@Log4j2
public class OrderController {
    private final OrderService orderService;  // 주문 서비스

    @GetMapping(value = {"/orders", "/orders/{page}"})
    public String orderHist(@PathVariable("page") Optional<Integer> page,
                            Principal principal, Model model){
        // 1 페이지 번호와 페이지 사이즈를 설정하여 페이지 요청 객체 생성
        Pageable pageable = PageRequest.of(Boolean.TRUE.equals(page.isPresent()) ? page.get() : 0, 4);

        // 2 주문 이력을 페이지별로 조회
        Page<OrderHistDto> orderHistDtoList =
                orderService.getOrderList(principal.getName(), pageable);

        // 모델에 조회된 주문 이력과 페이지 정보 추가
        model.addAttribute("orders", orderHistDtoList);
        model.addAttribute("page",pageable.getPageNumber());
        model.addAttribute("maxPage",5);

        return "order/orderHist"; // 주문 이력 페이지로 이동
    }

}
