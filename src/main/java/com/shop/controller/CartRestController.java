package com.shop.controller;

import com.shop.dto.CartItemDto;
import com.shop.service.CartService;
import groovy.util.logging.Log4j2;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Log4j2
public class CartRestController {

    private final CartService cartService;


    // 장바구니에 상품 추가하는 POST 요청 처리 메서드
    @PostMapping(value = "/cart")
    //매개변수 (클라이언트가 보낸 장바구니 항목 데이터, 유효성 검사 결과, 현재 로그인한 사용자 정보)
    public ResponseEntity<String> order(@RequestBody @Valid CartItemDto cartItemDto, BindingResult bindingResult, Principal principal) {
        // 1 유효성 검사 결과 오류가 있으면 오류 응답 반환
        if (bindingResult.hasErrors()) {
            StringBuilder sb = new StringBuilder();
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();

            for (FieldError fieldError : fieldErrors) {
                sb.append(fieldError.getDefaultMessage());
            }

            return new ResponseEntity<>(sb.toString(), HttpStatus.BAD_REQUEST);
        }

        // 2 현재 로그인한 사용자의 이메일 가져오기
        String email = principal.getName();

        Long cartItemId;

        try {
            // 3 장바구니에 상품 추가하고, 장바구니 항목의 ID 반환
            cartItemId = cartService.addCart(cartItemDto, email);
        } catch (Exception e) {
            // 예외 발생 시 예외 메시지와 함께 오류 응답 반환
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        // 4 성공적으로 처리된 경우 장바구니 항목의 ID 반환
        return new ResponseEntity<>(cartItemId.toString(), HttpStatus.OK);
    }
    //1 PATCH 요청은 해당 ID를 가진 장바구니 아이템을 수정하는 데 사용
    @PatchMapping(value = "/cartItem/{cartItemId}")
    public ResponseEntity<String> updateCartItem(@PathVariable("cartItemId") Long cartItemId, int count, Principal principal){

        // 2 수량이 0 이하인 경우 에러 메시지와 함께 BAD_REQUEST 상태 반환
        if(count <= 0){
            return new ResponseEntity<>("최소 1개 이상 담아주세요", HttpStatus.BAD_REQUEST);
            // 사용자 권한 검증 실패 시 에러 메시지와 함께 FORBIDDEN 상태 반환
        }//3 수정권한 체크
        else if(cartService.validateCartItem(cartItemId, principal.getName())){
            return new ResponseEntity<>("수정 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }
        // 4 장바구니 상품 개수 업데이트
        cartService.updateCartItemCount(cartItemId, count);

        // 업데이트된 항목의 ID와 함께 OK 상태 반환
        return new ResponseEntity<>(cartItemId.toString(), HttpStatus.OK);
    }

    // 1 장바구니 항목 삭제
    @DeleteMapping(value = "/cartItem/{cartItemId}")
    public ResponseEntity<String> deleteCartItem(@PathVariable("cartItemId") Long cartItemId, Principal principal){
        // 2 사용자 권한 검증 실패 시 에러 메시지와 함께 FORBIDDEN 상태 반환
        if(cartService.validateCartItem(cartItemId, principal.getName())){
            return new ResponseEntity<>("수정 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }

        // 3 장바구니 항목 삭제
        cartService.deleteCartItem(cartItemId);

        // 삭제된 항목의 ID와 함께 OK 상태 반환
        return new ResponseEntity<>(cartItemId.toString(), HttpStatus.OK);
    }
}