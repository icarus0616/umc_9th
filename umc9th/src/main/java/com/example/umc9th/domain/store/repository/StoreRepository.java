package com.example.umc9th.domain.store.repository;

import com.example.umc9th.domain.store.entity.Store;
import com.example.umc9th.domain.store.enums.District;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoreRepository extends JpaRepository<Store, Long> {

    // 지역 필터링
    List<Store> findByRegion_District(District district);

}