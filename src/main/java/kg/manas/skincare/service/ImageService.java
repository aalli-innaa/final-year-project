package kg.manas.skincare.service;

import kg.manas.skincare.dto.response.ImageResponse;
import kg.manas.skincare.dto.response.SimpleResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    ImageResponse uploadProductImage(Long productId, MultipartFile file);
    ImageResponse getByProductId(Long productId);
    SimpleResponse deleteImage(Long imageId);
    // ImageService.java
    String uploadFile(MultipartFile file); // Просто загрузить файл и получить URL
}