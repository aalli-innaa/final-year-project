package kg.manas.skincare.security.user;

import kg.manas.skincare.dto.requests.ProfileUpdateRequest;
import kg.manas.skincare.model.User;
import kg.manas.skincare.security.dto.request.RegistrationRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserMapper {

    private final PasswordEncoder passwordEncoder;

    public void mergeUserInfo(final User user, final ProfileUpdateRequest request) {
        if(StringUtils.isNoneBlank(request.getUsername())&&!user.getUsername().equals(request.getUsername())) {
            user.setUsername(request.getUsername());
        }
    }

    public User toUser(final RegistrationRequest request) {
        return User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(this.passwordEncoder.encode(request.getPassword()))
                .enabled(true)
                .blocked(false)
                .expired(false)
                .isEmailVerified(false)
                .build();
    }
}
