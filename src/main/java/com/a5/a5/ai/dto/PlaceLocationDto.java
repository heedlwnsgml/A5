package com.a5.a5.ai.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlaceLocationDto {
    private String name;    // 장소명
    private String address; // 주소
    private double lat;     // 위도
    private double lng;     // 경도
}