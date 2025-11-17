package pinup.backend.recommend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import pinup.backend.member.command.domain.Users;
import pinup.backend.member.command.repository.UserRepository;
import pinup.backend.recommendation.commend.RecommendRepository;
import pinup.backend.recommendation.common.config.OllamaClient;
import pinup.backend.recommendation.entity.Recommend;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
class RecommendControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RecommendRepository recommendRepository;

    // 🔥 여기서 실제 OllamaClient 대신 mock 주입
    @MockBean
    OllamaClient ollamaClient;

    @Test
    @Rollback
    void 추천_API_호출시_응답과_DB저장이_정상동작한다() throws Exception {
        // given - 테스트용 유저 한 명 저장
        Users user = Users.builder()
                .loginType(Users.LoginType.GOOGLE)          // ✅ NOT NULL
                .name("테스터")                             // ✅ user_name
                .email("tester@example.com")                // ✅ email
                .nickname("tester")                         // ✅ nickname
                .gender(Users.Gender.F)                     // ✅ gender
                .birthDate(LocalDate.of(2000, 1, 1))        // ✅ birth_date
                .preferredCategory(Users.PreferredCategory.자연) // ✅ enum
                .preferredSeason(Users.PreferredSeason.겨울)     // ✅ enum
                .status(Users.Status.ACTIVE)                // 🔥 builder 쓴다면 직접 넣어주는 게 안전
                .build();

        userRepository.save(user);


        // 🔥 여기서부터 mock 동작 정의
        // 어떤 prompt가 오든 간에, 항상 똑같은 결과 문자열 리턴
        given(ollamaClient.generate(anyString()))
                .willReturn("북한산 둘레길|||차분한 겨울 산책|||겨울 공기를 느끼며 걷기 좋은 코스입니다.|||0");

        // when - 실제 API 호출처럼 MockMvc로 POST
        mockMvc.perform(post("/api/recommend/{userId}", user.getUserId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.region").value("북한산 둘레길"))
                .andExpect(jsonPath("$.title").value("차분한 겨울 산책"));

        // then - DB에 잘 저장되었는지 확인
        List<Recommend> all = recommendRepository.findAll();
        assertThat(all).hasSize(1);
        assertThat(all.get(0).getRecommendSpot()).isEqualTo("북한산 둘레길");
        assertThat(all.get(0).getReason()).contains("겨울 공기를");
    }
}
