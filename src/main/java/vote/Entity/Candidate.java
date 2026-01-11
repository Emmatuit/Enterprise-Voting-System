package vote.Entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "candidates")
public class Candidate extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "election_id", nullable = false)
	private Election election;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "bio", columnDefinition = "TEXT")
	private String bio;

	@Column(name = "photo_url")
	private String photoUrl;

	@Column(name = "position", nullable = false)
	private String position;

	@Column(name = "party_affiliation")
	private String partyAffiliation;

	@Column(name = "is_active", nullable = false)
	private boolean active = true;

	@Column(name = "is_write_in", nullable = false)
	private boolean writeIn = false;

	@Column(name = "vote_count", nullable = false)
	private Integer voteCount = 0;

	@OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Vote> votes = new ArrayList<>();

	// Default constructor
	public Candidate() {
	}

	// Parameterized constructor
	public Candidate(Election election, String name, String position) {
		this.election = election;
		this.name = name;
		this.position = position;
	}

	public void addVote(Vote vote) {
		votes.add(vote);
		vote.setCandidate(this);
		incrementVoteCount();
	}

	public void decrementVoteCount() {
		if (this.voteCount > 0) {
			this.voteCount--;
		}
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

		Candidate candidate = (Candidate) o;

		return getId() != null ? getId().equals(candidate.getId()) : candidate.getId() == null;
	}

	public String getBio() {
		return bio;
	}

	// Getters and Setters
	public Election getElection() {
		return election;
	}

	public String getName() {
		return name;
	}

	public String getPartyAffiliation() {
		return partyAffiliation;
	}

	public String getPhotoUrl() {
		return photoUrl;
	}

	public String getPosition() {
		return position;
	}

	public Integer getVoteCount() {
		return voteCount;
	}

	public List<Vote> getVotes() {
		return votes;
	}

	@Override
	public int hashCode() {
		return getId() != null ? getId().hashCode() : 0;
	}

	// Helper methods
	public void incrementVoteCount() {
		this.voteCount++;
	}

	public boolean isActive() {
		return active;
	}

	public boolean isWriteIn() {
		return writeIn;
	}

	public void removeVote(Vote vote) {
		votes.remove(vote);
		vote.setCandidate(null);
		decrementVoteCount();
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public void setBio(String bio) {
		this.bio = bio;
	}

	public void setElection(Election election) {
		this.election = election;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPartyAffiliation(String partyAffiliation) {
		this.partyAffiliation = partyAffiliation;
	}

	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public void setVoteCount(Integer voteCount) {
		this.voteCount = voteCount;
	}

	public void setVotes(List<Vote> votes) {
		this.votes = votes;
	}

	public void setWriteIn(boolean writeIn) {
		this.writeIn = writeIn;
	}

	// toString method
	@Override
	public String toString() {
		return "Candidate{" + "id=" + getId() + ", name='" + name + '\'' + ", position='" + position + '\''
				+ ", voteCount=" + voteCount + ", electionId=" + (election != null ? election.getId() : "null") + '}';
	}
}