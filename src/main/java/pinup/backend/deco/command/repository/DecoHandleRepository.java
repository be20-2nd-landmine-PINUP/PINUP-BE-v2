package pinup.backend.deco.command.repository;

import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pinup.backend.store.command.domain.Inventory;
import pinup.backend.store.command.domain.InventoryKey;

public interface DecoHandleRepository extends JpaRepository<Inventory, InventoryKey> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            UPDATE Inventory i
               SET i.isEquipped = true,
                   i.equippedCoordinates = :coordinates
             WHERE i.users.userId = :userId
               AND i.store.itemId = :itemId
            """)
    int equipItem(@Param("userId") Long userId,
                  @Param("itemId") Integer itemId,
                  @Param("coordinates") Point coordinates);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            UPDATE Inventory i
               SET i.isEquipped = false,
                   i.equippedCoordinates = null
             WHERE i.users.userId = :userId
               AND i.store.itemId = :itemId
            """)
    int unequipItem(@Param("userId") Long userId,
                    @Param("itemId") Integer itemId);
}
