package com.shop.service;

import com.shop.dto.OrderDto;
import com.shop.entity.Item;
import com.shop.entity.Member;
import com.shop.entity.Order;
import com.shop.entity.OrderItem;
import com.shop.repository.ItemRepository;
import com.shop.repository.MemberRepository;
import com.shop.repository.OrderRepository;
import groovy.util.logging.Log4j2;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@Log4j2
@RequiredArgsConstructor
public class OrderService {
    private final ItemRepository itemRepository;  //상품 데이터베이스 접근 객체
    private final MemberRepository memberRepository; // 회원 데이터베이스 접근 객체
    private final OrderRepository orderRepository; // 주문 데이터베이스 접근 객체


    //주문을 생성하는 메서드
    // 매개변수 (orderDto 주문 정보를 담고 있는 DTO 객체,  email 주문하는 회원의 이메일, 생성된 주문의 ID)
    public Long order(OrderDto orderDto, String email){
        // 1 주문할 상품을 데이터베이스에서 조회. 존재하지 않으면 예외 발생
        Item item = itemRepository.findById(orderDto.getItemId())
                .orElseThrow(EntityNotFoundException::new);

        // 2 회원 정보를 이메일로 조회
        Member member = memberRepository.findByEmail(email);

        // 주문 상품 리스트를 생성
        List<OrderItem> orderItemList = new ArrayList<>();

        // 3 주문 상품 생성
        OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getCount());

        // 주문 상품 리스트에 추가
        orderItemList.add(orderItem);

        // 4주문 생성
        Order order = Order.createOrder(member, orderItemList);

        // 5 주문을 데이터베이스에 저장
        orderRepository.save(order);

        // 생성된 주문의 ID 반환
        return order.getId();
    }

}
