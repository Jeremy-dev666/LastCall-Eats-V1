package com.lastcalleats.community.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class CreatePostRequest {

    @NotBlank
    @Size(max = 1000)
    private String content;

    private Long merchantId;

    @Size(max = 9)
    private List<String> imageUrls;
}
