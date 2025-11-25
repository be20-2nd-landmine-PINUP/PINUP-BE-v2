package pinup.backend.deco.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import pinup.backend.deco.command.repository.DecoHandleRepository;
import pinup.backend.store.command.domain.Inventory;
import pinup.backend.store.command.domain.InventoryKey;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/dummy_deco.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class DecoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DecoHandleRepository decoHandleRepository;

    @Test
    @WithMockUser(username = "deco@example.com")
    @DisplayName("시도별 인벤토리 페이지 조회 - 성공")
    void getSidoInventoryPage_returnsItems() throws Exception {
        mockMvc.perform(get("/decorate/regions/{sidoName}/items", "서울특별시")
                        .param("page", "0")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.content[0].itemName").value("서울 건물"))
                .andExpect(jsonPath("$.content[0].imageUrl").value("https://example.com/building.png"))
                .andExpect(jsonPath("$.content[1].itemName").value("서울 마커"));
    }

    @Test
    @WithMockUser(username = "deco@example.com")
    @DisplayName("아이템 장착 API - 좌표 저장 및 상태 변경")
    void equipItem_updatesInventory() throws Exception {
        Map<String, Double> payload = Map.of("x", 128.12, "y", 37.51);

        mockMvc.perform(patch("/decorate/{itemId}/add", 401)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("아이템 장착 완료"))
                .andExpect(jsonPath("$.userId").value("101"))
                .andExpect(jsonPath("$.itemId").value("401"));

        Inventory inventory = decoHandleRepository.findById(new InventoryKey(101L, 401))
                .orElseThrow();
        assertThat(inventory.isEquipped()).isTrue();
        assertThat(inventory.getEquippedCoordinates()).isNotNull();
        assertThat(inventory.getEquippedCoordinates().getX()).isCloseTo(128.12, within(0.0001));
        assertThat(inventory.getEquippedCoordinates().getY()).isCloseTo(37.51, within(0.0001));
    }

    @Test
    @WithMockUser(username = "deco@example.com")
    @DisplayName("아이템 해제 API - 좌표 제거 및 상태 해제")
    void removeItem_updatesInventory() throws Exception {
        mockMvc.perform(patch("/decorate/{itemId}/delete", 402)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("아이템 해제 완료"))
                .andExpect(jsonPath("$.userId").value("101"))
                .andExpect(jsonPath("$.itemId").value("402"));

        Inventory inventory = decoHandleRepository.findById(new InventoryKey(101L, 402))
                .orElseThrow();
        assertThat(inventory.isEquipped()).isFalse();
        assertThat(inventory.getEquippedCoordinates()).isNull();
    }
}
