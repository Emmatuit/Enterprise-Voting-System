package vote.Entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "organizations")
public class Organization extends BaseEntity {

	@Column(name = "name", nullable = false, unique = true)
	private String name;

	@Column(name = "code", nullable = false, unique = true, length = 20)
	private String code;

	@Column(name = "description", length = 500)
	private String description;

	@Column(name = "contact_email")
	private String contactEmail;

	@Column(name = "contact_phone")
	private String contactPhone;

	@Column(name = "address")
	private String address;

	@Column(name = "is_active", nullable = false)
	private boolean active = true;

	// One organization can have multiple identity policies (historical)
	@OneToMany(mappedBy = "organization", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<IdentityPolicy> identityPolicies = new ArrayList<>();

	// One organization can have multiple voter registries
	@OneToMany(mappedBy = "organization", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<VoterRegistry> voterRegistries = new ArrayList<>();

	// One organization can have multiple elections
	@OneToMany(mappedBy = "organization", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Election> elections = new ArrayList<>();

	// Default constructor
	public Organization() {
	}

	// Parameterized constructor
	public Organization(String name, String code, String description, String contactEmail, String contactPhone,
			String address, boolean active) {
		this.name = name;
		this.code = code;
		this.description = description;
		this.contactEmail = contactEmail;
		this.contactPhone = contactPhone;
		this.address = address;
		this.active = active;
	}

	// Helper method to add election
	public void addElection(Election election) {
		elections.add(election);
		election.setOrganization(this);
	}

	// Helper method to add identity policy
	public void addIdentityPolicy(IdentityPolicy policy) {
		identityPolicies.add(policy);
		policy.setOrganization(this);
	}

	// Helper method to add voter registry
	public void addVoterRegistry(VoterRegistry registry) {
		voterRegistries.add(registry);
		registry.setOrganization(this);
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

		Organization that = (Organization) o;

		return getId() != null ? getId().equals(that.getId()) : that.getId() == null;
	}

	@PrePersist
	private void generateCode() {
		if (this.code == null || this.code.trim().isEmpty()) {
			this.code = generateOrganizationCode(this.name);
		}
	}

	private String generateOrganizationCode(String name) {
		if (name == null || name.trim().isEmpty()) {
			return "ORG_" + System.currentTimeMillis();
		}

		// Remove special characters and split by spaces
		String normalized = name.replaceAll("[^a-zA-Z0-9\\s]", "");
		String[] words = normalized.split("\\s+");

		StringBuilder codeBuilder = new StringBuilder();

		if (words.length == 1) {
			// Take first 8 characters if single word
			String word = words[0].toUpperCase();
			codeBuilder.append(word.length() > 8 ? word.substring(0, 8) : word);
		} else {
			// Use first letter of each word
			for (String word : words) {
				if (!word.isEmpty()) {
					codeBuilder.append(Character.toUpperCase(word.charAt(0)));
				}
			}
			// Limit to 8 characters
			if (codeBuilder.length() > 8) {
				codeBuilder.setLength(8);
			}
		}

		// Add timestamp to ensure uniqueness
		String timestamp = String.valueOf(System.currentTimeMillis());
		codeBuilder.append("_").append(timestamp.substring(timestamp.length() - 4));

		return codeBuilder.toString();
	}

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

	public String getDescription() {
		return description;
	}

	public List<Election> getElections() {
		return elections;
	}

	public List<IdentityPolicy> getIdentityPolicies() {
		return identityPolicies;
	}

	// Getters and Setters
	public String getName() {
		return name;
	}

	public List<VoterRegistry> getVoterRegistries() {
		return voterRegistries;
	}

	@Override
	public int hashCode() {
		return getId() != null ? getId().hashCode() : 0;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setCode(String code) {
		this.code = code;
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

	public void setElections(List<Election> elections) {
		this.elections = elections;
	}

	public void setIdentityPolicies(List<IdentityPolicy> identityPolicies) {
		this.identityPolicies = identityPolicies;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setVoterRegistries(List<VoterRegistry> voterRegistries) {
		this.voterRegistries = voterRegistries;
	}

	// toString method
	@Override
	public String toString() {
		return "Organization{" + "id=" + getId() + ", name='" + name + '\'' + ", code='" + code + '\'' + ", active="
				+ active + ", createdAt=" + getCreatedAt() + '}';
	}
}