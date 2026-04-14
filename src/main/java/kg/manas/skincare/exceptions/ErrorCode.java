package kg.manas.skincare.exceptions;

import lombok.Getter;
import org.springframework.boot.web.error.Error;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.http.HttpStatus;

import javax.tools.Diagnostic;

@Getter
public enum ErrorCode {
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
    INTERNAL_EXCEPTION("INTERNAL_EXCEPTION","Internal server error" , HttpStatus.INTERNAL_SERVER_ERROR),
    PRODUCT_NOT_FOUND("PRODUCT_NOT_FOUND", "Product not found with id %s", HttpStatus.NOT_FOUND),
    BARCODE_REQUIRED("BARCODE_REQUIRED","Barcode required", HttpStatus.BAD_REQUEST ),
    FILE_UPLOAD_FAILED("FILE_UPLOAD_FAILED", "File uploaded failed", HttpStatus.INTERNAL_SERVER_ERROR),
    PRODUCT_REQUIRED("PRODUCT_REQUIRED","Product required", HttpStatus.BAD_REQUEST ),
    IMAGE_NOT_FOUND("IMAGE_NOT_FOUND","Image not found" ,HttpStatus.BAD_REQUEST ),
    FILE_REQUIRED("FILE_REQUIRED","File required", HttpStatus.BAD_REQUEST ),
    TYPE_REQUIRED("TYPE_REQUIRED","Image type required" ,HttpStatus.BAD_REQUEST),
    FILE_UPDATE_FAILED("FILE_UPDATE_FAILED","File update failed" ,HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String defaultMessage;
    private final HttpStatus status;

    ErrorCode(String code, String defaultMessage, HttpStatus status) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.status = status;
    }
}
