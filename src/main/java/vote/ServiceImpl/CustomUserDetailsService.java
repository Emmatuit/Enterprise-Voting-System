package vote.ServiceImpl;

import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vote.Entity.User;
import vote.Repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

	private final UserRepository userRepository;

	public CustomUserDetailsService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Transactional
	public UserDetails loadUserByEmail(String email) throws UsernameNotFoundException {
		logger.debug("Loading user by email: {}", email);

		User user = userRepository.findByEmail(email).orElseThrow(() -> {
			logger.error("User not found with email: {}", email);
			return new UsernameNotFoundException("User not found with email: " + email);
		});

		if (!user.isActive()) {
			logger.error("User account is inactive: {}", email);
			throw new UsernameNotFoundException("User account is inactive: " + email);
		}

		if (user.isLocked()) {
			logger.error("User account is locked: {}", email);
			throw new UsernameNotFoundException("User account is locked: " + email);
		}

		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPasswordHash(),
				Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())));
	}

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		logger.debug("Loading user by username: {}", username);

		User user = userRepository.findByUsername(username).orElseThrow(() -> {
			logger.error("User not found with username: {}", username);
			return new UsernameNotFoundException("User not found with username: " + username);
		});

		if (!user.isActive()) {
			logger.error("User account is inactive: {}", username);
			throw new UsernameNotFoundException("User account is inactive: " + username);
		}

		if (user.isLocked()) {
			logger.error("User account is locked: {}", username);
			throw new UsernameNotFoundException("User account is locked: " + username);
		}

		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPasswordHash(),
				Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())));
	}
}