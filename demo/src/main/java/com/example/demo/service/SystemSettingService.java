package com.example.demo.service;

import com.example.demo.entity.SystemSetting;
import com.example.demo.mapper.SystemSettingMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

@Service
public class SystemSettingService {

    private static final String RESERVATION_CUTOFF_TIME_KEY = "RESERVATION_CUTOFF_TIME";
    private static final String MIN_RESERVATION_THRESHOLD_KEY = "MIN_RESERVATION_THRESHOLD";
    private static final int DEFAULT_MIN_RESERVATION_THRESHOLD = 6;

    private final SystemSettingMapper systemSettingMapper;

    public SystemSettingService(SystemSettingMapper systemSettingMapper) {
        this.systemSettingMapper = systemSettingMapper;
    }

    public LocalTime getReservationCutoffTime() {
        SystemSetting setting = systemSettingMapper.selectById(RESERVATION_CUTOFF_TIME_KEY);
        if (setting != null && setting.getSettingValue() != null) {
            try {
                return LocalTime.parse(setting.getSettingValue());
            } catch (DateTimeParseException e) {
                // Ignore and use default
            }
        }
        return LocalTime.of(9, 45); // Default to 09:45
    }

    public void updateReservationCutoffTime(String timeStr) {
        try {
            LocalTime.parse(timeStr);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("時刻形式が不正です。HH:mm形式で入力してください。");
        }

        upsertSetting(RESERVATION_CUTOFF_TIME_KEY, timeStr);
    }

    public int getMinReservationThreshold() {
        SystemSetting setting = systemSettingMapper.selectById(MIN_RESERVATION_THRESHOLD_KEY);
        if (setting != null && setting.getSettingValue() != null) {
            try {
                int threshold = Integer.parseInt(setting.getSettingValue());
                if (threshold > 0) {
                    return threshold;
                }
            } catch (NumberFormatException e) {
                // Ignore and use default
            }
        }
        return DEFAULT_MIN_RESERVATION_THRESHOLD;
    }

    public void updateMinReservationThreshold(int value) {
        if (value <= 0) {
            throw new IllegalArgumentException("最低成団人数は1以上に設定してください。");
        }

        upsertSetting(MIN_RESERVATION_THRESHOLD_KEY, String.valueOf(value));
    }

    private void upsertSetting(String key, String value) {
        SystemSetting setting = systemSettingMapper.selectById(key);
        if (setting == null) {
            setting = SystemSetting.builder()
                    .settingKey(key)
                    .settingValue(value)
                    .updatedAt(LocalDateTime.now())
                    .build();
            systemSettingMapper.insert(setting);
        } else {
            setting.setSettingValue(value);
            setting.setUpdatedAt(LocalDateTime.now());
            systemSettingMapper.updateById(setting);
        }
    }
}
