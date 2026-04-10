package kg.manas.skincare.config;

import kg.manas.skincare.model.User;
import kg.manas.skincare.security.user.PersonDetails;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class ApplicationAuditorAware implements AuditorAware<Long> {
    @Override
    public Optional<Long> getCurrentAuditor() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication==null||!authentication.isAuthenticated()
                ||authentication instanceof AnonymousAuthenticationToken) {
            return Optional.empty();
        }
        final PersonDetails user = (PersonDetails) authentication.getPrincipal();

        return Optional.ofNullable(user.getUser().getUserId());
    }
}
