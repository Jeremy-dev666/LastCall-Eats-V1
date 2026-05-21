package com.lastcalleats.common.storage;

import com.lastcalleats.common.exception.BusinessException;
import com.lastcalleats.common.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component
public class LocalStorageStrategy implements StorageStrategy {

    @Value("${storage.local.base-path:./uploads}")
    private String basePath;

    @Value("${storage.local.url-prefix:/uploads}")
    private String urlPrefix;

    @Override
    public String upload(MultipartFile file, String folder) {
        String originalFilename = file.getOriginalFilename();
        String extension = (originalFilename != null && originalFilename.contains("."))
                ? originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
        String newFilename = UUID.randomUUID() + extension;

        Path targetDir = Paths.get(basePath, folder);
        Path targetFile = targetDir.resolve(newFilename);

        try {
            Files.createDirectories(targetDir);
            file.transferTo(targetFile.toFile());
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "File upload failed");
        }

        return urlPrefix + "/" + folder + "/" + newFilename;
    }
}
