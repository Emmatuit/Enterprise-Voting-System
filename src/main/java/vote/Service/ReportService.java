//package vote.Service;
//
//
//
//import java.time.LocalDate;
//import java.util.List;
//
//public interface ReportService {
//
//    ElectionResultResponse getElectionResults(Long electionId);
//
//    List<ElectionResultResponse> getOrganizationElectionResults(Long organizationId);
//
//    VotingStatsResponse getVotingStats(Long organizationId, LocalDate startDate, LocalDate endDate);
//
//    OrganizationStatsResponse getOrganizationStats(Long organizationId);
//
//    List<Object[]> getVoterTurnoutTrend(Long organizationId, int days);
//
//    List<Object[]> getCandidatePerformance(Long electionId);
//
//    List<Object[]> getVotingPatterns(Long electionId);
//
//    byte[] generateElectionReport(Long electionId);
//
//    byte[] generateVoterRegistryReport(Long organizationId);
//
//    byte[] generateAuditReport(Long organizationId, LocalDate startDate, LocalDate endDate);
//}
