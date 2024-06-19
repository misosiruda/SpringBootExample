package com.shop.service;


import com.shop.dto.CartItemDto;
import com.shop.entity.Cart;
import com.shop.entity.CartItem;
import com.shop.entity.Item;
import com.shop.entity.Member;
import com.shop.repository.CartItemRepository;
import com.shop.repository.CartRepository;
import com.shop.repository.ItemRepository;
import com.shop.repository.MemberRepository;
import groovy.util.logging.Log4j2;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class CartService {
    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    // 장바구니에 상품 추가하는 메서드
    public Long addCart(CartItemDto cartItemDto, String email){
        //1 주어진 상품 ID로 상품을 찾음, 없으면 예외 발생
        Item item = itemRepository.findById(cartItemDto.getItemId())
                .orElseThrow(EntityNotFoundException::new);

        // 2 주어진 이메일로 회원을 찾음
        Member member = memberRepository.findByEmail(email);

        // 3 회원의 장바구니를 찾음
        Cart cart = cartRepository.findByMemberId(member.getId());

        // 4 email(사용자)에게 카트(장바구니)가 없다면 카트를 생성
        if(cart == null){
            cart = Cart.createCart(member); // 회원의 장바구니 생성
            cartRepository.save(cart);  // 생성된 장바구니 저장
        }

        //5 카트안에 같은 상품이 있는지 확인한다.
        CartItem savedCartItem =
                cartItemRepository.findByCartIdAndItemId(cart.getId(), item.getId());

        //카트안에 상품이 있다면 기존 카트(장바구니)에 추가한다.
        if(savedCartItem != null){
            savedCartItem.addCount(cartItemDto.getCount());  // 6 수량 추가
            return savedCartItem.getId();  // 장바구니 항목 ID 반환
        }else{ // 장바구니에 같은 상품이 없으면 새로운 장바구니 항목 생성
            // 7새로운 장바구니 항목 생성
            CartItem cartItem = CartItem.createCartItem(cart, item, cartItemDto.getCount());
            cartItemRepository.save(cartItem);  //8 생성된 장바구니 항목 저장
            return cartItem.getId();  // 장바구니 항목 ID 반환
        }
    }
}
