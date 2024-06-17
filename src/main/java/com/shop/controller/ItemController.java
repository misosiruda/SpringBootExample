
package com.shop.controller;

import com.shop.dto.ItemFormDto;
import com.shop.service.ItemService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@Log4j2
@Controller
public class ItemController {
    private final ItemService itemService;

    private static final String ITEMFORM_STRING = "item/itemForm";

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping(value = "/admin/item/new")
    public String itemForm(Model model) {
        log.info("--------------------itemForm--------------------");
        model.addAttribute("itemFormDto", new ItemFormDto());
        return ITEMFORM_STRING;
    }

    @PostMapping(value = "/admin/item/new")
    public String itemNew(@Valid ItemFormDto itemFormDto, BindingResult bindingResult,
                            Model model, @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList) {
        // 상품 등록시 필수 값이 없다면 다시 상품 등록 페이지로 전환한다.
        if (bindingResult.hasErrors()) {
            return ITEMFORM_STRING;
        }
        // 상품 등록시 첫번째 이미지가 없다면 에러 메시지와 함께 상품 등록 페이지로 전환한다.
        // 상품의 첫 번째 이미지는 메인 페이지에서 보여줄 상품 이미지로 사용하기 위해서 필수 값을 지정한다.
        if (itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null) {
            model.addAttribute("errorMessage", "첫번째 상품 이미지 는 필수 입력 값 입니다.");
            return ITEMFORM_STRING;
        }

        try {  // 상품 저장 로직을 호출한다. 매개변수로 상품 정보와 상품 이미지 정보를 담고 있는 itemImgFileList를 넘겨준다.
            itemService.saveItem(itemFormDto, itemImgFileList);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "상품 등록 중 에러가 발생 하였 습니다.");
            return ITEMFORM_STRING;
        }
        //상품이 정상적으로 등록되었다면  메인페이지로 이동한다.
        return "redirect:/members/";
    }
}
