package vote.ServiceImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vote.Entity.Organization;
import vote.Entity.User;
import vote.Exception.BusinessRuleException;
import vote.Repository.OrganizationRepository;
import vote.Repository.UserRepository;
import vote.Request.LoginRequest;
import vote.Request.RegisterRequest;
import vote.Response.AuthResponse;
import vote.Service.AuthService;
import vote.Util.JwtTokenProvider;


@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;

    public AuthServiceImpl(
            AuthenticationManager authenticationManager,
            JwtTokenProvider jwtTokenProvider,
            PasswordEncoder passwordEncoder,
            UserRepository userRepository,
            OrganizationRepository organizationRepository) {

        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;
    }

    @Override
    public AuthResponse login(LoginRequest loginRequest) {

        logger.info("Login attempt for username: {}", loginRequest.getUsername());

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            String token = jwtTokenProvider.generateToken(authentication);

            User user = userRepository.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new BusinessRuleException("User not found"));

            user.recordLogin();
            userRepository.save(user);

            AuthResponse response = AuthResponse.builder()
                    .token(token)
                    .tokenType("Bearer")
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .role(user.getRole().name())
                    .build();

            if (user.getOrganization() != null) {
                response.setOrganizationId(user.getOrganization().getId());
                response.setOrganizationName(user.getOrganization().getName());
            }

            logger.info("Login successful for username: {}", loginRequest.getUsername());
            return response;

        } catch (BadCredentialsException ex) {

            logger.warn("Invalid login attempt for username: {}", loginRequest.getUsername());

            userRepository.findByUsername(loginRequest.getUsername()).ifPresent(user -> {
                user.incrementFailedLoginAttempts();
                userRepository.save(user);
            });

            throw new BusinessRuleException("Invalid username or password");
        }
    }

    @Override
    public void logout(String token) {
        // Stateless JWT logout – client-side responsibility
        logger.info("Logout requested");
    }

    @Override
    public String refreshToken(String oldToken) {

        if (!jwtTokenProvider.validateToken(oldToken)) {
            throw new BusinessRuleException("Invalid or expired token");
        }

        String username = jwtTokenProvider.getUsername(oldToken);
        return jwtTokenProvider.generateTokenFromUsername(username);
    }

    @Override
    public AuthResponse register(RegisterRequest request) {

        logger.info("Registration attempt for username: {}", request.getUsername());

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessRuleException("Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessRuleException("Email already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setRole(request.getRole());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setActive(true);
        user.setLocked(false);

        if (request.getOrganizationId() != null) {
            Organization organization = organizationRepository
                    .findById(request.getOrganizationId())
                    .orElseThrow(() -> new BusinessRuleException("Organization not found"));
            user.setOrganization(organization);
        }

        User savedUser = userRepository.save(user);
        String token = jwtTokenProvider.generateTokenFromUsername(savedUser.getUsername());

        AuthResponse response = AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .role(savedUser.getRole().name())
                .build();

        if (savedUser.getOrganization() != null) {
            response.setOrganizationId(savedUser.getOrganization().getId());
            response.setOrganizationName(savedUser.getOrganization().getName());
        }

        logger.info("Registration successful for username: {}", savedUser.getUsername());
        return response;
    }

    @Override
    public boolean validateToken(String token) {
        return jwtTokenProvider.validateToken(token);
    }
}
