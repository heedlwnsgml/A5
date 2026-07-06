package com.a5.a5.ai.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class TravelRequestDto {

    // 필수 값 유효성 검사 적용
    @NotBlank(message = "여행지는 필수 입력값입니다.")
    private String destination;

    @Min(value = 1, message = "여행 일수는 최소 1일 이상이어야 합니다.")
    private int durationDays;

    @NotBlank(message = "테마는 필수 입력값입니다.")
    private String theme;

    @Min(value = 1, message = "동행 인원은 최소 1명 이상이어야 합니다.")
    private int companions;

    @NotBlank(message = "시작일은 필수 입력값입니다.")
    private String startDate;

    @NotBlank(message = "종료일은 필수 입력값입니다.")
    private String endDate;

    @NotEmpty(message = "최소 하나 이상의 도시를 선택해야 합니다.")
    private List<String> cities;

    @NotEmpty(message = "최소 하나 이상의 상세 테마를 선택해야 합니다.")
    private List<String> themes;

    @NotBlank(message = "이동 수단을 선택해주세요.")
    private String transportation;

    // 간결한 주석: 선택적 필드
    private List<String> fixedSchedules;

    @NotBlank(message = "언어 설정이 누락되었습니다.")
    private String language;
}