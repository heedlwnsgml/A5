package com.a5.a5.ai.repository;

import com.a5.a5.ai.entity.CachedPlace;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CachedPlaceRepository extends JpaRepository<CachedPlace, Long> {

    // 장소 이름으로 캐시 데이터 조회
    Optional<CachedPlace> findByPlaceName(String placeName);
}
