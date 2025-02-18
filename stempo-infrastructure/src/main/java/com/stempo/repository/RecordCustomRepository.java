package com.stempo.repository;

import com.stempo.entity.RecordEntity;
import java.time.LocalDate;
import java.util.List;

public interface RecordCustomRepository {

    List<RecordEntity> findRecordsByDeviceTags(List<String> deviceTags);

    List<RecordEntity> findRecordsByDeviceTagsAndDateRange(
        List<String> deviceTags, LocalDate startDate, LocalDate endDate);
}
