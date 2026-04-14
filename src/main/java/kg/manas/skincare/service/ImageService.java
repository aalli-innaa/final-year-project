package kg.manas.skincare.service;

import kg.manas.skincare.dto.requests.UpdateImageRequest;
import kg.manas.skincare.dto.response.ImageResponse;
import kg.manas.skincare.dto.response.SimpleResponse;
import kg.manas.skincare.enums.ImageAngle;
import kg.manas.skincare.enums.ImageType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ImageService {
    SimpleResponse updateImage(Long id, UpdateImageRequest request);
    SimpleResponse deleteImage(Long id);
    ImageResponse uploadImage(Long productId, MultipartFile file, ImageType type, ImageAngle angle);
    List<ImageResponse> getDatasetImages(Long productId);
    List<ImageResponse> getAllImages(Long productId);
    ImageResponse getMainImage(Long productId);
}