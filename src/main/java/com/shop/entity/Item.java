package com.shop.entity;

import com.shop.constant.ItemSellStatus;
import com.shop.dto.ItemFormDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name="item")
@Getter
@Setter
@ToString
public class Item extends BaseEntity{
    @Id
    @Column(name="item_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;       //상품 코드

    @Column(nullable = false, length = 50)
    private String itemNm; //상품명

    @Column(name = "price", nullable = false)
    private int price; //가격

    @Column(nullable = false)
    private int stockNumber; //재고수량

    @Lob
    @Column(nullable = false)
    private String itemDetail; //상품 상세 설명

    @Enumerated(EnumType.STRING)
    private ItemSellStatus itemSellStatus; //상품 판매 상태

    
    //Item 엔티티의 필드 값을 ItemFormDto 객체의 값으로 업데이트
    public void updateItem(ItemFormDto itemFormDto){
        this.itemNm = itemFormDto.getItemNm();  //DTO에서 가져온 상품명을 엔티티의 상품명으로 설정
        this.price = itemFormDto.getPrice();  //DTO에서 가져온 가격을 엔티티의 가격으로 설정
        this.stockNumber = itemFormDto.getStockNumber();  // 재고 수량으로 설정
        this.itemDetail = itemFormDto.getItemDetail();   // 상세 설명으로 설정
        this.itemSellStatus = itemFormDto.getItemSellStatus();   // 판매 상태로 설정
    }



}
