package kg.manas.skincare.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // Auth & User
    USER_NOT_FOUND("USER_NOT_FOUND", "User not found with id %s", HttpStatus.NOT_FOUND),
    CHANGE_PASSWORD_MISMATCH("CHANGE_PASSWORD_MISMATCH", "Current password and new password are not the same" , HttpStatus.BAD_REQUEST),
    INVALID_CURRENT_PASSWORD("INVALID_CURRENT_PASSWORD", "Current password is invalid", HttpStatus.BAD_REQUEST),
    ACCOUNT_ALREADY_DEACTIVATED("ACCOUNT_ALREADY_DEACTIVATED","Account is already deactivated" , HttpStatus.BAD_REQUEST ),
    ACCOUNT_ALREADY_ACTIVATED("ACCOUNT_ALREADY_ACTIVATED", "Account is already activated", HttpStatus.BAD_REQUEST),
    EMAIL_ALREADY_EXISTS("EMAIL_ALREADY_EXISTS", "Email already exists" , HttpStatus.BAD_REQUEST ),
    PASSWORD_MISMATCH("PASSWORD_MISMATCH","Password do not mismatch" , HttpStatus.BAD_REQUEST),
    ERR_USER_DISABLED("ERR_USER_DISABLED","User is disabled" , HttpStatus.UNAUTHORIZED),
    BAD_CREDENTIALS("BAD_CREDENTIALS","Username and / or password is incorrect" , HttpStatus.UNAUTHORIZED),
    USERNAME_NOT_FOUND("USERNAME_NOT_FOUND","Username not found" , HttpStatus.UNAUTHORIZED),

    // System & Files
    INTERNAL_EXCEPTION("INTERNAL_EXCEPTION","Internal server error" , HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_UPLOAD_FAILED("FILE_UPLOAD_FAILED", "File uploaded failed", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_REQUIRED("FILE_REQUIRED","File required", HttpStatus.BAD_REQUEST ),
    FILE_UPDATE_FAILED("FILE_UPDATE_FAILED","File update failed" ,HttpStatus.INTERNAL_SERVER_ERROR),

    // Product
    PRODUCT_NOT_FOUND("PRODUCT_NOT_FOUND", "Product not found with id %s", HttpStatus.NOT_FOUND),
    PRODUCT_REQUIRED("PRODUCT_REQUIRED","Product required", HttpStatus.BAD_REQUEST ),
    IMAGE_NOT_FOUND("IMAGE_NOT_FOUND","Image not found" ,HttpStatus.BAD_REQUEST ),

    // Analysis & AI (Новые)
    ACCESS_DENIED("FORBIDDEN", "У вас нет прав для этого действия", HttpStatus.FORBIDDEN),
    ANALYSIS_NOT_FOUND("ANALYSIS_NOT_FOUND", "Запись анализа не найдена", HttpStatus.NOT_FOUND),
    PROFILE_REQUIRED("PROFILE_REQUIRED", "Пожалуйста, заполните профиль (тип кожи и возраст) перед анализом", HttpStatus.BAD_REQUEST),
    AI_SERVICE_ERROR("AI_SERVICE_ERROR", "ИИ-сервис вернул ошибку", HttpStatus.INTERNAL_SERVER_ERROR),
    AI_SERVICE_UNAVAILABLE("AI_SERVICE_UNAVAILABLE", "Связь с ИИ-модулем разорвана", HttpStatus.SERVICE_UNAVAILABLE),
    FACE_NOT_FOUND("FACE_NOT_FOUND", "Лицо не найдено. Сделайте другое фото", HttpStatus.UNPROCESSABLE_ENTITY);

    private final String code;
    private final String defaultMessage;
    private final HttpStatus status;

    ErrorCode(String code, String defaultMessage, HttpStatus status) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.status = status;
    }
}