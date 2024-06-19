package com.shop.controller;

import com.shop.dto.CartDetailDto;
import com.shop.service.CartService;
import groovy.util.logging.Log4j2;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
public class CartController {

    private final CartService cartService;

    @GetMapping("/cart")
    public String orderHist(Principal principal, Model model){
        // 1현재 로그인한 사용자의 이메일 정보를 이용하여 장바구니에 담겨있는 상품 정보를 조회한다.
        List<CartDetailDto> cartDetailDtoList = cartService.getCartList(principal.getName());

        // 2 조회한 장바구니 상품 정보를 뷰로 전달한다.
        model.addAttribute("cartItems", cartDetailDtoList);
        return "cart/cartList"; // 장바구니 목록 페이지로 이동
    }

}