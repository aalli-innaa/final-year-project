package kg.manas.skincare.service.impl;

import jakarta.transaction.Transactional;
import kg.manas.skincare.dto.requests.UpdateImageRequest;
import kg.manas.skincare.dto.response.ImageResponse;
import kg.manas.skincare.dto.response.SimpleResponse;
import kg.manas.skincare.exceptions.BusinessException;
import kg.manas.skincare.exceptions.ErrorCode;
import kg.manas.skincare.model.Image;
import kg.manas.skincare.model.Product;
import kg.manas.skincare.enums.ImageAngle;
import kg.manas.skincare.enums.ImageType;
import kg.manas.skincare.repository.ImageRepository;
import kg.manas.skincare.repository.ProductRepository;
import kg.manas.skincare.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageServiceImpl implements ImageService {

    private final ProductRepository productRepository;
    private final ImageRepository imageRepository;

    private static final String BASE_PATH = "storage/products/";

    @Override
    public SimpleResponse updateImage(Long id, UpdateImageRequest request) {
        Image image = imageRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.IMAGE_NOT_FOUND));
        try {
            if (request.productId() != null &&
                    !request.productId().equals(image.getProduct().getId())) {

                Product newProduct = productRepository.findById(request.productId())
                        .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

                String oldPathStr = image.getImageUrl().replace("/products/", BASE_PATH);
                Path oldPath = Paths.get(oldPathStr);

                String subFolder = (image.getType() == ImageType.DATASET) ? "dataset/" : "user_upload/";
                String newDirPath = BASE_PATH + subFolder + newProduct.getId() + "/";
                Path newDir = Paths.get(newDirPath);
                Files.createDirectories(newDir);

                String fileName = oldPath.getFileName().toString();
                Path newPath = newDir.resolve(fileName);

                Files.move(oldPath, newPath, StandardCopyOption.REPLACE_EXISTING);

                String newUrl = "/products/" + subFolder + newProduct.getId() + "/" + fileName;

                image.setProduct(newProduct);
                image.setImageUrl(newUrl);
                log.info("Moving image from {} to {}", oldPath, newPath);
            }

            if (request.angle() != null) {
                image.setAngle(request.angle());
            }

            imageRepository.save(image);
            log.info("Updated image with id={}", id);

            return SimpleResponse.builder()
                    .httpStatus(HttpStatus.OK)
                    .message("Image successfully updated!")
                    .build();

        } catch (IOException e) {
            log.error("Error updating image id={}", id);
            throw new BusinessException(ErrorCode.FILE_UPDATE_FAILED);
        }
    }

    @Transactional
    @Override
    public SimpleResponse deleteImage(Long id) {
        Image image = imageRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.IMAGE_NOT_FOUND));

        try {
            String filePathStr = image.getImageUrl().replace("/products/", BASE_PATH);
            Path filePath = Paths.get(filePathStr);
            Files.deleteIfExists(filePath);
            log.info("Deleted file from disk: {}", filePath);

        } catch (IOException e) {
            log.error("Error deleting file for imageId={}", id, e);
        }

        imageRepository.delete(image);
        log.info("Deleted image from DB id={}", id);

        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Image successfully deleted!")
                .build();
    }

    @Transactional
    @Override
    public ImageResponse uploadImage(Long productId,
                                     MultipartFile file,
                                     ImageType type,
                                     ImageAngle angle) {

        if (productId == null) throw new BusinessException(ErrorCode.PRODUCT_REQUIRED);
        if (file == null || file.isEmpty()) throw new BusinessException(ErrorCode.FILE_REQUIRED);
        if (type == null) throw new BusinessException(ErrorCode.TYPE_REQUIRED);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        try {
            String subFolder = switch (type) {
                case MAIN -> "main/";
                case USER_UPLOAD -> "user_upload/";
                default -> "dataset/";
            };

            String dirPath = BASE_PATH + subFolder + productId + "/";
            Path directory = Paths.get(dirPath);
            Files.createDirectories(directory);

            if (type == ImageType.MAIN) {
                deleteOldMainImages(productId);
            }

            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = directory.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            String imageUrl = "/products/" + subFolder + productId + "/" + fileName;

            Image image = Image.builder()
                    .product(product)
                    .imageUrl(imageUrl)
                    .type(type)
                    .angle(angle != null ? angle : ImageAngle.UNKNOWN)
                    .build();

            imageRepository.save(image);

            log.info("Uploaded {} image for productId={}, file={}", image.getType(), productId, imageUrl);

            return mapToImageResponse(image);

        } catch (IOException e) {
            log.error("Error uploading image for productId={}", productId, e);
            throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    private void deleteOldMainImages(Long productId) {
        imageRepository.findByProduct_IdAndType(productId, ImageType.MAIN)
                .forEach(old -> {
                    try {
                        String filePathStr = old.getImageUrl().replace("/products/", BASE_PATH);
                        Path filePath = Paths.get(filePathStr);
                        Files.deleteIfExists(filePath);
                        imageRepository.delete(old);
                    } catch (IOException e) {
                        log.error("Error deleting old MAIN image for productId={}", productId, e);
                    }
                });
    }

    @Override
    public List<ImageResponse> getDatasetImages(Long productId) {
        if (productId == null) throw new BusinessException(ErrorCode.PRODUCT_REQUIRED);

        productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        log.info("Fetching dataset images for productId={}", productId);

        return imageRepository
                .findByProduct_IdAndType(productId, ImageType.DATASET)
                .stream()
                .map(this::mapToImageResponse)
                .toList();
    }

    @Override
    public List<ImageResponse> getAllImages(Long productId) {
        if (productId == null) throw new BusinessException(ErrorCode.PRODUCT_REQUIRED);

        productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        log.info("Fetching all images for productId={}", productId);

        return imageRepository.findByProduct_Id(productId).stream()
                .map(this::mapToImageResponse)
                .toList();
    }

    @Override
    public ImageResponse getMainImage(Long productId) {
        if (productId == null) throw new BusinessException(ErrorCode.PRODUCT_REQUIRED);

        productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        log.info("Getting main image for productId={}", productId);

        Image image = imageRepository
                .findFirstByProduct_IdAndType(productId, ImageType.MAIN)
                .orElseThrow(() -> new BusinessException(ErrorCode.IMAGE_NOT_FOUND));

        return mapToImageResponse(image);
    }

    private ImageResponse mapToImageResponse(Image image) {
        return ImageResponse.builder()
                .imageId(image.getImageId())
                .productId(image.getProduct().getId())
                .imageUrl(image.getImageUrl())
                .type(image.getType())
                .angle(image.getAngle())
                .createdAt(image.getCreatedAt())
                .updatedAt(image.getUpdatedAt())
                .build();
    }
}