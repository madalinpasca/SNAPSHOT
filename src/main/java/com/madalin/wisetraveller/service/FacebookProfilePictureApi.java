package com.madalin.wisetraveller.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.madalin.wisetraveller.model.FacebookProfilePictureResponse;
import com.madalin.wisetraveller.model.enums.FacebookProfilePictureType;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

public class FacebookProfilePictureApi {
    private ObjectMapper mapper = new ObjectMapper();
    private RestTemplate client = new RestTemplate();
    private static final String url = "https://graph.facebook.com/{id}/picture?type={type}&redirect=false";

    public FacebookProfilePictureResponse getProfilePicture(String id, FacebookProfilePictureType type) {
        Map<String,String> args = new HashMap<>();
        args.put("id", id);
        args.put("type", type.asFacebookParameter());
        try {
            String json = client.getForObject(url, String.class, args);
            return mapper.readValue(json, FacebookProfilePictureResponse.class);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}

