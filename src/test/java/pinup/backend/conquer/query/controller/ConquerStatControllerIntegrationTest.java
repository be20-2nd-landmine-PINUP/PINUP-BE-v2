package pinup.backend.conquer.query.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/dummy_conquer_stat.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class ConquerStatControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "conquer@example.com")
    @DisplayName("전체 정복 지역 수 조회 - 성공")
    void userTotalConqueredRegion_returnsCount() throws Exception {
        mockMvc.perform(get("/conquer/stats/total"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("3"));
    }

    @Test
    @WithMockUser(username = "conquer@example.com")
    @DisplayName("이번 달 정복 지역 수 조회 - 성공")
    void userMonthlyConqueredRegion_returnsCurrentMonthCount() throws Exception {
        mockMvc.perform(get("/conquer/stats/monthly"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("2"));
    }

    @Test
    @WithMockUser(username = "conquer@example.com")
    @DisplayName("사용자 점령지 목록 조회 - 성공")
    void loadUserTerritoryList_returnsRegions() throws Exception {
        mockMvc.perform(get("/conquer/stats/api/conquer/my-regions"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].regionName").value("서울특별시 성북구"))
                .andExpect(jsonPath("$[0].regionDepth1").value("서울특별시"))
                .andExpect(jsonPath("$[0].regionDepth2").value("성북구"))
                .andExpect(jsonPath("$[0].regionDepth3").value("성북1동"));
    }
}
