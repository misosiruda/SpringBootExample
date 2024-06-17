package com.shop.service;

import com.shop.dto.ItemFormDto;
import com.shop.entity.Item;
import com.shop.entity.ItemImg;
import com.shop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {
    //상품 정보를 데이터베이스에 저장하고 조회하는 데 사용
    private final ItemRepository itemRepository;

    //이미지 파일을 처리하고 저장하는 데 사용
    private final ItemImgService itemImgService;

    /**
     * itemFormDto: 상품 등록 폼에서 입력받은 데이터를 담고 있는 DTO(Data Transfer Object).
     * itemImgFileList: 클라이언트로부터 업로드된 이미지 파일들의 리스트.
     * throws Exception: 파일 업로드 및 데이터베이스 저장 과정에서 발생할 수 있는 예외를 호출자에게 전달
     */

    public Long saveItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception{

        // 상품 등록
        // 상품 등록폼으로부터 입력받은 데이터를 이용하여 item객체를 생성
        Item item = itemFormDto.createItem();
        itemRepository.save(item);// 상품데이터를 저장

        //이미지 등록
        for(int i=0;i<itemImgFileList.size();i++){
            ItemImg itemImg = new ItemImg();
            itemImg.setItem(item);

            //첫번째 이미지일 경우 대표이미지 여부 Y로 세팅 나머지 상품은 N로 설정
            if(i == 0)
                itemImg.setRepimgYn("Y");
            else
                itemImg.setRepimgYn("N");
            // 상품이미지 정보 저장(각 이미지 파일을 저장하고 관련 정보를 데이터베이스에 저장)
            itemImgService.saveItemImg(itemImg, itemImgFileList.get(i));
        }

        //저장된 상품의 ID를 반환
        return item.getId();
    }

}