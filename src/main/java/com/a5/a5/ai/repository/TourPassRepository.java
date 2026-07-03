package com.a5.a5.ai.repository;

import com.a5.a5.ai.entity.TourPass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TourPassRepository extends JpaRepository<TourPass, Long> {

    // 간결한 주석: 특정 도시의 패스 정보와 노선 적용 범위를 한 번에 조회 (성능 최적화)
    @Query("SELECT DISTINCT t FROM TourPass t LEFT JOIN FETCH t.coverages WHERE t.cityName = :cityName")
    List<TourPass> findByCityNameWithCoverages(@Param("cityName") String cityName);
}