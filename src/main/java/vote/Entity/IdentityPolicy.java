package vote.Entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import vote.Enum.OTPChannel;

@Entity
@Table(name = "identity_policies")
public class IdentityPolicy extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "organization_id", nullable = false)
	private Organization organization;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "description")
	private String description;

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "identity_policy_fields", joinColumns = @JoinColumn(name = "policy_id"))
	@Column(name = "field_name")
	private Set<String> identifierFields = new HashSet<>();

	@Enumerated(EnumType.STRING)
	@Column(name = "otp_channel", nullable = false)
	private OTPChannel otpChannel = OTPChannel.NONE;

	@Column(name = "is_locked", nullable = false)
	private boolean locked = false;

	@Column(name = "is_active", nullable = false)
	private boolean active = true;

	@Column(name = "otp_expiry_minutes")
	private Integer otpExpiryMinutes = 5;

	@Column(name = "max_otp_attempts")
	private Integer maxOtpAttempts = 3;

	// Default constructor
	public IdentityPolicy() {
	}

	// Parameterized constructor
	public IdentityPolicy(Organization organization, String name, Set<String> identifierFields, OTPChannel otpChannel) {
		this.organization = organization;
		this.name = name;
		this.identifierFields = identifierFields;
		this.otpChannel = otpChannel;
	}

	public void addIdentifierField(String field) {
		if (this.identifierFields == null) {
			this.identifierFields = new HashSet<>();
		}
		this.identifierFields.add(field);
	}

	// Check if policy can be modified
	public boolean canBeModified() {
		return !locked && active;
	}

	public boolean containsIdentifierField(String field) {
		return this.identifierFields != null && this.identifierFields.contains(field);
	}

	// equals and hashCode based on id
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		IdentityPolicy that = (IdentityPolicy) o;

		return getId() != null ? getId().equals(that.getId()) : that.getId() == null;
	}

	public String getDescription() {
		return description;
	}

	public Set<String> getIdentifierFields() {
		return identifierFields;
	}

	public Integer getMaxOtpAttempts() {
		return maxOtpAttempts;
	}

	public String getName() {
		return name;
	}

	// Getters and Setters
	public Organization getOrganization() {
		return organization;
	}

	public OTPChannel getOtpChannel() {
		return otpChannel;
	}

	public Integer getOtpExpiryMinutes() {
		return otpExpiryMinutes;
	}

	@Override
	public int hashCode() {
		return getId() != null ? getId().hashCode() : 0;
	}

	public boolean isActive() {
		return active;
	}

	public boolean isLocked() {
		return locked;
	}

	// Validate required fields
	public boolean isValid() {
		return !identifierFields.isEmpty() && otpChannel != null;
	}

	// Lock policy (cannot be undone)
	public void lockPolicy() {
		this.locked = true;
	}

	public void removeIdentifierField(String field) {
		if (this.identifierFields != null) {
			this.identifierFields.remove(field);
		}
	}

	public boolean requiresEmail() {
		return identifierFields.contains("email");
	}

	// Helper methods
	public boolean requiresMatricNumber() {
		return identifierFields.contains("matric_number");
	}

	public boolean requiresOtp() {
		return otpChannel != OTPChannel.NONE;
	}

	public boolean requiresPhone() {
		return identifierFields.contains("phone");
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setIdentifierFields(Set<String> identifierFields) {
		this.identifierFields = identifierFields;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public void setMaxOtpAttempts(Integer maxOtpAttempts) {
		this.maxOtpAttempts = maxOtpAttempts;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	public void setOtpChannel(OTPChannel otpChannel) {
		this.otpChannel = otpChannel;
	}

	public void setOtpExpiryMinutes(Integer otpExpiryMinutes) {
		this.otpExpiryMinutes = otpExpiryMinutes;
	}

	// toString method
	@Override
	public String toString() {
		return "IdentityPolicy{" + "id=" + getId() + ", name='" + name + '\'' + ", identifierFields=" + identifierFields
				+ ", otpChannel=" + otpChannel + ", locked=" + locked + ", active=" + active + ", organizationId="
				+ (organization != null ? organization.getId() : "null") + '}';
	}
}