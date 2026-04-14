package kg.manas.skincare.service;

import kg.manas.skincare.dto.requests.ChangePasswordRequest;
import kg.manas.skincare.dto.requests.ProfileUpdateRequest;
import kg.manas.skincare.dto.response.SimpleResponse;
import kg.manas.skincare.dto.response.UserResponse;
import kg.manas.skincare.model.User; // Добавь этот импорт

import java.util.List;

public interface UserService {

    SimpleResponse updateProfileInfo(ProfileUpdateRequest request, Long userId);
    SimpleResponse changePassword(ChangePasswordRequest request, Long userId);
    SimpleResponse blockAccount(Long userId);
    SimpleResponse unblockAccount(Long userId);
    SimpleResponse deleteAccount(Long userId);
    List<UserResponse> getAllUsers();
    UserResponse getUser(Long id);

    // Добавляем этот метод, чтобы контроллер мог его вызвать
    User getByEmail(String email);
}