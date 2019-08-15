package com.madalin.wisetraveller.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FacebookProfilePictureResponse {
    private FacebookProfilePictureData data;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class FacebookProfilePictureData {
        private String url;
        private Integer height;
        private Integer width;
        @JsonProperty("is_silhouette")
        private Boolean isSilhouette;
    }
}
