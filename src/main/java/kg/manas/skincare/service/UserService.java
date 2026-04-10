package kg.manas.skincare.service;

import kg.manas.skincare.dto.requests.ChangePasswordRequest;
import kg.manas.skincare.dto.requests.ProfileUpdateRequest;

public interface UserService {

    void updateProfileInfo(ProfileUpdateRequest request, Long userId);
    void changePassword(ChangePasswordRequest request, Long userId);
    void blockAccount(Long userId);
    void unblockAccount(Long userId);
    void deleteAccount(Long userId);

}
