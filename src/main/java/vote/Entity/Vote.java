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
@Table(name = "votes", uniqueConstraints = { @UniqueConstraint(columnNames = { "election_id", "voter_registry_id" }) })
public class Vote extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "election_id", nullable = false)
	private Election election;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "candidate_id", nullable = false)
	private Candidate candidate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "voter_registry_id", nullable = false)
	private VoterRegistry voterRegistry;

	@Column(name = "cast_at", nullable = false)
	private LocalDateTime castAt;

	@Column(name = "ip_address")
	private String ipAddress;

	@Column(name = "user_agent")
	private String userAgent;

	@Column(name = "is_anonymous", nullable = false)
	private boolean anonymous = false;

	@Column(name = "write_in_candidate_name")
	private String writeInCandidateName;

	@Column(name = "verification_method")
	private String verificationMethod; // OTP, BIOMETRIC, etc.

	// Default constructor
	public Vote() {
		this.castAt = LocalDateTime.now();
	}

	// Parameterized constructor
	public Vote(Election election, Candidate candidate, VoterRegistry voterRegistry) {
		this.election = election;
		this.candidate = candidate;
		this.voterRegistry = voterRegistry;
		this.castAt = LocalDateTime.now();
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

		Vote vote = (Vote) o;

		return getId() != null ? getId().equals(vote.getId()) : vote.getId() == null;
	}

	public Candidate getCandidate() {
		return candidate;
	}

	public LocalDateTime getCastAt() {
		return castAt;
	}

	// Getters and Setters
	public Election getElection() {
		return election;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public String getVerificationMethod() {
		return verificationMethod;
	}

	public String getVoterIdentifier() {
		if (voterRegistry != null) {
			if (voterRegistry.getMatricNumber() != null) {
				return voterRegistry.getMatricNumber();
			} else if (voterRegistry.getEmail() != null) {
				return voterRegistry.getEmail();
			} else if (voterRegistry.getPhone() != null) {
				return voterRegistry.getPhone();
			}
		}
		return "Unknown";
	}

	public VoterRegistry getVoterRegistry() {
		return voterRegistry;
	}

	public String getWriteInCandidateName() {
		return writeInCandidateName;
	}

	@Override
	public int hashCode() {
		return getId() != null ? getId().hashCode() : 0;
	}

	public boolean isAnonymous() {
		return anonymous;
	}

	// Helper methods
	public boolean isWriteInVote() {
		return candidate != null && candidate.isWriteIn();
	}

	public void setAnonymous(boolean anonymous) {
		this.anonymous = anonymous;
	}

	public void setCandidate(Candidate candidate) {
		this.candidate = candidate;
	}

	public void setCastAt(LocalDateTime castAt) {
		this.castAt = castAt;
	}

	public void setElection(Election election) {
		this.election = election;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public void setVerificationMethod(String verificationMethod) {
		this.verificationMethod = verificationMethod;
	}

	public void setVoterRegistry(VoterRegistry voterRegistry) {
		this.voterRegistry = voterRegistry;
	}

	public void setWriteInCandidateName(String writeInCandidateName) {
		this.writeInCandidateName = writeInCandidateName;
	}

	// toString method
	@Override
	public String toString() {
		return "Vote{" + "id=" + getId() + ", castAt=" + castAt + ", electionId="
				+ (election != null ? election.getId() : "null") + ", candidateId="
				+ (candidate != null ? candidate.getId() : "null") + ", voterRegistryId="
				+ (voterRegistry != null ? voterRegistry.getId() : "null") + '}';
	}
}