package pinup.backend.store.command.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pinup.backend.conquer.command.domain.entity.Region;

import java.util.Optional;

public interface RegionRepository extends JpaRepository<Region, Long> {
    Optional<Region> findByRegionName(String regionName);
}
