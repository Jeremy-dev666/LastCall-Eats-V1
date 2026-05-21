package com.lastcalleats.common.storage;

import org.springframework.web.multipart.MultipartFile;

public interface StorageStrategy {

    String upload(MultipartFile file, String folder);
}
