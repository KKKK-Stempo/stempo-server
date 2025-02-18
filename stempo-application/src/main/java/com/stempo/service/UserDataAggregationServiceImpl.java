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
        // deviceTags에 해당하는 보행 훈련 기록과 과제 조회
        List<DecryptedRecord> records = recordService.getByDeviceTags(deviceTags);
        List<DecryptedHomework> homeworks = homeworkService.getByDeviceTags(deviceTags);

        // 보행 훈련 기록을 deviceTag별로 그룹화
        Map<String, List<DecryptedRecord>> recordsByDevice = records.stream()
            .filter(decryptedRecord -> decryptedRecord.getDeviceTag() != null)
            .collect(Collectors.groupingBy(DecryptedRecord::getDeviceTag));

        // 과제를 deviceTag별로 그룹화
        Map<String, List<DecryptedHomework>> homeworkByDevice = homeworks.stream()
            .filter(decryptedHomework -> decryptedHomework.getDeviceTag() != null)
            .collect(Collectors.groupingBy(DecryptedHomework::getDeviceTag));

        // 조회된 두 데이터셋의 deviceTag를 모두 합쳐 중복 제거
        Set<String> deviceTagSet = new HashSet<>();
        deviceTagSet.addAll(recordsByDevice.keySet());
        deviceTagSet.addAll(homeworkByDevice.keySet());

        // 각 deviceTag별로 DTO 빌드
        return deviceTagSet.stream()
            .map(deviceTag -> {
                List<RecordDataResponseDto> recordDatas = recordsByDevice.getOrDefault(deviceTag,
                        Collections.emptyList())
                    .stream()
                    .map(RecordDataResponseDto::from)
                    .sorted(Comparator.comparing(RecordDataResponseDto::getCreatedAt))
                    .toList();

                List<HomeworkDataResponseDto> homeworkDatas = homeworkByDevice.getOrDefault(deviceTag,
                        Collections.emptyList())
                    .stream()
                    .map(HomeworkDataResponseDto::from)
                    .sorted(Comparator.comparing(HomeworkDataResponseDto::getCreatedAt))
                    .toList();

                return UserDataResponseDto.of(deviceTag, recordDatas, homeworkDatas);
            })
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PersonalTrainingSettingsResponseDto> getPersonalTrainingSettings(List<String> deviceTags) {
        // deviceTags에 해당하는 보행 훈련 기록과 과제 조회
        List<DecryptedRecord> allRecords = recordService.getByDeviceTags(deviceTags);
        List<DecryptedHomework> allHomeworks = homeworkService.getByDeviceTags(deviceTags);

        // 보행 훈련 기록을 deviceTag별로 그룹화
        Map<String, List<DecryptedRecord>> recordsByDevice = allRecords.stream()
            .filter(r -> r.getDeviceTag() != null)
            .collect(Collectors.groupingBy(DecryptedRecord::getDeviceTag));

        // 과제를 deviceTag별로 그룹화
        Map<String, List<DecryptedHomework>> homeworksByDevice = allHomeworks.stream()
            .filter(h -> h.getDeviceTag() != null)
            .collect(Collectors.groupingBy(DecryptedHomework::getDeviceTag));

        // 조회된 두 데이터셋의 deviceTag를 모두 합쳐 중복 제거
        Set<String> deviceTagKeys = new HashSet<>();
        deviceTagKeys.addAll(recordsByDevice.keySet());
        deviceTagKeys.addAll(homeworksByDevice.keySet());

        // 각 deviceTag별로 DTO 빌드
        return deviceTagKeys.stream()
            .map(deviceTag -> {
                List<RecordDataResponseDto> sortedRecordDtos = recordsByDevice.getOrDefault(deviceTag,
                        Collections.emptyList())
                    .stream()
                    .map(RecordDataResponseDto::from)
                    .sorted(Comparator.comparing(RecordDataResponseDto::getCreatedAt))
                    .toList();

                List<HomeworkDataResponseDto> sortedHomeworkDtos = homeworksByDevice.getOrDefault(deviceTag,
                        Collections.emptyList())
                    .stream()
                    .map(HomeworkDataResponseDto::from)
                    .sorted(Comparator.comparing(HomeworkDataResponseDto::getCreatedAt))
                    .toList();

                RecordDataResponseDto initialRecord = sortedRecordDtos.isEmpty() ? null : sortedRecordDtos.getFirst();

                return PersonalTrainingSettingsResponseDto.of(deviceTag, initialRecord, sortedHomeworkDtos);
            })
            .toList();
    }
}
