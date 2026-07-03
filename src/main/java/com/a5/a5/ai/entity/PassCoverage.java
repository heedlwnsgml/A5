package com.a5.a5.ai.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "PASS_COVERAGE")
public class PassCoverage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coverage_id")
    private Long coverageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pass_id", nullable = false)
    private TourPass tourPass;

    @Column(name = "operator_name", nullable = false, length = 100)
    private String operatorName;

    public PassCoverage() {}

    // 간결한 주석: Getter 및 Setter
    public Long getCoverageId() { return coverageId; }
    public void setCoverageId(Long coverageId) { this.coverageId = coverageId; }

    public TourPass getTourPass() { return tourPass; }
    public void setTourPass(TourPass tourPass) { this.tourPass = tourPass; }

    public String getOperatorName() { return operatorName; }
    public void setOperatorName(String operatorName) { this.operatorName = operatorName; }
}