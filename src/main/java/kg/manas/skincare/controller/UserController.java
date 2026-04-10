package kg.manas.skincare.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kg.manas.skincare.dto.requests.ChangePasswordRequest;
import kg.manas.skincare.dto.requests.ProfileUpdateRequest;
import kg.manas.skincare.model.User;
import kg.manas.skincare.security.user.PersonDetails;
import kg.manas.skincare.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
@Tag(name="User", description = "User api")
public class UserController {


    private final UserService userService;

    @PatchMapping("/me")
    @ResponseStatus(code= HttpStatus.NO_CONTENT)
    public void updateProfileInfo(
            @RequestBody
            @Valid
            final ProfileUpdateRequest request,
            final Authentication principal) {
        this.userService.updateProfileInfo(request, getUserId(principal));
    }


    @PostMapping("/me/password")
    @ResponseStatus(code=HttpStatus.NO_CONTENT)
    public void changePassword(
            @RequestBody
            @Valid
            final ChangePasswordRequest request,
            final Authentication principal
            ) {
        this.userService.changePassword(request, getUserId(principal));
    }


//    @PatchMapping("/me/deactivate")
//    @ResponseStatus(code=HttpStatus.NO_CONTENT)
//    public void deactivateAccount(final Authentication principal) {
//        this.userService.blockAccount(getUserId(principal));
//    }
//
//    @PatchMapping("/me/reactivate")
//    @ResponseStatus(code=HttpStatus.NO_CONTENT)
//    public void reactivateAccount(final Authentication principal) {
//        this.userService.unblockAccount(getUserId(principal));
//    }

//    @DeleteMapping("/me")
//    @ResponseStatus(code=HttpStatus.NO_CONTENT)
//    public void deleteAccount(final Authentication principal) {
//        this.userService.deleteAccount(getUserId(principal));
//    }

    private Long getUserId(final Authentication principal) {
        return ((PersonDetails) principal.getPrincipal()).getUser().getUserId();
    }
}
