package vote.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class OrganizationRequest {

	@NotBlank(message = "Organization name is required")
	@Size(min = 3, max = 100, message = "Organization name must be between 3 and 100 characters")
	private String name;

	@Size(max = 500, message = "Description cannot exceed 500 characters")
	private String description;

	@Email(message = "Invalid email format")
	private String contactEmail;

	@Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
	private String contactPhone;

	private String address;

	public String getAddress() {
		return address;
	}

	public String getContactEmail() {
		return contactEmail;
	}

	public String getContactPhone() {
		return contactPhone;
	}

	public String getDescription() {
		return description;
	}

	// Getters and Setters
	public String getName() {
		return name;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}

	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setName(String name) {
		this.name = name;
	}
}