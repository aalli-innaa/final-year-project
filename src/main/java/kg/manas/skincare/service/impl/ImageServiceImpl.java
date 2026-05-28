package kg.manas.skincare.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
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
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;
    private final ProductRepository productRepository;
    private final Cloudinary cloudinary;

    @Transactional
    @Override
    public ImageResponse uploadProductImage(Long productId, MultipartFile file) {
        if (file == null || file.isEmpty())
            throw new BusinessException(ErrorCode.FILE_REQUIRED);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        try {
            // 1. Если фото уже было — удаляем старое из Cloudinary
            Optional<Image> existingImageOpt = imageRepository.findByProduct_Id(productId);
            existingImageOpt.ifPresent(img -> deleteFromCloudinary(img.getPublicId()));

            // 2. Загружаем в Cloudinary в папку products/{productId}/
            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap("folder", "products/" + productId)
            );

            String imageUrl  = (String) uploadResult.get("secure_url"); // https://res.cloudinary.com/...
            String publicId  = (String) uploadResult.get("public_id");  // нужен для удаления

            // 3. Синхронизируем URL в таблице products
            product.setImageUrl(imageUrl);
            productRepository.save(product);

            // 4. Обновляем или создаём запись в таблице images
            Image image;
            if (existingImageOpt.isPresent()) {
                image = existingImageOpt.get();
                image.setImageUrl(imageUrl);
                image.setPublicId(publicId);
            } else {
                image = Image.builder()
                        .product(product)
                        .imageUrl(imageUrl)
                        .publicId(publicId)
                        .build();
            }

            imageRepository.save(image);
            log.info("Image uploaded to Cloudinary for product {}: {}", productId, imageUrl);

            return mapToResponse(image);

        } catch (IOException e) {
            log.error("Error uploading image to Cloudinary for product {}", productId, e);
            throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    @Override
    public String uploadFile(MultipartFile file) {
        if (file == null || file.isEmpty())
            throw new BusinessException(ErrorCode.FILE_REQUIRED);

        try {
            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap("folder", "products/general")
            );
            return (String) uploadResult.get("secure_url");

        } catch (IOException e) {
            log.error("Error uploading general file to Cloudinary", e);
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

        // Удаляем из Cloudinary по publicId
        deleteFromCloudinary(image.getPublicId());

        // Обнуляем ссылку в продукте
        Product product = image.getProduct();
        product.setImageUrl(null);
        productRepository.save(product);

        imageRepository.delete(image);
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Image successfully deleted")
                .build();
    }

    private void deleteFromCloudinary(String publicId) {
        if (publicId == null) return;
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            log.info("Deleted from Cloudinary: {}", publicId);
        } catch (IOException e) {
            log.error("Could not delete from Cloudinary: {}", publicId, e);
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