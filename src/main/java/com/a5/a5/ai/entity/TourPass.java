package com.a5.a5.ai.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "TOUR_PASS")
public class TourPass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pass_id")
    private Long passId;

    @Column(name = "city_name", nullable = false, length = 50)
    private String cityName;

    @Column(name = "pass_name", nullable = false, length = 100)
    private String passName;

    @Column(name = "price", nullable = false)
    private int price;

    @Column(name = "valid_hours", nullable = false)
    private int validHours;

    // 간결한 주석: PASS_COVERAGE 테이블과 1:N 양방향 연관관계 설정
    @OneToMany(mappedBy = "tourPass", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PassCoverage> coverages = new ArrayList<>();

    public TourPass() {}

    // 간결한 주석: Getter 및 Setter
    public Long getPassId() { return passId; }
    public void setPassId(Long passId) { this.passId = passId; }

    public String getCityName() { return cityName; }
    public void setCityName(String cityName) { this.cityName = cityName; }

    public String getPassName() { return passName; }
    public void setPassName(String passName) { this.passName = passName; }

    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }

    public int getValidHours() { return validHours; }
    public void setValidHours(int validHours) { this.validHours = validHours; }

    public List<PassCoverage> getCoverages() { return coverages; }
    public void setCoverages(List<PassCoverage> coverages) { this.coverages = coverages; }
}