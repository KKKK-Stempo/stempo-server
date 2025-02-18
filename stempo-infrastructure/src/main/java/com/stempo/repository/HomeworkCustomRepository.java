package com.stempo.repository;

import com.stempo.entity.HomeworkEntity;
import java.util.List;

public interface HomeworkCustomRepository {

    List<HomeworkEntity> findHomeworkByDeviceTags(List<String> deviceTags);
}
