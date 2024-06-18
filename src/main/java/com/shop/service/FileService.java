package com.shop.service;


import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

@Service
@Log4j2
public class FileService {
    public String uploadFile(String uploadPath, String originalFileName, byte[] fildeDate) throws IOException {
        // UUID 로 파일명 중복 문제를 해결
        UUID uuid = UUID.randomUUID();
        String extensiton = originalFileName.substring(originalFileName.lastIndexOf("."));

        //UUID로 받은 값과 원래 파일의 이름의 확장자를 조합해서 저장될 파일 이름을 만든다.
        String savedFileName = uuid + extensiton;

        String fileuploadFullUrl = uploadPath + "/" + savedFileName;

        // fileOutputStream 클래스는 바이트 단위의 출력을 내보내는 클래스이다.
        // 생성자로 파일이 저장될 위치와 파일의 이름을 넘겨 파일에  쓸 파일 출력 스트림을 만든다.
        try (FileOutputStream fos = new FileOutputStream(fileuploadFullUrl)) {
            fos.write(fildeDate);  // fileData 를 파일 출력 스트림에 입력한다.
        } catch (IOException e) {
            throw new FileNotFoundException();
        }
        return savedFileName;  // 업로드된 파일의 이름을 반환한다.
    }

    public void deleteFile(String fileName) throws Exception {
        // 파일이 저장된 경로를 이용하여 파일 객체를 생성한다.
        File deleteFile = new File(fileName);

        // 해당 파일이 존재하면 파일을 삭제한다.
        if (Boolean.TRUE.equals(deleteFile.exists())) {
            Files.deleteIfExists(deleteFile.toPath());
            log.info("파일을 삭제하였습니다.");
        } else {
            log.info("파일이 존재하지 않습니다.");
        }
    }


}






