package com.bookvillage.backend.config;

import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.nio.file.Paths;

@Configuration
public class TomcatConfig {

    @Value("${file.lab-upload-path:./uploads/lab}")
    private String labUploadPath;

    /**
     * ./uploads/lab 디렉토리를 Tomcat 웹 컨텍스트의 /uploads 경로에 마운트한다.
     * 이 디렉토리에 업로드된 .jsp 파일은 /uploads/{filename}.jsp 로 접근 시
     * Tomcat Jasper 엔진에 의해 컴파일·실행된다.
     */
    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> labUploadDirCustomizer() {
        return factory -> factory.addContextCustomizers(context -> {
            File labDir = Paths.get(labUploadPath).toAbsolutePath().toFile();
            labDir.mkdirs();

            WebResourceRoot resources = new StandardRoot(context);

            // /uploads URL 경로 → ./uploads/lab 실제 디렉토리 매핑
            resources.addPreResources(
                    new DirResourceSet(resources, "/uploads", labDir.getAbsolutePath(), "/")
            );

            context.setResources(resources);
        });
    }
}
