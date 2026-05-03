package com.lebanonplatform.common.security.jwt;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lebanonplatform.modules.roles.domain.PlatformRole;
import com.lebanonplatform.modules.users.domain.User;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private static final Base64.Encoder BASE64_URL_ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder BASE64_URL_DECODER = Base64.getUrlDecoder();
    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {
    };

    private final ObjectMapper objectMapper;
    private final String accessSecret;
    private final long accessExpirationMinutes;

    public JwtService(
            ObjectMapper objectMapper,
            @Value("${app.jwt.access-secret}") String accessSecret,
            @Value("${app.jwt.access-expiration-minutes}") long accessExpirationMinutes
    ) {
        this.objectMapper = objectMapper;
        this.accessSecret = accessSecret;
        this.accessExpirationMinutes = accessExpirationMinutes;
    }

    public String createAccessToken(User user, List<PlatformRole> roles) {
        Instant now = Instant.now();
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", user.getId().toString());
        claims.put("phone", user.getPhoneNumber());
        claims.put("roles", roles.stream().map(Enum::name).toList());
        claims.put("iat", now.getEpochSecond());
        claims.put("exp", now.plusSeconds(accessExpirationMinutes * 60).getEpochSecond());

        return sign(claims);
    }

    public UUID extractUserId(String token) {
        Object subject = parseClaims(token).get("sub");
        if (subject == null) {
            throw new IllegalArgumentException("Token subject is missing.");
        }
        return UUID.fromString(subject.toString());
    }

    public boolean isValid(String token) {
        try {
            Map<String, Object> claims = parseClaims(token);
            Object expiration = claims.get("exp");
            return expiration instanceof Number number && Instant.now().getEpochSecond() < number.longValue();
        } catch (RuntimeException exception) {
            return false;
        }
    }

    private String sign(Map<String, Object> claims) {
        try {
            String headerJson = objectMapper.writeValueAsString(Map.of("alg", "HS256", "typ", "JWT"));
            String claimsJson = objectMapper.writeValueAsString(claims);
            String unsignedToken = encode(headerJson) + "." + encode(claimsJson);
            return unsignedToken + "." + hmac(unsignedToken, accessSecret);
        } catch (Exception exception) {
            throw new IllegalStateException("Could not create access token.", exception);
        }
    }

    private Map<String, Object> parseClaims(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                throw new IllegalArgumentException("Invalid token format.");
            }

            String expectedSignature = hmac(parts[0] + "." + parts[1], accessSecret);
            if (!constantTimeEquals(expectedSignature, parts[2])) {
                throw new IllegalArgumentException("Invalid token signature.");
            }

            String claimsJson = new String(BASE64_URL_DECODER.decode(parts[1]), StandardCharsets.UTF_8);
            return objectMapper.readValue(claimsJson, MAP_TYPE);
        } catch (Exception exception) {
            throw new IllegalArgumentException("Invalid token.", exception);
        }
    }

    private String encode(String value) {
        return BASE64_URL_ENCODER.encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private String hmac(String value, String secret) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        return BASE64_URL_ENCODER.encodeToString(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
    }

    private boolean constantTimeEquals(String left, String right) {
        if (left.length() != right.length()) {
            return false;
        }

        int result = 0;
        for (int i = 0; i < left.length(); i++) {
            result |= left.charAt(i) ^ right.charAt(i);
        }
        return result == 0;
    }
}
