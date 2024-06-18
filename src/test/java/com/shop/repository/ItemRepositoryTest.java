package com.shop.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.constant.ItemSellStatus;
import com.shop.entity.Item;
import com.shop.entity.QItem;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Log4j2
class ItemRepositoryTest {
    @Autowired
    ItemRepository itemRepository;

    @PersistenceContext
    EntityManager em;


    @Test
    @DisplayName("상품 저장 테스트")
    void createItemTest(){
        Item item = new Item();
        item.setItemNm("테스트 상품");
        item.setPrice(10000);
        item.setItemDetail("테스트 상품 상세 설명");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
        item.setRegTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());
        Item savedItem = itemRepository.save(item);
        log.info(savedItem.toString());
    }

    @Test
    void oneTest() {
        // Optional은 null이냐? 아니냐?
        // Optional은 null을 포함하지 않는다. 조회 결과가 있을 경우에는 해당 객체를 감싸고,
        // 결과가 없을 경우에는 빈 상태를 나타내는 컨테이너이다.
        // Optional 객체를 사용하여 조회한 결과를 안전하게 처리할 수 있다.
        // Optional의 orElseThrow 메소드를 사용하여 조회 결과가 없을 경우에는 예외를 발생시킬 수 있다.
        Optional<Item> result = itemRepository.findById(1L);
        Item item = result.orElseThrow();
        log.info(item);
    }


    void createItemList(){
        for(int i=1;i<=10;i++){
            Item item = new Item();
            item.setItemNm("테스트 상품" + i);
            item.setPrice(10000 + i);
            item.setItemDetail("테스트 상품 상세 설명" + i);
            item.setItemSellStatus(ItemSellStatus.SELL);
            item.setStockNumber(100);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            Item savedItem = itemRepository.save(item);
            log.info(savedItem);
        }
    }

    @Test
    @DisplayName("상품명 조회 테스트")
    void findByItemNmTest(){
        this.createItemList();
        List<Item> itemList = itemRepository.findByItemNm("테스트 상품1");
        for(Item item : itemList){
            log.info(item.toString());
        }
    }


    @Test
    @DisplayName("가격이 10005보다 큰 값만 추출")
    void testPrice(){
        List<Item> items = itemRepository.findByPriceGreaterThan(10005);

        for(Item item : items){
            log.info(item.toString());
        }
    }


    @Test
    @DisplayName("상품평 or  상품상세설명 테스트")
    void findByItemOrItemDetailTest(){
        List<Item> itemList  = itemRepository.findByItemNmOrItemDetail("테스트상품1", "테스트 상품 상세 설명 5");
        for(Item item : itemList){
            log.info(item.toString());
        }
    }

    @Test
    @DisplayName("가격이 특정가격보다 작은상품 조회 테스트")
    void findByPriceLessThanTest(){
        List<Item> itemList = itemRepository.findByPriceLessThan(10005);
        for(Item item : itemList){
            log.info(item.toString());
        }
    }

    @Test
    @DisplayName("가격 내림차순 조회 테스트")
    void findByPriceLessThanOrderByPriceDesc(){
        List<Item> itemList = itemRepository.findByPriceLessThanOrderByPriceDesc(10005);
        for(Item item : itemList){
            log.info(item.toString());
        }
    }

    @Test
    @DisplayName("@Query 설명 내림차순 조회 테스트")
    void findByItemDetailOrderByPriceDesc(){
        List<Item> lists = itemRepository.findByItemDetail("설명");
        log.info(lists);
    }

    @Test
    @DisplayName("nativeQuery 속성을 이용한 상품 조회 테스트")
    void findByItemDetailByNative(){
        List<Item> itemList = itemRepository.findByItemDetailByNative("테스트 상품 상세 설명");
        for(Item item : itemList){
            log.info(item.toString());
        }
    }

    @Test
    @DisplayName("Querydsl 조회 테스트1")
    void queryDslTest(){

        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QItem qItem = QItem.item;
        JPAQuery<Item> query  = queryFactory.selectFrom(qItem)
                .where(qItem.itemSellStatus.eq(ItemSellStatus.SELL))
                .where(qItem.itemDetail.like("%" + "테스트 상품 상세 설명" + "%"))
                .orderBy(qItem.price.desc());
        List<Item> itemList = query.fetch();

        for(Item item : itemList){
            log.info(item.toString());
        }
    }

    // 상품 데이터 생성 및 저장
    void createItemList2(){
        for(int i=1;i<=5;i++){
            Item item = new Item();
            item.setItemNm("테스트 상품" + i);
            item.setPrice(10000 + i);
            item.setItemDetail("테스트 상품 상세 설명" + i);
            item.setItemSellStatus(ItemSellStatus.SELL);
            item.setStockNumber(100);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            itemRepository.save(item);
        }
        // 품절 상품 데이터 생성 및 저장
        for(int i=6;i<=10;i++){
            Item item = new Item();
            item.setItemNm("테스트 상품" + i);
            item.setPrice(10000 + i);
            item.setItemDetail("테스트 상품 상세 설명" + i);
            item.setItemSellStatus(ItemSellStatus.SOLD_OUT);
            item.setStockNumber(0);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            itemRepository.save(item);
        }
    }


    @Test
    @DisplayName("상품 Querydsl 조회 테스트 2")
    void queryDslTest2(){
        // 테스트용 상품 데이터 생성
        this.createItemList2();

        // BooleanBuilder를 사용하여 동적 쿼리 생성
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QItem item = QItem.item;
        String itemDetail = "테스트 상품 상세 설명";
        int price = 10003;
        String itemSellStat = "SELL";

        // 상세 설명이 특정 문자열을 포함하고,
        // 가격이 특정 가격보다 크며,
        // 판매 상태가 판매 중인 상품을 검색하는 조건 추가
        booleanBuilder.and(item.itemDetail.like("%" + itemDetail + "%"));
        booleanBuilder.and(item.price.gt(price));
        log.info(ItemSellStatus.SELL);
        if(StringUtils.equals(itemSellStat, ItemSellStatus.SELL)){
            booleanBuilder.and(item.itemSellStatus.eq(ItemSellStatus.SELL));
        }

        // 페이징 처리
        Pageable pageable = (Pageable) PageRequest.of(0, 5);
        Page<Item> itemPagingResult = itemRepository.findAll(booleanBuilder, pageable);
        log.info("total elements : {}", itemPagingResult.getTotalElements());

        // 조회된 상품 목록 출력
        List<Item> resultItemList = itemPagingResult.getContent();
        for(Item resultItem: resultItemList){
            log.info(resultItem.toString());
        }
    }
}