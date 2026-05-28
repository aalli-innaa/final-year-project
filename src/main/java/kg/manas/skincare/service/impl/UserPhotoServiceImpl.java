package kg.manas.skincare.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import jakarta.transaction.Transactional;
import kg.manas.skincare.exceptions.BusinessException;
import kg.manas.skincare.exceptions.ErrorCode;
import kg.manas.skincare.model.User;
import kg.manas.skincare.model.UserPhoto;
import kg.manas.skincare.repository.UserPhotoRepository;
import kg.manas.skincare.service.UserPhotoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserPhotoServiceImpl implements UserPhotoService {

    private final UserPhotoRepository userPhotoRepository;
    private final Cloudinary cloudinary;

    @Transactional
    public UserPhoto uploadFacePhoto(User user, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.FILE_REQUIRED);
        }

        try {
            // Загружаем в Cloudinary в папку faces/{userId}/
            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap("folder", "faces/" + user.getUserId())
            );

            String imageUrl = (String) uploadResult.get("secure_url");
            String publicId = (String) uploadResult.get("public_id");

            UserPhoto photo = UserPhoto.builder()
                    .user(user)
                    .imageUrl(imageUrl)
                    .publicId(publicId)
                    .build();

            log.info("Face photo uploaded to Cloudinary for user {}: {}", user.getUserId(), imageUrl);
            return userPhotoRepository.save(photo);

        } catch (IOException e) {
            log.error("Failed to upload face photo for user {}", user.getUserId(), e);
            throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    @Transactional
    public void deletePhoto(UserPhoto photo) {
        try {
            if (photo.getPublicId() != null) {
                cloudinary.uploader().destroy(photo.getPublicId(), ObjectUtils.emptyMap());
                log.info("Deleted from Cloudinary: {}", photo.getPublicId());
            }
        } catch (IOException e) {
            log.error("Could not delete from Cloudinary: {}", photo.getPublicId(), e);
        }
        userPhotoRepository.delete(photo);
    }
}