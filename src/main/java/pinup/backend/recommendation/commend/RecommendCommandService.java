package pinup.backend.recommendation.commend;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pinup.backend.member.command.domain.Users;
import pinup.backend.member.command.repository.UserRepository;
import pinup.backend.recommendation.domain.RecommendRepository;
import pinup.backend.recommendation.domain.Recommend;
import pinup.backend.recommendation.query.RecommendationResponseDTO;

@Service
@RequiredArgsConstructor
public class RecommendCommandService {

    private final RecommendRepository recommendRepository;
    private final UserRepository userRepository;

    public void saveRecommendation(Long userId, RecommendationResponseDTO response) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다. userId=" + userId));


        Recommend recommend = Recommend.builder()
                .user(user)
                .recommendSpot(response.getRegion())     // "부산광역시"
                .regionId(response.getRegionId())
                .reason(response.getDescription())
                .build();

        recommendRepository.save(recommend);
    }
}