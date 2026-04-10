package kg.manas.skincare.security.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import kg.manas.skincare.security.dto.request.AuthenticationRequest;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthenticationResponse {

    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("refresh_token")
    private String refreshToken;
    @JsonProperty("token_type")
    private String tokenType;

}
