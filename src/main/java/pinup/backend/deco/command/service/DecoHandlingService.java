package pinup.backend.deco.command.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pinup.backend.deco.command.repository.DecoHandleRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class DecoHandlingService {

    private static final int DEFAULT_SRID = 4326;

    private final DecoHandleRepository decoHandleRepository;
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), DEFAULT_SRID);

    public void equipItem(Long userId, Integer itemId, double x, double y) {
        Point coordinates = createPoint(x, y);
        int updated = decoHandleRepository.equipItem(userId, itemId, coordinates);
        validateUpdated(userId, itemId, updated);
    }

    public void removeItem(Long userId, Integer itemId) {
        int updated = decoHandleRepository.unequipItem(userId, itemId);
        validateUpdated(userId, itemId, updated);
    }

    private Point createPoint(double x, double y) {
        Point point = geometryFactory.createPoint(new Coordinate(x, y));
        point.setSRID(DEFAULT_SRID);
        return point;
    }

    private void validateUpdated(Long userId, Integer itemId, int updatedRows) {
        if (updatedRows == 0) {
            throw new EntityNotFoundException(
                    String.format("Inventory not found for userId=%d, itemId=%d", userId, itemId)
            );
        }
    }
}
