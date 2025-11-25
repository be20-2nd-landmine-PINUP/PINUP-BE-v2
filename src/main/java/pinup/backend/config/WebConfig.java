package pinup.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${feed.image.base-path:uploads/feeds}")
    private String basePath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // <프로젝트>/uploads/feeds → <프로젝트>/uploads 잡기
        Path uploadRoot = Paths.get(basePath).toAbsolutePath().getParent();

        String location = "file:" + uploadRoot.toString() + "/";  // ← 슬래시 필수!!

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(location);
    }
}
