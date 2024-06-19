package com.shop.controller;


import com.shop.dto.ItemFormDto;
import com.shop.dto.ItemSearchDto;
import com.shop.entity.Item;
import com.shop.service.ItemService;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@Log4j2
public class ItemController {

    public static final String ITEM_FORM_DTO_STRING = "itemFormDto";
    private static final String ITEM_FORM_PAGE_STRING = "item/itemForm";
    private static final String ERROR_MESSAGE_STRING = "errorMessage";
    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }


    @GetMapping(value = "/admin/item/new")
    public String itemForm(Model model) {
        log.info("----------- itemForm  ----------");
        model.addAttribute(ITEM_FORM_DTO_STRING, new ItemFormDto());
        return ITEM_FORM_PAGE_STRING;
    }

    @PostMapping(value = "/admin/item/new")
    public String itemNew(@Valid ItemFormDto itemFormDto, BindingResult bindingResult,
                          Model model, @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList) {

        // 상품 등록시 필수 값이 없다면 다시 상품 등록 페이지로 전환
        if (bindingResult.hasErrors()) {
            return ITEM_FORM_PAGE_STRING;
        }

        // 상품등록시 첫번째 이미지가 없다면 에러메시지와 함께 상품등록 페이지로 전환
        // 상품의 첫번째 이미지는 필수 값으로 지정
        if (itemImgFileList.isEmpty() && itemFormDto.getId() == null) {
            model.addAttribute(ERROR_MESSAGE_STRING, "첫번째 상품 이미지는 필수 입력 값입니다.");
            return ITEM_FORM_PAGE_STRING;
        }

        try {  // 상품 저장 로직호출, 매개변수로 상품정보와 상품 이미지 정보를 담고 있는 itemImgFileList 를 넘겨준다.
            itemService.saveItem(itemFormDto, itemImgFileList);
        } catch (Exception e) {
            model.addAttribute("errormessage", "상품 등록 중 에러가 발생하였습니다.");
            return ITEM_FORM_PAGE_STRING;
        }
        // 상품이 정상적으로 등록되었다면 메인 페이지로 이동한다.
        return "redirect:/";
    }

    @GetMapping(value = "/admin/item/{itemId}")
    public String itemDetail(@PathVariable("itemId") Long itemId, Model model) {
        try {

            //조회한 상품 데이터를 모델에 담아서 뷰에 전달
            ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
            model.addAttribute(ITEM_FORM_DTO_STRING, itemFormDto);
        } catch (EntityNotFoundException e) {
            //상품 엔티티가 존재하지 않은 경우 에러메시지를 담아 상품등록 페이지로 이동
            model.addAttribute(ERROR_MESSAGE_STRING, "존재하지 않은 상품입니다.");
            model.addAttribute(ITEM_FORM_DTO_STRING, new ItemFormDto());
        }
        return ITEM_FORM_PAGE_STRING;
    }


    //상품 수정을 처리하는 POST 요청 핸들러
    @PostMapping(value = "/admin/item/{itemId}")
    public String itemUpdate(@Valid ItemFormDto itemFormDto, BindingResult bindingResult,
                             @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList, Model model, @PathVariable("itemId") Long itemId) {
        log.info("Item update : {}", itemId);
        // 유효성 검사를 수행하가 에러가 있으면 item/itemForm 뷰로이동
        if (bindingResult.hasErrors()) {
            return ITEM_FORM_PAGE_STRING;
        }

        // 첫번째 상품 이미지가 비어있고, 상품 ID가 널이 경우 에러메시지를 표히사고 item/itemForm 뷰로 이동
        if (itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null) {
            model.addAttribute(ERROR_MESSAGE_STRING, "첫번째 상품 이미지는 필수 입력 값입니다.");
            return ITEM_FORM_PAGE_STRING;
        }

        try {  // 상품 업데이트
            itemService.updateItem(itemFormDto, itemImgFileList);  // 상품 수정 로직을 호출
        } catch (Exception e) {
            // 상품 수정 중에 에러가 발행하면 에러 메시지를 표시하고 item/itemForm 뷰로 이동
            model.addAttribute(ERROR_MESSAGE_STRING, "상품 수정 중 에러가 발생하였습니다.");
            return ITEM_FORM_PAGE_STRING;

        }
        // 모든 작업이 성공하면 itemForm 리다이렉트
        return "redirect:/";
    }


    // 1 URL 패턴에 따라 아이템 관리 페이지를 매핑
    @GetMapping(value = {"/admin/items", "/admin/items/{page}"})
    public String itemManage(ItemSearchDto itemSearchDto, @PathVariable("page") Optional<Integer> page, Model model) {

        // 2 페이지 정보 설정: 요청된 페이지가 있으면 해당 페이지를 사용하고, 없으면 첫 번째 페이지를 사용 (한 페이지에 3개의 항목)
        Pageable pageable = PageRequest.of(Boolean.TRUE.equals(page.isPresent()) ? page.get() : 0, 3);

        // 3 검색 조건과 페이징 정보를 기반으로  Page<Item>아이템 페이지를 가져옴
        Page<Item> items = itemService.getAdminItemPage(itemSearchDto, pageable);

        // 모델에 아이템 목록, 검색 조건 및 최대 페이지 수를 추가
        //4 조회한 상품 데이터 및 페이징 정보를 뷰에 전달
        model.addAttribute("items", items);

        //5 페이지 전환시 기존 검색 조건을 유지한 채 이동할 수 있도록 뷰에 다시 전달
        model.addAttribute("itemSearchDto", itemSearchDto);

        //6 상품 관리 메뉴 하단에 보여줄 페이지 번호의 최대 개수 , 5로 설정했으므로 최대 5개의 이동할 페이지번호만 보여준다.
        model.addAttribute("maxPage", 5);

        // 아이템 관리 뷰를 반환
        return "item/itemMng";
    }

    @GetMapping(value = "/item/{itemId}")
    //아이템 상세 정보 페이지를 반환하는 메서드
    public String itemDtl(Model model, @PathVariable("itemId") Long itemId){
        // 서비스에서 아이템 상세 정보를 가져옴
        ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
        // 모델에 아이템 정보를 추가
        model.addAttribute("item", itemFormDto);
        // 아이템 상세 정보 페이지 뷰를 반환
        return "item/itemDtl";
    }

}















