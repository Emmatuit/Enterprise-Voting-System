package vote.Entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "voter_registry", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "organization_id", "matric_number" }),
		@UniqueConstraint(columnNames = { "organization_id", "email" }),
		@UniqueConstraint(columnNames = { "organization_id", "phone" }) })
public class VoterRegistry extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "organization_id", nullable = false)
	private Organization organization;

	@Column(name = "matric_number")
	private String matricNumber;

	@Column(name = "email")
	private String email;

	@Column(name = "phone")
	private String phone;

	@Column(name = "full_name")
	private String fullName;

	@Column(name = "used", nullable = false)
	private boolean used = false;

	@Column(name = "voted_at")
	private LocalDateTime votedAt;

	@Column(name = "verification_attempts")
	private int verificationAttempts = 0;

	@Column(name = "last_verification_attempt")
	private LocalDateTime lastVerificationAttempt;

	// Default constructor
	public VoterRegistry() {
	}

	// Parameterized constructor
	public VoterRegistry(Organization organization, String matricNumber, String email, String phone, String fullName) {
		this.organization = organization;
		this.matricNumber = matricNumber;
		this.email = email;
		this.phone = phone;
		this.fullName = fullName;
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

		VoterRegistry that = (VoterRegistry) o;

		return getId() != null ? getId().equals(that.getId()) : that.getId() == null;
	}

	public String getEmail() {
		return email;
	}

	public String getFullName() {
		return fullName;
	}

	public LocalDateTime getLastVerificationAttempt() {
		return lastVerificationAttempt;
	}

	public String getMatricNumber() {
		return matricNumber;
	}

	// Getters and Setters
	public Organization getOrganization() {
		return organization;
	}

	public String getPhone() {
		return phone;
	}

	public int getVerificationAttempts() {
		return verificationAttempts;
	}

	public LocalDateTime getVotedAt() {
		return votedAt;
	}

	@Override
	public int hashCode() {
		return getId() != null ? getId().hashCode() : 0;
	}

	// Helper method to increment verification attempts
	public void incrementVerificationAttempts() {
		this.verificationAttempts++;
		this.lastVerificationAttempt = LocalDateTime.now();
	}

	// Check if voter is eligible to vote
	public boolean isEligible() {
		return !used && verificationAttempts < 5; // Max 5 verification attempts
	}

	public boolean isUsed() {
		return used;
	}

	// Helper method to mark as voted
	public void markAsVoted() {
		this.used = true;
		this.votedAt = LocalDateTime.now();
	}

	// Helper method to reset verification attempts
	public void resetVerificationAttempts() {
		this.verificationAttempts = 0;
		this.lastVerificationAttempt = null;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public void setLastVerificationAttempt(LocalDateTime lastVerificationAttempt) {
		this.lastVerificationAttempt = lastVerificationAttempt;
	}

	public void setMatricNumber(String matricNumber) {
		this.matricNumber = matricNumber;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public void setUsed(boolean used) {
		this.used = used;
	}

	public void setVerificationAttempts(int verificationAttempts) {
		this.verificationAttempts = verificationAttempts;
	}

	public void setVotedAt(LocalDateTime votedAt) {
		this.votedAt = votedAt;
	}

	// toString method
	@Override
	public String toString() {
		return "VoterRegistry{" + "id=" + getId() + ", matricNumber='" + matricNumber + '\'' + ", email='" + email
				+ '\'' + ", used=" + used + ", organizationId=" + (organization != null ? organization.getId() : "null")
				+ '}';
	}
}