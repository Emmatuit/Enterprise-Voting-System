package vote.Repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vote.Entity.Candidate;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Long> {

    List<Candidate> findByElectionId(Long electionId);

    List<Candidate> findByElectionIdAndActiveTrue(Long electionId);

    List<Candidate> findByElectionIdAndPosition(Long electionId, String position);

    @Query("SELECT c FROM Candidate c WHERE c.election.id = :electionId ORDER BY c.voteCount DESC")
    List<Candidate> findByElectionIdOrderByVoteCountDesc(@Param("electionId") Long electionId);

    long countByElectionId(Long electionId);

    long countByElectionIdAndActiveTrue(Long electionId);

    @Query("SELECT c FROM Candidate c WHERE c.election.id = :electionId " +
           "AND (c.name LIKE %:searchTerm% OR c.position LIKE %:searchTerm% OR " +
           "c.partyAffiliation LIKE %:searchTerm%)")
    List<Candidate> searchCandidates(@Param("electionId") Long electionId,
                                    @Param("searchTerm") String searchTerm);

    @Query("SELECT c FROM Candidate c WHERE c.election.id = :electionId AND c.active = true " +
           "AND c.position = :position ORDER BY c.voteCount DESC")
    List<Candidate> findCandidatesByPosition(@Param("electionId") Long electionId,
                                            @Param("position") String position);

    @Query("SELECT c FROM Candidate c WHERE c.election.id = :electionId AND c.writeIn = true")
    List<Candidate> findWriteInCandidates(@Param("electionId") Long electionId);

    @Query("SELECT c.position, COUNT(c) FROM Candidate c WHERE c.election.id = :electionId " +
           "AND c.active = true GROUP BY c.position")
    List<Object[]> countCandidatesByPosition(@Param("electionId") Long electionId);

    @Query("SELECT c FROM Candidate c WHERE c.election.organization.id = :orgId")
    List<Candidate> findByOrganizationId(@Param("orgId") Long organizationId);
}