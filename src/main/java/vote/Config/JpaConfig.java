package vote.Config;

import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class JpaConfig {

	@Bean
	public AuditorAware<String> auditorAware() {
		return () -> {
			if (SecurityContextHolder.getContext().getAuthentication() == null) {
				return Optional.of("system");
			}

			Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

			if (principal instanceof UserDetails) {
				return Optional.of(((UserDetails) principal).getUsername());
			} else if (principal instanceof String) {
				return Optional.of((String) principal);
			}

			return Optional.of("anonymous");
		};
	}
}