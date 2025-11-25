package pinup.backend.conquer.query.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/dummy_conquer_stat.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class ConquerStatControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser
    @DisplayName("전체 정복 지역 수 조회 - 성공")
    void userTotalConqueredRegion_returnsCount() throws Exception {
        mockMvc.perform(get("/conquer/{userId}/stats/total", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("3"));
    }

    @Test
    @WithMockUser
    @DisplayName("이번 달 정복 지역 수 조회 - 성공")
    void userMonthlyConqueredRegion_returnsCurrentMonthCount() throws Exception {
        mockMvc.perform(get("/conquer/{userId}/stats/monthly", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("2"));
    }
}
