package com.shop.entity;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class OrderItem extends BaseEntity{

    @Id
    @GeneratedValue
    @Column(name="order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;
    //한번의 주문에 여러개의 상품을 주문할 수 있다.
    // 주문상품 엔티티를 기준으로 다대일 단방향 매핑 설정
    
    private int orderPrice;  //주문가격

    private int count; //수량



}
