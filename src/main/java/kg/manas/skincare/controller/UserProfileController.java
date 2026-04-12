package kg.manas.skincare.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kg.manas.skincare.dto.requests.SkinQuestionnaireRequest;
import kg.manas.skincare.dto.response.UserProfileResponse;
import kg.manas.skincare.security.user.PersonDetails;
import kg.manas.skincare.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/user-profiles")
@RequiredArgsConstructor
@Tag(name = "User Profile", description = "API для управления профилем пользователя")
public class UserProfileController {

    private final UserProfileService userProfileService;

    /**
     * Создать профиль — заполнить анкету после регистрации
     * POST /api/v1/user-profiles
     */
    @PostMapping
    @Operation(summary = "Создать профиль: 5 вопросов + дата рождения + пол")
    public ResponseEntity<UserProfileResponse> createProfile(
            @Valid @RequestBody SkinQuestionnaireRequest request,
            Authentication principal) {
        Long userId = getUserId(principal);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userProfileService.createProfileFromQuestionnaire(userId, request));
    }

    /**
     * Получить свой профиль
     * GET /api/v1/user-profiles/me
     */
    @GetMapping("/me")
    @Operation(summary = "Получить мой профиль")
    public ResponseEntity<UserProfileResponse> getMyProfile(Authentication principal) {
        Long userId = getUserId(principal);
        return ResponseEntity.ok(userProfileService.getProfile(userId));
    }

    /**
     * Обновить профиль — пересдать анкету
     * PUT /api/v1/user-profiles/me
     */
    @PutMapping("/me")
    @Operation(summary = "Обновить профиль через анкету")
    public ResponseEntity<UserProfileResponse> updateProfile(
            @Valid @RequestBody SkinQuestionnaireRequest request,
            Authentication principal) {
        Long userId = getUserId(principal);
        return ResponseEntity.ok(
                userProfileService.updateProfileFromQuestionnaire(userId, request));
    }

    private Long getUserId(Authentication principal) {
        return ((PersonDetails) principal.getPrincipal()).getUser().getUserId();
    }
}