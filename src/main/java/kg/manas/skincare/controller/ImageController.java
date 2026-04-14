package kg.manas.skincare.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import kg.manas.skincare.dto.requests.UpdateImageRequest;
import kg.manas.skincare.dto.response.ImageResponse;
import kg.manas.skincare.dto.response.SimpleResponse;
import kg.manas.skincare.enums.ImageAngle;
import kg.manas.skincare.enums.ImageType;
import kg.manas.skincare.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/images")
@RequiredArgsConstructor
@Tag(name="Image", description = "Image api")
public class ImageController {

//    DATASET — для обучения модели
//    MAIN — основное фото продукта
//    USER_UPLOAD — фото от пользователя

    private final ImageService imageService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value="/dataset/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ImageResponse uploadDatasetImage(
            @PathVariable Long productId,
            @RequestParam MultipartFile file,
            @RequestParam(value = "angle", defaultValue = "UNKNOWN")ImageAngle angle) {
        return imageService.uploadImage(productId, file, ImageType.DATASET ,angle);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value="/main/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ImageResponse uploadMainImage(
            @PathVariable Long productId,
            @RequestParam MultipartFile file,
            @RequestParam(value = "angle", defaultValue = "UNKNOWN")ImageAngle angle) {
        return imageService.uploadImage(productId, file, ImageType.MAIN ,angle);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping(value = "upload/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ImageResponse uploadUserImage(
            @PathVariable Long productId,
            @RequestParam MultipartFile file) {
        return imageService.uploadImage(productId,file, ImageType.USER_UPLOAD, ImageAngle.UNKNOWN);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/dataset/{productId}")
    public List<ImageResponse> getDatasetImagesByProduct(
            @PathVariable Long productId) {
        return imageService.getDatasetImages(productId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{productId}")
    public List<ImageResponse> getAllImagesByProduct(
            @PathVariable Long productId) {
        return imageService.getAllImages(productId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/dataset/{id}")
    public SimpleResponse updateImage(
            @PathVariable Long id,
            @RequestBody UpdateImageRequest request) {
        return imageService.updateImage(id,request);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public SimpleResponse deleteImage(
            @PathVariable Long id) {
        return imageService.deleteImage(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/main/{productId}")
    public ImageResponse getMainImage(
            @PathVariable Long productId){
        return imageService.getMainImage(productId);
    }



}