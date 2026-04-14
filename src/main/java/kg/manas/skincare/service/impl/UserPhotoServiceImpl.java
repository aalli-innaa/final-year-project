package kg.manas.skincare.service.impl;

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
import java.nio.file.*;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserPhotoServiceImpl implements UserPhotoService {

    private final UserPhotoRepository userPhotoRepository;
    // userRepository удалил, так как он не используется

    private static final String BASE_PATH = "storage/faces/";

    @Transactional
    public UserPhoto uploadFacePhoto(User user, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.FILE_REQUIRED);
        }

        try {
            // Создаем путь к папке пользователя: storage/faces/{userId}/
            Path directory = Paths.get(BASE_PATH, String.valueOf(user.getUserId()));
            Files.createDirectories(directory);

            // Генерируем уникальное имя файла
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = directory.resolve(fileName);

            // Сохраняем файл на диск
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Формируем URL для базы данных
            String imageUrl = "/faces/" + user.getUserId() + "/" + fileName;

            UserPhoto photo = UserPhoto.builder()
                    .user(user)
                    .imageUrl(imageUrl)
                    .build();

            log.info("Face photo uploaded for user {}: {}", user.getUserId(), imageUrl);
            return userPhotoRepository.save(photo);

        } catch (IOException e) {
            log.error("Failed to upload face photo for user {}", user.getUserId(), e);
            throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    @Transactional
    public void deletePhoto(UserPhoto photo) {
        try {
            // Превращаем URL в путь на диске
            String pathOnDisk = photo.getImageUrl().replace("/faces/", BASE_PATH);
            Files.deleteIfExists(Paths.get(pathOnDisk));
            log.info("Physical file deleted: {}", pathOnDisk);
        } catch (IOException e) {
            log.error("Could not delete physical file: {}", photo.getImageUrl(), e);
        }
        userPhotoRepository.delete(photo);
    }

}

