package pinup.backend.feed.command.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
@NoArgsConstructor
@Schema(description = "피드 작성 요청 DTO")
public class FeedCreateRequest {

    @Schema(description = "작성자 고유식별자", example = "1")
    private Long userId;

    @Schema(description = "피드 제목", example = "피드 작성용 예시제목")
    private String title;

    @Schema(description = "피드 내용", example = "내용 예시 아아아아아아아")
    private String content;

    @Schema(description = "첨부 이미지 파일", type = "string", format = "binary")
    private MultipartFile imageFile;
}