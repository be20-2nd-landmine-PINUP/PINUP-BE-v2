package pinup.backend.recommendation.domain;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pinup.backend.recommendation.query.TourSpot;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TourSpotRepository {

    private final ObjectMapper objectMapper;
    private final List<TourSpot> spots = new ArrayList<>();

    // 애플리케이션 시작할 때 JSON 읽어서 메모리에 로딩
    @javax.annotation.PostConstruct
    public void load() {
        try (InputStream is = getClass().getClassLoader()
                .getResourceAsStream("data/tour_spots.json")) { // 경로: src/main/resources/data/tour_spots.json
            if (is == null) {
                throw new IllegalStateException("data/tour_spots.json not found");
            }
            List<TourSpot> list = objectMapper.readValue(
                    is,
                    new TypeReference<List<TourSpot>>() {}
            );
            spots.clear();
            spots.addAll(list);
            System.out.println("[TourSpotRepository] 로딩 완료: " + spots.size() + "개");
        } catch (Exception e) {
            throw new RuntimeException("tour_spots.json 로딩 실패", e);
        }
    }

    public List<TourSpot> findAll() {
        return spots;
    }
}
