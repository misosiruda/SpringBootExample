package com.shop.entity;


import com.shop.constant.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "order_id")
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "membmer_id")
    private Member member;  // 한명의 회원은 여러번 주문할 수 있으므로 주문 엔티티 기준으로 다대일 단방향 매핑


    private LocalDateTime orderDate;  //주문일

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;   //주문상태


    //주문상품 엔티티와 일대일 매핑
    //부모 엔티티의 영속성 상태 변화를 자식 엔티티에 모두 전이하는 CasadTypeAll 옵션 설정
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    //OrderItem 엔티티가 order필드를 통해 관계를 관리하므로  OrderItem이 엔티티 주인

    //하나의 주문이 여러개의 주문 상품을 가지므로  List자료형을 사용해서 매핑을한다.
    private List<OrderItem> orderItems = new ArrayList<>();  //OrderItem이 주인



}
