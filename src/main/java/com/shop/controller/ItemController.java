
package com.shop.controller;

import com.shop.dto.ItemFormDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ItemController {
    private static final Logger log = LoggerFactory.getLogger(ItemController.class);

    @GetMapping(value = "/admin/item/new")
    public String itemForm(Model model){
        log.info("--------------------itemForm--------------------");
        model.addAttribute("itemFormDto", new ItemFormDto());
        return "item/itemForm";
    }
}
