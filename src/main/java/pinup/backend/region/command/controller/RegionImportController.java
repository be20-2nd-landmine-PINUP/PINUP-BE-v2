package pinup.backend.region.command.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pinup.backend.region.command.service.RegionGeoJsonImportService;

import java.util.Map;

@RestController
@RequestMapping("/regions/import")
@RequiredArgsConstructor
public class RegionImportController {

    private final RegionGeoJsonImportService regionGeoJsonImportService;

    @PostMapping("/geojson")
    public ResponseEntity<Map<String, Long>> importGeoJson() {
        long imported = regionGeoJsonImportService.importRegions();
        return ResponseEntity.ok(Map.of("imported", imported));
    }
}
