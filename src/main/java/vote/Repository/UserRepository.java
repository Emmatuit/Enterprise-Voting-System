package vote.Repository;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vote.Entity.User;
import vote.Enum.UserRole;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    List<User> findByOrganizationId(Long organizationId);

    List<User> findByRole(UserRole role);

    List<User> findByOrganizationIdAndRole(Long organizationId, UserRole role);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    List<User> findByActiveTrue();

    List<User> findByActiveFalse();

    List<User> findByLockedTrue();

    @Query("SELECT u FROM User u WHERE u.organization.id = :orgId AND u.active = true AND u.locked = false")
    List<User> findActiveAdminsByOrganization(@Param("orgId") Long organizationId);

    @Query("SELECT COUNT(u) FROM User u WHERE u.organization.id = :orgId AND u.active = true")
    long countActiveUsersByOrganization(@Param("orgId") Long organizationId);

    @Query("SELECT u FROM User u WHERE u.lastLoginAt < :date AND u.active = true")
    List<User> findInactiveUsers(@Param("date") LocalDateTime date);

    @Query("SELECT u FROM User u WHERE u.username LIKE %:searchTerm% OR u.email LIKE %:searchTerm% OR u.fullName LIKE %:searchTerm%")
    List<User> searchUsers(@Param("searchTerm") String searchTerm);
}