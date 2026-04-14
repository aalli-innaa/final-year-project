package kg.manas.skincare.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kg.manas.skincare.dto.requests.ChangePasswordRequest;
import kg.manas.skincare.dto.requests.ProfileUpdateRequest;
import kg.manas.skincare.dto.response.SimpleResponse;
import kg.manas.skincare.dto.response.UserResponse;
import kg.manas.skincare.security.user.PersonDetails;
import kg.manas.skincare.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
@Tag(name="User", description = "User api")
public class UserController {

    private final UserService userService;

    @PatchMapping("/me")
    @ResponseStatus(code= HttpStatus.NO_CONTENT)
    public SimpleResponse updateProfileInfo(
            @RequestBody
            @Valid
            final ProfileUpdateRequest request,
            final Authentication principal) {
        return userService.updateProfileInfo(request, getUserId(principal));
    }

    @PostMapping("/me/password")
    @ResponseStatus(code=HttpStatus.NO_CONTENT)
    public SimpleResponse changePassword(
            @RequestBody
            @Valid
            final ChangePasswordRequest request,
            final Authentication principal
    ) {
        return userService.changePassword(request, getUserId(principal));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/admin/deactivate/{userId}")
    @ResponseStatus(code=HttpStatus.NO_CONTENT)
    public SimpleResponse deactivateAccount(@PathVariable Long userId) {
        return userService.blockAccount(userId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/admin/reactivate/{userId}")
    @ResponseStatus(code=HttpStatus.NO_CONTENT)
    public SimpleResponse reactivateAccount(@PathVariable Long userId) {
        return userService.unblockAccount(userId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/{userId}")
    @ResponseStatus(code=HttpStatus.NO_CONTENT)
    public SimpleResponse deleteAccount(@PathVariable Long userId) {
        return userService.deleteAccount(userId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/{userId}")
    public UserResponse getUser(@PathVariable Long userId) {
        return userService.getUser(userId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    private Long getUserId(final Authentication principal) {
        return ((PersonDetails) principal.getPrincipal()).getUser().getUserId();
    }
}