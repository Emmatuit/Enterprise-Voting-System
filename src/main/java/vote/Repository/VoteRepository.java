package vote.Repository;


import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vote.Entity.Vote;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {

    List<Vote> findByElectionId(Long electionId);

    List<Vote> findByCandidateId(Long candidateId);

    List<Vote> findByVoterRegistryId(Long voterRegistryId);

    @Query("SELECT v FROM Vote v WHERE v.election.id = :electionId AND v.voterRegistry.id = :voterRegistryId")
    List<Vote> findByElectionAndVoter(@Param("electionId") Long electionId,
                                     @Param("voterRegistryId") Long voterRegistryId);

    @Query("SELECT COUNT(v) FROM Vote v WHERE v.election.id = :electionId")
    long countByElectionId(@Param("electionId") Long electionId);

    @Query("SELECT COUNT(v) FROM Vote v WHERE v.candidate.id = :candidateId")
    long countByCandidateId(@Param("candidateId") Long candidateId);

    @Query("SELECT COUNT(v) FROM Vote v WHERE v.election.organization.id = :organizationId")
    long countByOrganizationId(@Param("organizationId") Long organizationId);

    boolean existsByElectionIdAndVoterRegistryId(Long electionId, Long voterRegistryId);

    @Query("SELECT v FROM Vote v WHERE v.election.id = :electionId " +
           "AND v.candidate.id = :candidateId " +
           "ORDER BY v.castAt DESC")
    List<Vote> findVotesByElectionAndCandidate(@Param("electionId") Long electionId,
                                              @Param("candidateId") Long candidateId);

    @Query("SELECT v FROM Vote v WHERE v.election.id = :electionId AND v.castAt BETWEEN :startDate AND :endDate")
    List<Vote> findVotesByTimeRange(@Param("electionId") Long electionId,
                                   @Param("startDate") LocalDateTime startDate,
                                   @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(v) FROM Vote v WHERE v.election.id = :electionId " +
           "AND v.castAt BETWEEN :startDate AND :endDate")
    long countVotesByTimeRange(@Param("electionId") Long electionId,
                              @Param("startDate") LocalDateTime startDate,
                              @Param("endDate") LocalDateTime endDate);

    @Query("SELECT v.ipAddress, COUNT(v) FROM Vote v WHERE v.election.id = :electionId " +
           "GROUP BY v.ipAddress HAVING COUNT(v) > 1")
    List<Object[]> findDuplicateIPVotes(@Param("electionId") Long electionId);

    @Query("SELECT v FROM Vote v WHERE v.anonymous = false AND v.election.id = :electionId " +
           "ORDER BY v.castAt DESC")
    List<Vote> findNonAnonymousVotes(@Param("electionId") Long electionId);

    @Query("SELECT COUNT(DISTINCT v.voterRegistry.id) FROM Vote v WHERE v.election.id = :electionId")
    long countUniqueVoters(@Param("electionId") Long electionId);
}