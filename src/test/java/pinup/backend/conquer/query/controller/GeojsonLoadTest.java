package pinup.backend.conquer.query.controller;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import pinup.backend.conquer.command.domain.entity.Region;
import pinup.backend.conquer.command.domain.entity.Territory;
import pinup.backend.conquer.command.domain.repository.TerritoryRepository;
import pinup.backend.member.command.domain.Users;
import pinup.backend.member.command.repository.MemberCommandRepository;
import pinup.backend.region.command.service.RegionGeoJsonImportService;
import pinup.backend.store.command.repository.RegionRepository;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GeojsonLoadTest {

    @Autowired
    private RegionGeoJsonImportService regionGeoJsonImportService;
    @Autowired
    private RegionRepository regionRepository;
    @Autowired
    private TerritoryRepository territoryRepository;
    @Autowired
    private MemberCommandRepository memberCommandRepository;
    @Autowired
    private MockMvc mockMvc;

    private HttpServer httpServer;
    private String geoJsonServerUrl;
    private byte[] geoJsonBody;

    @BeforeAll
    void startGeoJsonServer() throws IOException {
        geoJsonBody = Files.readAllBytes(Path.of("src/test/resources/geojson/hangjeongdong_sample.geojson"));
        httpServer = HttpServer.create(new InetSocketAddress(0), 0);
        httpServer.createContext("/geojson", exchange -> {
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, geoJsonBody.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(geoJsonBody);
            }
        });
        httpServer.start();
        geoJsonServerUrl = "http://localhost:" + httpServer.getAddress().getPort() + "/geojson";
    }

    @AfterAll
    void stopGeoJsonServer() {
        if (httpServer != null) {
            httpServer.stop(0);
        }
    }

    @BeforeEach
    void setUp() {
        territoryRepository.deleteAll();
        regionRepository.deleteAll();
        memberCommandRepository.deleteAll();
        ReflectionTestUtils.setField(regionGeoJsonImportService, "geoJsonUrl", geoJsonServerUrl);
    }

    @Test
    @WithMockUser(username = "geojson@example.com")
    @DisplayName("GeoJSON으로 로드된 데이터를 통해 점령지 목록 조회")
    void loadUserTerritoryList_returnsRegionsAfterGeoJsonImport() throws Exception {
        regionGeoJsonImportService.importRegions();

        Region region = regionRepository.findByRegionName("서울특별시 종로구 사직동")
                .orElseThrow(() -> new IllegalStateException("Region not imported from GeoJSON"));

        Users savedUser = memberCommandRepository.save(createUser("geojson@example.com"));

        Date captureStart = Date.from(Instant.now().minus(5, ChronoUnit.DAYS));
        Date captureEnd = Date.from(Instant.now().minus(4, ChronoUnit.DAYS));
        territoryRepository.save(new Territory(0L, savedUser, region, captureStart, captureEnd, 1, null));

        mockMvc.perform(get("/conquer/stats/api/conquer/my-regions"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].regionName").value(region.getRegionName()))
                .andExpect(jsonPath("$[0].regionDepth1").value(region.getRegionDepth1()))
                .andExpect(jsonPath("$[0].regionDepth2").value(region.getRegionDepth2()))
                .andExpect(jsonPath("$[0].regionDepth3").value(region.getRegionDepth3()));
    }

    private Users createUser(String email) {
        return Users.builder()
                .loginType(Users.LoginType.GOOGLE)
                .name("Geojson User")
                .email(email)
                .nickname("geojson")
                .gender(Users.Gender.M)
                .status(Users.Status.ACTIVE)
                .birthDate(LocalDate.of(1990, 1, 1))
                .preferredCategory(Users.PreferredCategory.문화)
                .preferredSeason(Users.PreferredSeason.봄)
                .profileImage(null)
                .build();
    }
}
