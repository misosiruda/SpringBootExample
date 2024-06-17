package com.shop.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.constant.ItemSellStatus;
import com.shop.dto.ItemSearchDto;

import com.shop.entity.Item;
import com.shop.entity.QItem;

import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

@Log4j2
//1 ItemRepositoryCustom 상속받는다.
public class ItemRepositoryCustomImpl implements ItemRepositoryCustom {

    //2 동적으로 쿼리를 생성하기 위해  JPAQueryFactory 클래스를 사용한다.
    private final JPAQueryFactory queryFactory;

    //3 JPAQueryFactory의 생성자로 EntityManager객체를 넣어준다.
    public ItemRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    //4 상품 판매 상태 조건이 전체 null일 경우 null을 리턴한다.
    //결과값이 null이면 where절에서 해당 조건은 무시된다.
    // 상품 판매 상태 조건이 null이 아니라 판매중 or 품절 상태라면 해당 조건의 상품만 조회된다.
    private BooleanExpression searchSellStatusEq(ItemSellStatus searchSellStatus) {
        //5 searchDateType의 값에 따라서 dateTime 의 값을 해당시간 이후로 등록된 상품만 조회한다.
        return searchSellStatus == null ? null : QItem.item.itemSellStatus.eq(searchSellStatus);
    }

    //5 searchDateType 값이 1m인 경우 dateTime의 시간을 한달 전으로 세팅 후 최근 한달 동안 등록된 상품만 조회하도록 조건값을 반환한다.
    private BooleanExpression regDtsAfter(String searchDateType) {

        LocalDateTime dateTime = LocalDateTime.now();

        if (Boolean.TRUE.equals(StringUtils.equals("all", searchDateType)) || searchDateType == null) {
            return null;
        } else if (Boolean.TRUE.equals(StringUtils.equals("1d", searchDateType))) {
            dateTime = dateTime.minusDays(1);
        } else if (Boolean.TRUE.equals(StringUtils.equals("1w", searchDateType))) {
            dateTime = dateTime.minusWeeks(1);
        } else if (Boolean.TRUE.equals(StringUtils.equals("1m", searchDateType))) {
            dateTime = dateTime.minusMonths(1);
        } else if (Boolean.TRUE.equals(StringUtils.equals("6m", searchDateType))) {
            dateTime = dateTime.minusMonths(6);
        }

        return QItem.item.regTime.after(dateTime);
    }

    //6 searchBy의 값에 따라서 상품명에 검색어를 포함하고 있는 상품 또는 상품 생성자의 아이디에
    // 검색어를 포함하고 있는 상품을 조회하도록 조건값을 반환한다.
    private BooleanExpression searchByLike(String searchBy, String searchQuery) {

        if (Boolean.TRUE.equals(StringUtils.equals("itemNm", searchBy))) {
            return QItem.item.itemNm.like("%" + searchQuery + "%");
        } else if (Boolean.TRUE.equals(StringUtils.equals("createdBy", searchBy))) {
            return QItem.item.createdBy.like("%" + searchQuery + "%");
        }

        return null;
    }

    /*
        1. itemSearchDto : 검색조건
        2. pageable : 페이지정보(페이지 번호, 페이지크기,정렬정보)
        3. regDtsAfter(itemSearchDto.getSearchDateType()) : 등록 날짜 필터링 조건
        4. searchSellStatusEq(itemSearchDto.getSearchSellStatus()) : 판매 상태 필터링 조건
        5. searchByLike(itemSearchDto.getSearchBy(), itemSearchDto.getSearchQuery())
            : 검색어에 따른 필터링 조건
        6. orderBy(QItem.item.id.desc()): 아이템 ID를 기준으로 내림차순 정렬
        7. offset(pageable.getOffset()): 조회할 데이터의 시작 위치를 설정
        8. limit(pageable.getPageSize()): 한 번에 조회할 데이터의 최대 개수를 설정
        9. fetch(): 최종적으로 쿼리를 실행하여 결과 리스트
        10. queryFactory.select(Wildcard.count).from(QItem.item): 전체 아이템 수를 세기 위해 count
        11. fetchOne(): 단일 결과를 반환, 여기서는 총 아이템 수
        12. new PageImpl<>(content, pageable, total): PageImpl 객체를 생성
            -> content: 조회된 아이템 리스트
            -> pageable: 페이징 정보
            -> total: 전체 아이템 수
     */
    @Override
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        //7 queryFactory를 이용해서 쿼리를 생성한다.
        List<Item> content = queryFactory
                .selectFrom(QItem.item)
                .where(regDtsAfter(itemSearchDto.getSearchDateType()),
                        searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
                        searchByLike(itemSearchDto.getSearchBy(),
                                itemSearchDto.getSearchQuery()))
                .orderBy(QItem.item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();


        Long total = queryFactory.select(Wildcard.count).from(QItem.item)
                .where(regDtsAfter(itemSearchDto.getSearchDateType()),
                        searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
                        searchByLike(itemSearchDto.getSearchBy(), itemSearchDto.getSearchQuery()))
                .fetchOne();
        //8 조회한 데이터를 Page 클래스의 구현체인 PageImpl 객체로 반환한다.
        if (total != null) {
            return new PageImpl<>(content, pageable, total);
        } else {
            // Handle the case when total is null
            // For example, throw an exception or return an empty page
            throw new IllegalArgumentException("Total count is null");
        }
    }
}