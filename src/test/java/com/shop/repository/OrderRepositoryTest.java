package com.shop.repository;

import com.shop.constant.ItemSellStatus;
import com.shop.entity.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class OrderRepositoryTest {


    private static final Logger log = LoggerFactory.getLogger(OrderRepositoryTest.class);
    @Autowired
    OrderRepository orderRepository;

    @Autowired
    ItemRepository itemRepository;

    @PersistenceContext
    EntityManager em;


    @Autowired
    private MemberRepository memberRepository;


    @Autowired
    OrderItemRepository orderItemRepository;


    public Item createItem() {
        Item item = new Item();
        item.setItemNm("테스트 상품");
        item.setPrice(10000);
        item.setItemDetail("상세설명");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
        item.setRegTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());
        return item;
    }


    @Test
    @DisplayName("영속성 전이 테스트")
    //@Commit
    void cascadeTest() {
        Order order = new Order();

        for (int i = 0; i < 3; i++) {
            Item item = this.createItem();
            itemRepository.save(item);
            OrderItem orderItem = new OrderItem();
            orderItem.setItem(item);
            orderItem.setCount(10);
            orderItem.setOrderPrice(1000);

            //아직 영속성 컨텍스트에 저장되지 않은 orderItem 엔티티를 order엔티티에 담아준다.
            order.getOrderItems().add(orderItem);
        }

        // order 엔티티를 저장하면서 강제로  flush를 호출하여
        // 영속성 컨테스트에 있는 객체들을 데이터베이스에 반영한다.
        orderRepository.saveAndFlush(order);

        // 영속성 컨테스트의 상태를 초기화한다.
        em.clear();


        Order savedOrder = orderRepository.findById(order.getId())
                .orElseThrow(EntityNotFoundException::new);
        assertEquals(3, savedOrder.getOrderItems().size());
    }


    // 주문 데이터를 생성해서 저장하는 메서드를 만든다.
    public Order createOrder() {
        Order order = new Order();

        for (int i = 0; i < 3; i++) {
            Item item = this.createItem();
            itemRepository.save(item);
            OrderItem orderItem = new OrderItem();
            orderItem.setItem(item);
            orderItem.setCount(10);
            orderItem.setOrderPrice(1000);
            orderItem.setOrder(order);
            order.getOrderItems().add(orderItem);
        }

        Member member = new Member();
        memberRepository.save(member);

        order.setMember(member);
        orderRepository.save(order);
        return order;

    }


    @Test
    @DisplayName("고아객체 제거 테스트")
    //@Commit
    void orphanRemovalTest() {
        Order order = this.createOrder();
        // order 엔티티에서 관리하고 있는 orderItem 리스트의 0번째 인덱스 제거
        assertAll((Executable) order.getOrderItems().remove(0));
        em.flush();
        log.info("delete");
    }


    @Test
    @DisplayName("지연 로딩 테스트")
    @Commit  //DB에 반영된걸 확인하기위해
    void lazyLoadingTest() {
        //기존에 만들었던 주문 생성 메서드를 이용하여 주문 데이터를 저장한다.
        Order order = this.createOrder();
        Long orderItemId = order.getOrderItems().get(0).getId();
        em.flush();
        em.clear();

        // 영속성 컨텍스트의 상태 초기화 후 order 엔티티에 저장했던 주문 상품 아이디를 이용하여
        // orderItem을 데이터베이스에서 조회한다.
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(EntityNotFoundException::new);


        // orderItem 엔티티에 있는 order 객체의 클래스를 출력한다.
        log.info("====================================");
        // 지연로딩후 OrderItem에 매핑된 order 클래스 출력결과
        log.info("Order class:{}", orderItem.getOrder().getClass());

        // 프록시 객체가 실제 사용시점인 조회 쿼리문에서 실행
        assertAll(String.valueOf(orderItem.getOrder().getOrderDate()));
        log.info("====================================");

    }
}