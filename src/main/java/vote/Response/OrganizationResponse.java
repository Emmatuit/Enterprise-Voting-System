package vote.Response;

import java.time.LocalDateTime;

public class OrganizationResponse {

	public static class Builder {
		private OrganizationResponse response = new OrganizationResponse();

		public Builder active(boolean active) {
			response.active = active;
			return this;
		}

		public Builder address(String address) {
			response.address = address;
			return this;
		}

		public OrganizationResponse build() {
			return response;
		}

		public Builder code(String code) {
			response.code = code;
			return this;
		}

		public Builder contactEmail(String contactEmail) {
			response.contactEmail = contactEmail;
			return this;
		}

		public Builder contactPhone(String contactPhone) {
			response.contactPhone = contactPhone;
			return this;
		}

		public Builder createdAt(LocalDateTime createdAt) {
			response.createdAt = createdAt;
			return this;
		}

		public Builder description(String description) {
			response.description = description;
			return this;
		}

		public Builder id(Long id) {
			response.id = id;
			return this;
		}

		public Builder name(String name) {
			response.name = name;
			return this;
		}

		public Builder updatedAt(LocalDateTime updatedAt) {
			response.updatedAt = updatedAt;
			return this;
		}
	}
	// Builder
	public static Builder builder() {
		return new Builder();
	}
	private Long id;
	private String name;
	private String code;
	private String description;
	private String contactEmail;
	private String contactPhone;
	private String address;
	private boolean active;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;

	public String getAddress() {
		return address;
	}

	public String getCode() {
		return code;
	}

	public String getContactEmail() {
		return contactEmail;
	}

	public String getContactPhone() {
		return contactPhone;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public String getDescription() {
		return description;
	}

	// Getters
	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public boolean isActive() {
		return active;
	}
}