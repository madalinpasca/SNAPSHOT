package com.madalin.wisetraveller.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.madalin.wisetraveller.model.FacebookApiResponse;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FacebookAccessTokenApi {
    private ObjectMapper mapper = new ObjectMapper();
    private RestTemplate client = new RestTemplate();
    private int majorVersion = 0;
    private int minorVersion = 0;
    private List<String> fields = new ArrayList<>();
    private static final String url = "https://graph.facebook.com/v{version}/me?access_token={accessToken}&fields={fields}";

    private FacebookAccessTokenApi() {}

    static class Builder {
        private FacebookAccessTokenApi target = new FacebookAccessTokenApi();

        public Builder setMajorVersion(int majorVersion) {
            target.majorVersion = majorVersion;
            return this;
        }

        public Builder setMinorVersion(int minorVersion) {
            target.minorVersion = minorVersion;
            return this;
        }

        public Builder setFields(List<String> fields) {
            target.fields = fields;
            return this;
        }

        public FacebookAccessTokenApi build() {
            if (target.majorVersion == 0 || target.minorVersion == 0 || target.fields.isEmpty()) {
                return new FacebookAccessTokenApi() {
                    @Override
                    public FacebookApiResponse verify(String accessToken) {
                        throw new RuntimeException();
                    }
                };
            }
            return target;
        }
    }

    public FacebookApiResponse verify(String accessToken) {
        Map<String,String> args = new HashMap<>();
        args.put("version", majorVersion + "." + minorVersion);
        args.put("accessToken", accessToken);
        args.put("fields", String.join(",",fields));
        try {
            String json = client.getForObject(url, String.class, args);
            FacebookApiResponse result = new FacebookApiResponse(mapper.readValue(json,
                    new TypeReference<Map<String, String>>(){}));
            for (String field : fields) {
                if (!result.hasValue(field)) {
                    throw new RuntimeException();
                }
            }
            return result;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
