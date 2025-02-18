package com.stempo.service;

import com.stempo.dto.response.PersonalTrainingSettingsResponseDto;
import com.stempo.dto.response.UserDataResponseDto;
import java.util.List;

public interface UserDataAggregationService {

    List<UserDataResponseDto> getUserData(List<String> deviceTags);

    List<PersonalTrainingSettingsResponseDto> getPersonalTrainingSettings(List<String> deviceTags);
}
