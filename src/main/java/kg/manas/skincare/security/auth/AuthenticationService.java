package kg.manas.skincare.security.auth;

import kg.manas.skincare.security.dto.request.AuthenticationRequest;
import kg.manas.skincare.security.dto.request.RefreshRequest;
import kg.manas.skincare.security.dto.request.RegistrationRequest;
import kg.manas.skincare.security.dto.response.AuthenticationResponse;

public interface AuthenticationService {

    AuthenticationResponse login(AuthenticationRequest request);
    void register(RegistrationRequest request);
    AuthenticationResponse refreshToken(RefreshRequest request);

}
