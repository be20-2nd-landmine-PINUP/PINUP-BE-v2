package pinup.backend.feed.command.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;

import javax.imageio.ImageIO;

@Service
@RequiredArgsConstructor
public class FeedImageService {

    @Value("${feed.image.base-path:uploads/feeds}")
    private String basePath;

    private static final String THUMBNAIL_SUFFIX = "_s";
    private static final int THUMBNAIL_SIZE = 300;

    public ImageUploadResult storeImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return new ImageUploadResult(null, null);
        }

        String originalFilename = sanitizeFilename(Objects.requireNonNull(file.getOriginalFilename(), "이미지 파일 이름이 비어있습니다."));
        String extension = extractExtension(originalFilename);
        String uniqueBaseName = UUID.randomUUID() + "_" + removeExtension(originalFilename);

        Path uploadDir = Paths.get(basePath).toAbsolutePath().normalize();
        createDirectoriesIfNeeded(uploadDir);

        Path originalPath = uploadDir.resolve(uniqueBaseName + "." + extension);
        Path thumbnailPath = appendThumbnailSuffix(originalPath);

        try {
            file.transferTo(originalPath);
            writeThumbnail(originalPath, thumbnailPath, extension);
        } catch (IOException e) {
            throw new IllegalStateException("이미지 저장 중 오류가 발생했습니다.", e);
        }

        return new ImageUploadResult(originalPath.toString(), appendThumbnailSuffix(originalPath.toString()));
    }

    public void deleteImages(String imageUrl, String thumbnailUrl) {
        deleteIfExists(imageUrl);
        deleteIfExists(thumbnailUrl);
    }

    public String appendThumbnailSuffix(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            return imageUrl;
        }

        int dotIndex = imageUrl.lastIndexOf('.');
        if (dotIndex == -1) {
            return imageUrl + THUMBNAIL_SUFFIX;
        }

        String name = imageUrl.substring(0, dotIndex);
        String extension = imageUrl.substring(dotIndex);
        return name + THUMBNAIL_SUFFIX + extension;
    }

    private void deleteIfExists(String path) {
        if (path == null || path.isBlank()) {
            return;
        }
        try {
            Files.deleteIfExists(Paths.get(path));
        } catch (IOException ignored) {
            // 삭제 실패 시 로그 시스템을 붙이면 추적 가능 (요구사항에 따라 생략)
        }
    }

    private Path appendThumbnailSuffix(Path originalPath) {
        String thumbnailName = appendThumbnailSuffix(originalPath.toString());
        return Paths.get(thumbnailName);
    }

    private void writeThumbnail(Path originalPath, Path thumbnailPath, String extension) throws IOException {
        BufferedImage originalImage = ImageIO.read(originalPath.toFile());
        if (originalImage == null) {
            throw new IllegalStateException("지원하지 않는 이미지 형식입니다.");
        }

        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        int targetWidth;
        int targetHeight;
        if (width >= height) {
            targetWidth = THUMBNAIL_SIZE;
            targetHeight = (int) ((double) height / width * THUMBNAIL_SIZE);
        } else {
            targetHeight = THUMBNAIL_SIZE;
            targetWidth = (int) ((double) width / height * THUMBNAIL_SIZE);
        }

        Image scaled = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        BufferedImage thumbnail = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = thumbnail.createGraphics();
        g2d.drawImage(scaled, 0, 0, null);
        g2d.dispose();

        ImageIO.write(thumbnail, extension, thumbnailPath.toFile());
    }

    private String extractExtension(String filename) {
        int idx = filename.lastIndexOf('.');
        if (idx == -1 || idx == filename.length() - 1) {
            throw new IllegalArgumentException("이미지 확장자를 확인할 수 없습니다.");
        }
        return filename.substring(idx + 1).toLowerCase();
    }

    private String removeExtension(String filename) {
        int idx = filename.lastIndexOf('.');
        return (idx == -1) ? filename : filename.substring(0, idx);
    }

    private String sanitizeFilename(String filename) {
        return filename.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    private void createDirectoriesIfNeeded(Path uploadDir) {
        try {
            Files.createDirectories(uploadDir);
        } catch (IOException e) {
            throw new IllegalStateException("이미지 저장 경로를 생성할 수 없습니다.", e);
        }
    }
}