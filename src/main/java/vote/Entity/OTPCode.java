package vote.Entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "otp_codes", indexes = { @Index(name = "idx_otp_identifier", columnList = "identifier"),
		@Index(name = "idx_otp_expiry", columnList = "expires_at") })
public class OTPCode extends BaseEntity {

	@Column(name = "identifier", nullable = false)
	private String identifier; // email or phone number

	@Column(name = "code", nullable = false, length = 10)
	private String code;

	@Column(name = "channel", nullable = false, length = 10)
	private String channel; // EMAIL or SMS

	@Column(name = "purpose", nullable = false, length = 50)
	private String purpose; // VOTER_VERIFICATION, PASSWORD_RESET, etc.

	@Column(name = "attempts", nullable = false)
	private Integer attempts = 0;

	@Column(name = "max_attempts", nullable = false)
	private Integer maxAttempts = 3;

	@Column(name = "is_used", nullable = false)
	private boolean used = false;

	@Column(name = "expires_at", nullable = false)
	private LocalDateTime expiresAt;

	@Column(name = "used_at")
	private LocalDateTime usedAt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "organization_id")
	private Organization organization;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "voter_registry_id")
	private VoterRegistry voterRegistry;

	// Default constructor
	public OTPCode() {
	}

	// Parameterized constructor
	public OTPCode(String identifier, String code, String channel, String purpose, Integer maxAttempts,
			LocalDateTime expiresAt) {
		this.identifier = identifier;
		this.code = code;
		this.channel = channel;
		this.purpose = purpose;
		this.maxAttempts = maxAttempts;
		this.expiresAt = expiresAt;
	}

	public boolean canRetry() {
		return attempts < maxAttempts && !isExpired();
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

		OTPCode otpCode = (OTPCode) o;

		return getId() != null ? getId().equals(otpCode.getId()) : otpCode.getId() == null;
	}

	public Integer getAttempts() {
		return attempts;
	}

	public String getChannel() {
		return channel;
	}

	public String getCode() {
		return code;
	}

	public LocalDateTime getExpiresAt() {
		return expiresAt;
	}

	// Getters and Setters
	public String getIdentifier() {
		return identifier;
	}

	public Integer getMaxAttempts() {
		return maxAttempts;
	}

	public Organization getOrganization() {
		return organization;
	}

	public String getPurpose() {
		return purpose;
	}

	public long getRemainingSeconds() {
		LocalDateTime now = LocalDateTime.now();
		if (now.isAfter(expiresAt)) {
			return 0;
		}
		return java.time.Duration.between(now, expiresAt).getSeconds();
	}

	public LocalDateTime getUsedAt() {
		return usedAt;
	}

	public VoterRegistry getVoterRegistry() {
		return voterRegistry;
	}

	@Override
	public int hashCode() {
		return getId() != null ? getId().hashCode() : 0;
	}

	// Helper methods
	public void incrementAttempts() {
		this.attempts++;
	}

	public boolean isExpired() {
		return LocalDateTime.now().isAfter(expiresAt);
	}

	public boolean isUsed() {
		return used;
	}

	public boolean isValid() {
		return !used && !isExpired() && attempts < maxAttempts;
	}

	public void setAttempts(Integer attempts) {
		this.attempts = attempts;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setExpiresAt(LocalDateTime expiresAt) {
		this.expiresAt = expiresAt;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public void setMaxAttempts(Integer maxAttempts) {
		this.maxAttempts = maxAttempts;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	public void setUsed(boolean used) {
		this.used = used;
	}

	public void setUsedAt(LocalDateTime usedAt) {
		this.usedAt = usedAt;
	}

	public void setVoterRegistry(VoterRegistry voterRegistry) {
		this.voterRegistry = voterRegistry;
	}

	// toString method
	@Override
	public String toString() {
		return "OTPCode{" + "id=" + getId() + ", identifier='" + identifier + '\'' + ", channel='" + channel + '\''
				+ ", purpose='" + purpose + '\'' + ", used=" + used + ", expiresAt=" + expiresAt + '}';
	}

	public boolean verify(String inputCode) {
		if (!isValid()) {
			return false;
		}

		incrementAttempts();

		if (code.equals(inputCode)) {
			this.used = true;
			this.usedAt = LocalDateTime.now();
			return true;
		}

		return false;
	}
}