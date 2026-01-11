package vote.Entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import vote.Enum.ElectionStatus;

@Entity
@Table(name = "elections")
public class Election extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "organization_id", nullable = false)
	private Organization organization;

	@Column(name = "title", nullable = false)
	private String title;

	@Column(name = "description", columnDefinition = "TEXT")
	private String description;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private ElectionStatus status = ElectionStatus.DRAFT;

	@Column(name = "start_time", nullable = false)
	private LocalDateTime startTime;

	@Column(name = "end_time", nullable = false)
	private LocalDateTime endTime;

	@Column(name = "results_published", nullable = false)
	private boolean resultsPublished = false;

	@Column(name = "voter_turnout")
	private Integer voterTurnout = 0;

	@Column(name = "total_voters")
	private Integer totalVoters = 0;

	@Column(name = "max_votes_per_voter")
	private Integer maxVotesPerVoter = 1;

	@Column(name = "allow_write_in", nullable = false)
	private boolean allowWriteIn = false;

	@Column(name = "require_photo_id", nullable = false)
	private boolean requirePhotoId = false;

	@OneToMany(mappedBy = "election", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Candidate> candidates = new ArrayList<>();

	@OneToMany(mappedBy = "election", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Vote> votes = new ArrayList<>();

	// Default constructor
	public Election() {
	}

	// Parameterized constructor
	public Election(Organization organization, String title, String description, LocalDateTime startTime,
			LocalDateTime endTime) {
		this.organization = organization;
		this.title = title;
		this.description = description;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	// Helper methods
	public void addCandidate(Candidate candidate) {
		candidates.add(candidate);
		candidate.setElection(this);
	}

	public void addVote(Vote vote) {
		votes.add(vote);
		vote.setElection(this);
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

		Election election = (Election) o;

		return getId() != null ? getId().equals(election.getId()) : election.getId() == null;
	}

	public List<Candidate> getCandidates() {
		return candidates;
	}

	public String getDescription() {
		return description;
	}

	public LocalDateTime getEndTime() {
		return endTime;
	}

	public Integer getMaxVotesPerVoter() {
		return maxVotesPerVoter;
	}

	// Getters and Setters
	public Organization getOrganization() {
		return organization;
	}

	public long getRemainingDays() {
		if (hasEnded()) {
			return 0;
		}
		LocalDateTime now = LocalDateTime.now();
		return java.time.Duration.between(now, endTime).toDays();
	}

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public ElectionStatus getStatus() {
		return status;
	}

	public String getTitle() {
		return title;
	}

	public Integer getTotalVoters() {
		return totalVoters;
	}

	public Integer getVoterTurnout() {
		return voterTurnout;
	}

	public List<Vote> getVotes() {
		return votes;
	}

	public boolean hasEnded() {
		return LocalDateTime.now().isAfter(endTime);
	}

	@Override
	public int hashCode() {
		return getId() != null ? getId().hashCode() : 0;
	}

	public boolean hasStarted() {
		return LocalDateTime.now().isAfter(startTime);
	}

	public boolean isActive() {
		return status == ElectionStatus.ACTIVE;
	}

	public boolean isAllowWriteIn() {
		return allowWriteIn;
	}

	public boolean isCompleted() {
		return status == ElectionStatus.COMPLETED;
	}

	public boolean isDraft() {
		return status == ElectionStatus.DRAFT;
	}

	public boolean isOngoing() {
		LocalDateTime now = LocalDateTime.now();
		return isActive() && now.isAfter(startTime) && now.isBefore(endTime);
	}

	public boolean isRequirePhotoId() {
		return requirePhotoId;
	}

	public boolean isResultsPublished() {
		return resultsPublished;
	}

	public void removeCandidate(Candidate candidate) {
		candidates.remove(candidate);
		candidate.setElection(null);
	}

	public void setAllowWriteIn(boolean allowWriteIn) {
		this.allowWriteIn = allowWriteIn;
	}

	public void setCandidates(List<Candidate> candidates) {
		this.candidates = candidates;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}

	public void setMaxVotesPerVoter(Integer maxVotesPerVoter) {
		this.maxVotesPerVoter = maxVotesPerVoter;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	public void setRequirePhotoId(boolean requirePhotoId) {
		this.requirePhotoId = requirePhotoId;
	}

	public void setResultsPublished(boolean resultsPublished) {
		this.resultsPublished = resultsPublished;
	}

	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}

	public void setStatus(ElectionStatus status) {
		this.status = status;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setTotalVoters(Integer totalVoters) {
		this.totalVoters = totalVoters;
	}

	public void setVoterTurnout(Integer voterTurnout) {
		this.voterTurnout = voterTurnout;
	}

	public void setVotes(List<Vote> votes) {
		this.votes = votes;
	}

	// toString method
	@Override
	public String toString() {
		return "Election{" + "id=" + getId() + ", title='" + title + '\'' + ", status=" + status + ", organizationId="
				+ (organization != null ? organization.getId() : "null") + '}';
	}

	public void updateVoterTurnout() {
		if (totalVoters > 0) {
			double turnout = (double) votes.size() / totalVoters * 100;
			this.voterTurnout = (int) Math.round(turnout);
		}
	}
}