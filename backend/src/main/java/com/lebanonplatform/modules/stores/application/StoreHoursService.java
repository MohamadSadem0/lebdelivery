package com.lebanonplatform.modules.stores.application;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lebanonplatform.modules.stores.domain.Store;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class StoreHoursService {

    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {
    };
    private static final ZoneId DEFAULT_ZONE = ZoneId.of("Asia/Beirut");

    private final ObjectMapper objectMapper;

    public StoreHoursService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public boolean isOpenNow(Store store) {
        return isOpenAt(store, Instant.now());
    }

    public boolean isOpenAt(Store store, Instant instant) {
        Map<String, Object> openingHours = readOpeningHours(store);
        if (openingHours.isEmpty()) {
            return true;
        }

        ZoneId zoneId = zoneId(openingHours);
        ZonedDateTime localDateTime = instant.atZone(zoneId);
        Object dayValue = openingHours.get(dayKey(localDateTime.getDayOfWeek()));
        if (dayValue == null) {
            dayValue = openingHours.get(localDateTime.getDayOfWeek().name());
        }
        if (dayValue == null) {
            return false;
        }
        return isOpenForDayValue(dayValue, localDateTime.toLocalTime());
    }

    private boolean isOpenForDayValue(Object dayValue, LocalTime time) {
        if (dayValue instanceof Boolean booleanValue) {
            return booleanValue;
        }
        if (dayValue instanceof Map<?, ?> mapValue) {
            return isOpenForWindow(mapValue, time);
        }
        if (dayValue instanceof List<?> listValue) {
            if (listValue.isEmpty()) {
                return false;
            }
            return listValue.stream().anyMatch(window -> window instanceof Map<?, ?> mapValue && isOpenForWindow(mapValue, time));
        }
        return false;
    }

    private boolean isOpenForWindow(Map<?, ?> window, LocalTime time) {
        Object closed = window.get("closed");
        if (closed instanceof Boolean closedBoolean && closedBoolean) {
            return false;
        }
        Object openValue = window.get("open");
        Object closeValue = window.get("close");
        if (!(openValue instanceof String openText) || !(closeValue instanceof String closeText)) {
            return false;
        }

        LocalTime open = LocalTime.parse(openText);
        LocalTime close = LocalTime.parse(closeText);
        if (open.equals(close)) {
            return true;
        }
        if (close.isAfter(open)) {
            return !time.isBefore(open) && time.isBefore(close);
        }
        return !time.isBefore(open) || time.isBefore(close);
    }

    private Map<String, Object> readOpeningHours(Store store) {
        String json = store.getOpeningHoursJson();
        if (json == null || json.isBlank()) {
            return Map.of();
        }
        try {
            return objectMapper.readValue(json, MAP_TYPE);
        } catch (Exception exception) {
            return Map.of();
        }
    }

    private ZoneId zoneId(Map<String, Object> openingHours) {
        Object timezone = openingHours.get("timezone");
        if (timezone instanceof String timezoneText && !timezoneText.isBlank()) {
            try {
                return ZoneId.of(timezoneText);
            } catch (Exception ignored) {
                return DEFAULT_ZONE;
            }
        }
        return DEFAULT_ZONE;
    }

    private String dayKey(DayOfWeek dayOfWeek) {
        return dayOfWeek.name().toLowerCase(Locale.ROOT);
    }
}
