package com.example.umc9th.domain.store.controller;


import com.example.umc9th.domain.store.dto.StoreResponseDto;
import com.example.umc9th.domain.store.enums.District;
import com.example.umc9th.domain.store.repository.StoreQueryDsl;
import com.example.umc9th.domain.store.service.StoreServiece;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stores")
public class StoreController {

    private final StoreServiece storeService;
    private final StoreQueryDsl storeQueryDsl;


    // 지역 필터 + 이름 검색
    @GetMapping("/search/district")
    public ResponseEntity<List<StoreResponseDto>> searchStoresWithDistrict(
            @RequestParam(required = false) District district
    ) {
        List<StoreResponseDto> results = storeService.searchStoresWithDistrict(district);
        return ResponseEntity.ok(results);
    }

    //가게 이름으로 찾기
    @GetMapping("/search/name")
    public ResponseEntity<List<StoreResponseDto>> searchStoresWithName(
            @RequestParam(required = false) String keyword
    ) {
        List<StoreResponseDto> result = storeService.searchStoresByKeyword(keyword);
        return ResponseEntity.ok(result);
    }

    //모든 가게를 커서 기반 페이징 및 정렬 순서를 한글 > 영대문자 > 영소문자 > 특수문자 순서로 정렬(각 단어는 사전순으로 한다)
    @GetMapping("/search/all_store")
    public ResponseEntity<Map<String, Object>> getAllStores(
            @RequestParam(required = false) Long cursorId // 👉 페이지 대신 커서
    ) {
        List<StoreResponseDto> stores = storeService.getAllStores(cursorId);

        // 다음 커서 = 이번 페이지의 마지막 storeId
        Long nextCursor = stores.isEmpty() ? null :
                stores.get(stores.size() - 1).getStoreId();

        Map<String, Object> result = new HashMap<>();
        result.put("prevCursor", cursorId);
        result.put("nextCursor", nextCursor);
        result.put("stores", stores);

        return ResponseEntity.ok(result);
    }


}