package com.lastcalleats.community.post.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class PostResponse {

    private Long id;
    private Long userId;
    private String userNickname;
    private String userAvatarUrl;
    private Long merchantId;
    private String merchantName;
    private String content;
    private List<String> imageUrls;
    private Integer likeCount;
    private Integer commentCount;
    private LocalDateTime createdAt;
}
