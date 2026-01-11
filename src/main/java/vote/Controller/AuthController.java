package vote.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import vote.Request.LoginRequest;
import vote.Request.RegisterRequest;
import vote.Response.ApiResponse;
import vote.Response.AuthResponse;
import vote.Service.AuthService;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication APIs")
public class AuthController {

	private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/admin/login")
	@Operation(summary = "Admin login", description = "Authenticates admin and returns JWT token")
	public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
		logger.info("Login API called for username: {}", loginRequest.getUsername());

		AuthResponse authResponse = authService.login(loginRequest);

		return ResponseEntity.ok(ApiResponse.success(authResponse, "Login successful"));
	}

	@PostMapping("/logout")
	@Operation(summary = "Logout", description = "Logs out the current user")
	public ResponseEntity<ApiResponse<Void>> logout(@RequestHeader("Authorization") String authorizationHeader) {
		logger.info("Logout API called");

		String token = authorizationHeader.substring(7); // Remove "Bearer " prefix
		authService.logout(token);

		return ResponseEntity.ok(ApiResponse.success(null, "Logout successful"));
	}

	@PostMapping("/refresh-token")
	@Operation(summary = "Refresh token", description = "Refreshes JWT token")
	public ResponseEntity<ApiResponse<String>> refreshToken(
			@RequestHeader("Authorization") String authorizationHeader) {
		String oldToken = authorizationHeader.substring(7); // Remove "Bearer " prefix

		String newToken = authService.refreshToken(oldToken);

		return ResponseEntity.ok(ApiResponse.success(newToken, "Token refreshed successfully"));
	}

	@PostMapping("/admin/register")
	@Operation(summary = "Admin registration", description = "Registers a new admin user")
	public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest registerRequest) {
		logger.info("Register API called for username: {}", registerRequest.getUsername());

		AuthResponse authResponse = authService.register(registerRequest);

		return ResponseEntity.ok(ApiResponse.success(authResponse, "Registration successful"));
	}

	@PostMapping("/validate-token")
	@Operation(summary = "Validate token", description = "Validates JWT token")
	public ResponseEntity<ApiResponse<Boolean>> validateToken(
			@RequestHeader("Authorization") String authorizationHeader) {
		String token = authorizationHeader.substring(7); // Remove "Bearer " prefix

		boolean isValid = authService.validateToken(token);

		return ResponseEntity.ok(ApiResponse.success(isValid, "Token validation result"));
	}
}
