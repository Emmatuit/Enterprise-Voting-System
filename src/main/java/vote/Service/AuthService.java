package vote.Service;

import vote.Request.LoginRequest;
import vote.Request.RegisterRequest;
import vote.Response.AuthResponse;

public interface AuthService {

	AuthResponse login(LoginRequest loginRequest);

	void logout(String token);

	String refreshToken(String oldToken);

	AuthResponse register(RegisterRequest registerRequest);

	boolean validateToken(String token);
}
