package kg.manas.skincare.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.manas.skincare.dto.response.ImageResponse;
import kg.manas.skincare.dto.response.SimpleResponse;
import kg.manas.skincare.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/images")
@RequiredArgsConstructor
@Tag(name="Product Images", description = "Управление изображениями товаров")
public class ImageController {

    private final ImageService imageService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value="/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ImageResponse uploadImage(
            @PathVariable Long productId,
            @RequestParam MultipartFile file) {
        return imageService.uploadProductImage(productId, file);
    }

    @GetMapping("/{productId}")
    public ImageResponse getImageByProduct(@PathVariable Long productId) {
        return imageService.getByProductId(productId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{imageId}")
    public SimpleResponse deleteImage(@PathVariable Long imageId) {
        return imageService.deleteImage(imageId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Загрузить фото и получить ссылку")
    public String uploadStandaloneImage(@RequestParam MultipartFile file) {
        return imageService.uploadFile(file);
    }
}