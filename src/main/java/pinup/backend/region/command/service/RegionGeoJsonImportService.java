package pinup.backend.region.command.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.geojson.GeoJsonReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pinup.backend.conquer.command.domain.entity.Region;
import pinup.backend.store.command.repository.RegionRepository;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegionGeoJsonImportService {

    private static final Duration REQUEST_TIMEOUT = Duration.ofMinutes(2);

    @Value("${pinup.region.geojson-url:https://raw.githubusercontent.com/vuski/admdongkor/master/ver20250401/HangJeongDong_ver20250401.geojson}")
    private String geoJsonUrl;

    private final RegionRepository regionRepository;
    private final ObjectMapper objectMapper;
    private final GeoJsonReader geoJsonReader = new GeoJsonReader();
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(30))
            .build();

    @Transactional
    public long importRegions() {
        String payload = fetchGeoJson();
        ArrayNode features = extractFeatures(payload);
        List<Region> regions = new ArrayList<>(features.size());

        for (JsonNode feature : features) {
            JsonNode properties = feature.get("properties");
            JsonNode geometryNode = feature.get("geometry");

            if (properties == null || geometryNode == null) {
                continue;
            }

            Geometry geometry = readGeometry(geometryNode);
            String depth1 = textValue(properties, "sidonm");
            String depth2 = textValue(properties, "sggnm");
            String name = textValue(properties, "adm_nm");

            regions.add(
                    Region.builder()
                            .regionName(name)
                            .regionDepth1(depth1)
                            .regionDepth2(depth2)
                            .regionDepth3(extractDepth3(name, depth1, depth2))
                            .geom(geometry)
                            .build()
            );
        }

        regionRepository.deleteAllInBatch();
        return regionRepository.saveAll(regions).size();
    }

    private String fetchGeoJson() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(geoJsonUrl))
                .timeout(REQUEST_TIMEOUT)
                .GET()
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() != 200) {
                throw new IllegalStateException("Failed to download GeoJSON. status=%d".formatted(response.statusCode()));
            }
            return response.body();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("GeoJSON download interrupted", e);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to download GeoJSON data", e);
        }
    }

    private ArrayNode extractFeatures(String payload) {
        try {
            JsonNode root = objectMapper.readTree(payload);
            JsonNode featuresNode = root.get("features");
            if (featuresNode == null || !featuresNode.isArray()) {
                throw new IllegalStateException("GeoJSON does not contain features");
            }
            return (ArrayNode) featuresNode;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to parse GeoJSON", e);
        }
    }

    private Geometry readGeometry(JsonNode geometryNode) {
        try {
            Geometry geometry = geoJsonReader.read(geometryNode.toString());
            geometry.setSRID(4326);
            return geometry;
        } catch (ParseException e) {
            throw new IllegalStateException("Failed to parse geometry", e);
        }
    }

    private String textValue(JsonNode node, String field) {
        return Optional.ofNullable(node.get(field))
                .filter(JsonNode::isTextual)
                .map(JsonNode::asText)
                .filter(value -> !value.isBlank())
                .orElse(null);
    }

    private String extractDepth3(String fullName, String depth1, String depth2) {
        if (fullName == null) {
            return null;
        }
        String result = fullName.trim();

        if (depth1 != null && !depth1.isBlank() && result.startsWith(depth1)) {
            result = result.substring(depth1.length()).trim();
        }
        if (depth2 != null && !depth2.isBlank() && result.startsWith(depth2)) {
            result = result.substring(depth2.length()).trim();
        }
        return result.isEmpty() ? null : result;
    }
}
