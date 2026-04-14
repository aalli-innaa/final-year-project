package kg.manas.skincare.service;

import kg.manas.skincare.model.User;
import kg.manas.skincare.model.UserPhoto;
import org.springframework.web.multipart.MultipartFile;

public interface UserPhotoService {

    /**
     * Загружает фото лица пользователя и сохраняет его в хранилище + БД
     */
    UserPhoto uploadFacePhoto(User user, MultipartFile file);

    /**
     * Удаляет фото пользователя (и файл с диска, и запись из БД)
     */
    void deletePhoto(UserPhoto photo);
}