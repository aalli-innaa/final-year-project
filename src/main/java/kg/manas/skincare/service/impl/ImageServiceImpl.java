package kg.manas.skincare.service.impl;

import jakarta.transaction.Transactional;
import kg.manas.skincare.dto.response.ImageResponse;
import kg.manas.skincare.dto.response.SimpleResponse;
import kg.manas.skincare.exceptions.BusinessException;
import kg.manas.skincare.exceptions.ErrorCode;
import kg.manas.skincare.model.Image;
import kg.manas.skincare.model.Product;
import kg.manas.skincare.repository.ImageRepository;
import kg.manas.skincare.repository.ProductRepository;
import kg.manas.skincare.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;
    private final ProductRepository productRepository;

    private static final String BASE_PATH = "storage/products/";

    @Transactional
    @Override
    public ImageResponse uploadProductImage(Long productId, MultipartFile file) {
        if (file == null || file.isEmpty())
            throw new BusinessException(ErrorCode.FILE_REQUIRED);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        // Ищем существующее изображение
        Optional<Image> existingImageOpt = imageRepository.findByProduct_Id(productId);

        try {
            String dirPath = BASE_PATH + productId + "/";
            Files.createDirectories(Paths.get(dirPath));

            // Если фото уже было — удаляем старый файл с диска
            existingImageOpt.ifPresent(img -> deleteFileFromDisk(img.getImageUrl()));

            // Генерируем новое имя и сохраняем
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(dirPath).resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            String imageUrl = "/products/" + productId + "/" + fileName;

            Image image;
            if (existingImageOpt.isPresent()) {
                image = existingImageOpt.get();
                image.setImageUrl(imageUrl);
            } else {
                image = Image.builder()
                        .product(product)
                        .imageUrl(imageUrl)
                        .build();
            }

            imageRepository.save(image);
            log.info("Image updated/created for product {}: {}", productId, imageUrl);

            return mapToResponse(image);

        } catch (IOException e) {
            log.error("Error saving file for product {}", productId, e);
            throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    @Override
    public ImageResponse getByProductId(Long productId) {
        Image image = imageRepository.findByProduct_Id(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.IMAGE_NOT_FOUND));
        return mapToResponse(image);
    }

    @Transactional
    @Override
    public SimpleResponse deleteImage(Long imageId) {
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new BusinessException(ErrorCode.IMAGE_NOT_FOUND));

        deleteFileFromDisk(image.getImageUrl());
        imageRepository.delete(image);

        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Image successfully deleted")
                .build();
    }

    private void deleteFileFromDisk(String imageUrl) {
        try {
            String pathOnDisk = imageUrl.replace("/products/", BASE_PATH);
            Files.deleteIfExists(Paths.get(pathOnDisk));
        } catch (IOException e) {
            log.error("Could not delete physical file: {}", imageUrl, e);
        }
    }

    private ImageResponse mapToResponse(Image image) {
        return ImageResponse.builder()
                .imageId(image.getImageId())
                .productId(image.getProduct().getId())
                .imageUrl(image.getImageUrl())
                .createdAt(image.getCreatedAt())
                .updatedAt(image.getUpdatedAt())
                .build();
    }
}