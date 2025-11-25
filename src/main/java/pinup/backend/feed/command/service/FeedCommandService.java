package pinup.backend.feed.command.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pinup.backend.feed.command.dto.FeedCreateRequest;
import pinup.backend.feed.command.dto.FeedUpdateRequest;
import pinup.backend.feed.command.entity.Feed;
import pinup.backend.feed.command.repository.FeedLikeRepository;
import pinup.backend.feed.command.repository.FeedRepository;
import pinup.backend.feed.command.service.ImageUploadResult;
import pinup.backend.feed.common.exception.FeedNotFoundException;
import pinup.backend.member.command.domain.Users;
import pinup.backend.member.command.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class FeedCommandService {

    private final FeedRepository feedRepository;
    private final UserRepository userRepository;
    private final FeedLikeRepository feedLikeRepository; // ★ 추가
    private final FeedImageService feedImageService;

    public Long createFeed(FeedCreateRequest request) {
        Users user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        ImageUploadResult uploadResult = feedImageService.storeImage(request.getImageFile());

        Feed feed = Feed.builder()
                .user(user)
                .title(request.getTitle())
                .content(request.getContent())
                .imageUrl(uploadResult.imageUrl())
                .thumbnailUrl(uploadResult.thumbnailUrl())
                .build();

        feedRepository.save(feed);
        return feed.getFeedId();
    }

    public void updateFeed(Long feedId, FeedUpdateRequest request) {
        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new FeedNotFoundException(feedId));

        ImageUploadResult uploadResult = feedImageService.storeImage(request.getImageFile());
        String newImageUrl = uploadResult.imageUrl();
        String newThumbnailUrl = uploadResult.thumbnailUrl();

        if (newImageUrl != null || newThumbnailUrl != null) {
            feedImageService.deleteImages(feed.getImageUrl(), feed.getThumbnailUrl());
        }

        feed.update(request.getTitle(), request.getContent(), newImageUrl, newThumbnailUrl);
    }

    public void deleteFeed(Long feedId) {
        // 1) 좋아요 선삭제 (FK 제약 회피)
        feedLikeRepository.deleteAllByFeedLikeId_FeedId(feedId);

        // 2) 피드 삭제
        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new FeedNotFoundException(feedId));
        feedImageService.deleteImages(feed.getImageUrl(), feed.getThumbnailUrl());
        feedRepository.delete(feed);
    }
}