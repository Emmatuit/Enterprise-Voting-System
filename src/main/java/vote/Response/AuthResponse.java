package vote.Response;

public class AuthResponse {

	public static class Builder {
		private AuthResponse response = new AuthResponse();

		public AuthResponse build() {
			return response;
		}

		public Builder email(String email) {
			response.email = email;
			return this;
		}

		public Builder organizationId(Long organizationId) {
			response.organizationId = organizationId;
			return this;
		}

		public Builder organizationName(String organizationName) {
			response.organizationName = organizationName;
			return this;
		}

		public Builder role(String role) {
			response.role = role;
			return this;
		}

		public Builder token(String token) {
			response.token = token;
			return this;
		}

		public Builder tokenType(String tokenType) {
			response.tokenType = tokenType;
			return this;
		}

		public Builder username(String username) {
			response.username = username;
			return this;
		}
	}
	// Builder
	public static Builder builder() {
		return new Builder();
	}
	private String token;
	private String tokenType = "Bearer";
	private String username;
	private String email;
	private String role;

	private Long organizationId;

	private String organizationName;

	public String getEmail() {
		return email;
	}

	public Long getOrganizationId() {
		return organizationId;
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public String getRole() {
		return role;
	}

	// Getters
	public String getToken() {
		return token;
	}

	public String getTokenType() {
		return tokenType;
	}

	public String getUsername() {
		return username;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setOrganizationId(Long organizationId) {
		this.organizationId = organizationId;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	public void setRole(String role) {
		this.role = role;
	}

	// Setters
	public void setToken(String token) {
		this.token = token;
	}

	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}