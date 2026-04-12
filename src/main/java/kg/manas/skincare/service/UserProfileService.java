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
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;

    /**
     * Создать профиль на основе ответов анкеты (5 вопросов)
     * Система определяет SkinType путём голосования
     */
    @Transactional
    public UserProfileResponse createProfileFromQuestionnaire(
            Long userId,
            SkinQuestionnaireRequest request) {

        // Проверяем, есть ли уже профиль
        if (userProfileRepository.existsByUser_UserId(userId)) {
            throw new BusinessException(
                    ErrorCode.INTERNAL_EXCEPTION,
                    "Profile already exists for user"
            );
        }

        // Ищем пользователя
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.USER_NOT_FOUND,
                        userId
                ));

        // 🧮 ГЛАВНОЕ: Определяем SkinType на основе ответов (голосование)
        SkinType determinedSkinType = determineSkinType(request);

        // Создаём профиль с определённым SkinType
        UserProfile profile = UserProfile.builder()
                .user(user)
                .skinType(determinedSkinType)
                .build();

        UserProfile savedProfile = userProfileRepository.save(profile);
        return UserProfileResponse.fromEntity(savedProfile);
    }

    /**
     * 🧮 АЛГОРИТМ ГОЛОСОВАНИЯ: Определяем SkinType
     *
     * Логика:
     * 1. Каждый ответ - "голос" за определённый тип кожи
     * 2. Считаем голоса за каждый тип
     * 3. Выбираем тип с максимальным количеством голосов
     * 4. Если ничья - берём COMBINATION по умолчанию
     */
    private SkinType determineSkinType(SkinQuestionnaireRequest request) {
        // Счётчик голосов для каждого типа кожи
        Map<SkinType, Integer> votes = new HashMap<>();
        votes.put(SkinType.OILY, 0);
        votes.put(SkinType.DRY, 0);
        votes.put(SkinType.COMBINATION, 0);
        votes.put(SkinType.SENSITIVE, 0);

        // Добавляем голос за каждый ответ
        votes.put(request.getQuestion1(), votes.get(request.getQuestion1()) + 1);
        votes.put(request.getQuestion2(), votes.get(request.getQuestion2()) + 1);
        votes.put(request.getQuestion3(), votes.get(request.getQuestion3()) + 1);
        votes.put(request.getQuestion4(), votes.get(request.getQuestion4()) + 1);
        votes.put(request.getQuestion5(), votes.get(request.getQuestion5()) + 1);

        // Логирование для отладки
        System.out.println("Votes: " + votes);

        // 🏆 Определяем победителя (тип с максимальными голосами)
        SkinType resultSkinType = votes.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(SkinType.COMBINATION);

        System.out.println("Determined SkinType: " + resultSkinType);

        return resultSkinType;
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
     * Обновить профиль пересдачей анкеты
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

        // 🧮 Пересчитываем SkinType на основе новых ответов
        SkinType newSkinType = determineSkinType(request);
        profile.setSkinType(newSkinType);

        UserProfile updatedProfile = userProfileRepository.save(profile);

        return UserProfileResponse.fromEntity(updatedProfile);
    }
}