package com.stempo.repository;

import com.stempo.entity.RecordEntity;
import java.util.List;

public interface RecordCustomRepository {

    List<RecordEntity> findRecordsByDeviceTags(List<String> deviceTags);
}
