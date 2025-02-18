package com.stempo.service;

import com.stempo.dto.DecryptedHomework;
import com.stempo.dto.DecryptedRecord;
import com.stempo.dto.response.HomeworkDataResponseDto;
import com.stempo.dto.response.PersonalTrainingSettingsResponseDto;
import com.stempo.dto.response.RecordDataResponseDto;
import com.stempo.dto.response.UserDataResponseDto;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserDataAggregationServiceImpl implements UserDataAggregationService {

    private final RecordService recordService;
    private final HomeworkService homeworkService;

    @Override
    @Transactional(readOnly = true)
    public List<UserDataResponseDto> getUserData(List<String> deviceTags) {
        List<DecryptedRecord> records = recordService.getByDeviceTags(deviceTags);
        List<DecryptedHomework> homeworks = homeworkService.getByDeviceTags(deviceTags);

        Map<String, List<DecryptedRecord>> recordsByDevice = groupRecordsByDevice(records);
        Map<String, List<DecryptedHomework>> homeworksByDevice = groupHomeworksByDevice(homeworks);
        Set<String> deviceTagSet = mergeDeviceTags(recordsByDevice, homeworksByDevice);

        return deviceTagSet.stream()
            .map(deviceTag -> {
                List<RecordDataResponseDto> recordDatas = sortedRecordData(
                    recordsByDevice.getOrDefault(deviceTag, Collections.emptyList()));
                List<HomeworkDataResponseDto> homeworkDatas = sortedHomeworkData(
                    homeworksByDevice.getOrDefault(deviceTag, Collections.emptyList()));
                return UserDataResponseDto.of(deviceTag, recordDatas, homeworkDatas);
            })
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PersonalTrainingSettingsResponseDto> getPersonalTrainingSettings(List<String> deviceTags) {
        List<DecryptedRecord> allRecords = recordService.getByDeviceTags(deviceTags);
        List<DecryptedHomework> allHomeworks = homeworkService.getByDeviceTags(deviceTags);

        Map<String, List<DecryptedRecord>> recordsByDevice = groupRecordsByDevice(allRecords);
        Map<String, List<DecryptedHomework>> homeworksByDevice = groupHomeworksByDevice(allHomeworks);
        Set<String> deviceTagKeys = mergeDeviceTags(recordsByDevice, homeworksByDevice);

        return deviceTagKeys.stream()
            .map(deviceTag -> {
                List<RecordDataResponseDto> sortedRecordDtos = sortedRecordData(
                    recordsByDevice.getOrDefault(deviceTag, Collections.emptyList()));
                List<HomeworkDataResponseDto> sortedHomeworkDtos = sortedHomeworkData(
                    homeworksByDevice.getOrDefault(deviceTag, Collections.emptyList()));
                // 첫 번째 기록을 초기 분석 지표로 사용 (데이터가 없는 경우 null)
                RecordDataResponseDto initialRecord = sortedRecordDtos.isEmpty() ? null : sortedRecordDtos.getFirst();
                return PersonalTrainingSettingsResponseDto.of(deviceTag, initialRecord, sortedHomeworkDtos);
            })
            .toList();
    }

    // DecryptedRecord를 deviceTag 기준으로 그룹화
    private Map<String, List<DecryptedRecord>> groupRecordsByDevice(List<DecryptedRecord> records) {
        return records.stream()
            .filter(r -> r.getDeviceTag() != null)
            .collect(Collectors.groupingBy(DecryptedRecord::getDeviceTag));
    }

    // DecryptedHomework를 deviceTag 기준으로 그룹화
    private Map<String, List<DecryptedHomework>> groupHomeworksByDevice(List<DecryptedHomework> homeworks) {
        return homeworks.stream()
            .filter(h -> h.getDeviceTag() != null)
            .collect(Collectors.groupingBy(DecryptedHomework::getDeviceTag));
    }

    // 두 그룹에서 deviceTag 키를 모두 합쳐 중복 제거
    private Set<String> mergeDeviceTags(Map<String, List<DecryptedRecord>> recordsByDevice,
        Map<String, List<DecryptedHomework>> homeworksByDevice) {
        Set<String> keys = new HashSet<>();
        keys.addAll(recordsByDevice.keySet());
        keys.addAll(homeworksByDevice.keySet());
        return keys;
    }

    // DecryptedRecord를 RecordDataResponseDto로 변환 후 createdAt 기준 정렬
    private List<RecordDataResponseDto> sortedRecordData(List<DecryptedRecord> records) {
        return records.stream()
            .map(RecordDataResponseDto::from)
            .sorted(Comparator.comparing(RecordDataResponseDto::getCreatedAt))
            .toList();
    }

    // DecryptedHomework를 HomeworkDataResponseDto로 변환 후 createdAt 기준 정렬
    private List<HomeworkDataResponseDto> sortedHomeworkData(List<DecryptedHomework> homeworks) {
        return homeworks.stream()
            .map(HomeworkDataResponseDto::from)
            .sorted(Comparator.comparing(HomeworkDataResponseDto::getCreatedAt))
            .toList();
    }
}

