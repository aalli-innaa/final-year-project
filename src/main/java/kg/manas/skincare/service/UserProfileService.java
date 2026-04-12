package kg.manas.skincare.service;

import kg.manas.skincare.dto.requests.SkinQuestionnaireRequest;
import kg.manas.skincare.dto.response.UserProfileResponse;
import kg.manas.skincare.enums.SkinType;
import kg.manas.skincare.exceptions.BusinessException;
import kg.manas.skincare.exceptions.ErrorCode;
import kg.manas.skincare.model.User;
import kg.manas.skincare.model.UserProfile;
import kg.manas.skincare.repository.UserProfileRepository;
import kg.manas.skincare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;

    private static final List<String> ALLOWED_GENDERS = List.of("MALE", "FEMALE");

    /**
     * Создать профиль на основе ответов анкеты (5 вопросов + дата рождения + пол)
     */
    @Transactional
    public UserProfileResponse createProfileFromQuestionnaire(
            Long userId,
            SkinQuestionnaireRequest request) {

        if (userProfileRepository.existsByUser_UserId(userId)) {
            throw new BusinessException(
                    ErrorCode.INTERNAL_EXCEPTION,
                    "Profile already exists for user"
            );
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.USER_NOT_FOUND,
                        userId
                ));

        validateGender(request.getGender());
        SkinType determinedSkinType = determineSkinType(request);

        UserProfile profile = UserProfile.builder()
                .user(user)
                .skinType(determinedSkinType)
                .birthDate(request.getBirthDate())
                .gender(request.getGender().toUpperCase())
                .build();

        return UserProfileResponse.fromEntity(userProfileRepository.save(profile));
    }

    /**
     * Получить профиль пользователя
     */
    @Transactional(readOnly = true)
    public UserProfileResponse getProfile(Long userId) {
        UserProfile profile = userProfileRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.USER_NOT_FOUND,
                        "User profile not found for user: " + userId
                ));

        return UserProfileResponse.fromEntity(profile);
    }

    /**
     * Обновить профиль — пересдать анкету
     */
    @Transactional
    public UserProfileResponse updateProfileFromQuestionnaire(
            Long userId,
            SkinQuestionnaireRequest request) {

        UserProfile profile = userProfileRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.USER_NOT_FOUND,
                        "User profile not found for user: " + userId
                ));

        validateGender(request.getGender());
        SkinType newSkinType = determineSkinType(request);

        profile.setSkinType(newSkinType);
        profile.setBirthDate(request.getBirthDate());
        profile.setGender(request.getGender().toUpperCase());

        return UserProfileResponse.fromEntity(userProfileRepository.save(profile));
    }

    /**
     * Проверяем, что пол только MALE или FEMALE
     */
    private void validateGender(String gender) {
        if (gender == null || !ALLOWED_GENDERS.contains(gender.toUpperCase())) {
            throw new BusinessException(
                    ErrorCode.INTERNAL_EXCEPTION,
                    "Invalid gender value. Allowed: MALE, FEMALE"
            );
        }
    }

    /**
     * 🧮 АЛГОРИТМ ГОЛОСОВАНИЯ: Определяем SkinType
     *
     * Каждый из 5 ответов — голос за тип кожи.
     * Побеждает тип с максимальным числом голосов.
     * При ничьей — по умолчанию COMBINATION.
     */
    private SkinType determineSkinType(SkinQuestionnaireRequest request) {
        Map<SkinType, Integer> votes = new HashMap<>();
        votes.put(SkinType.OILY, 0);
        votes.put(SkinType.DRY, 0);
        votes.put(SkinType.COMBINATION, 0);
        votes.put(SkinType.SENSITIVE, 0);

        votes.put(request.getQuestion1(), votes.get(request.getQuestion1()) + 1);
        votes.put(request.getQuestion2(), votes.get(request.getQuestion2()) + 1);
        votes.put(request.getQuestion3(), votes.get(request.getQuestion3()) + 1);
        votes.put(request.getQuestion4(), votes.get(request.getQuestion4()) + 1);
        votes.put(request.getQuestion5(), votes.get(request.getQuestion5()) + 1);

        System.out.println("Votes: " + votes);

        SkinType result = votes.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(SkinType.COMBINATION);

        System.out.println("Determined SkinType: " + result);

        return result;
    }
}