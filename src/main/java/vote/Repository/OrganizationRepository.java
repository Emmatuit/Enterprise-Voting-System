package vote.Repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vote.Entity.Organization;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {

    Optional<Organization> findByName(String name);

    Optional<Organization> findByCode(String code);

    boolean existsByName(String name);

    boolean existsByCode(String code);

    List<Organization> findByActiveTrue();

    List<Organization> findByActiveFalse();

    @Query("SELECT o FROM Organization o WHERE o.active = :active AND LOWER(o.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Organization> searchOrganizations(@Param("searchTerm") String searchTerm, @Param("active") boolean active);

    @Query("SELECT COUNT(o) FROM Organization o WHERE o.active = true")
    long countActiveOrganizations();
}