package pinup.backend.feed.command.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
@NoArgsConstructor
@Schema(description = "피드 수정 요청 DTO")
public class FeedUpdateRequest {

    @Schema(description = "수정할 피드 제목", example = "수정할 제목아무말대잔치")
    private String title;

    @Schema(description = "수정할 피드 내용", example = "아아아아아에서 가가가가가로 경상도식 수정")
    private String content;

    @Schema(description = "수정할 이미지 파일", type = "string", format = "binary")
    private MultipartFile imageFile;

    public FeedUpdateRequest(String title, String content, MultipartFile imageFile) {
        this.title = title;
        this.content = content;
        this.imageFile = imageFile;
    }
}
