package com.example.umc9th.domain.store.service;

import com.example.umc9th.domain.store.dto.StoreResponseDto;
import com.example.umc9th.domain.store.entity.QStore;
import com.example.umc9th.domain.store.entity.Store;
import com.example.umc9th.domain.store.enums.District;
import com.example.umc9th.domain.store.repository.StoreQueryDsl;
import com.example.umc9th.domain.store.repository.StoreRepository;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class StoreServiece {
    private final StoreRepository storeRepository;
    private final StoreQueryDsl storeQueryDsl;

    //해당 지역의 이름으로 가게 정보 받아오는 로직, QueryDsl이 아니랑 기본 JPA를 사용하여 구현함
    public List<StoreResponseDto> searchStoresWithDistrict(District district) {
        List<Store> stores;

        if (district != null) {
            stores = storeRepository.findByRegion_District(district);
        } else {
            stores = storeRepository.findAll();
        }

        return stores.stream()
                .map(StoreResponseDto::from)
                .toList();
    }

    //키워드를 받아 buildSearchPredicate로 조건문을 작성한뒤 조건에 맞는 가게 정보를 받아오는 로직
    public List<StoreResponseDto> searchStoresByKeyword(String keyword) {
        Predicate predicate = buildSearchPredicate(keyword);
        return storeQueryDsl.searchStore(predicate).stream()
                .map(StoreResponseDto::from)
                .toList();
    }
    //키워드로 가게 이름 검색시 공백 문자가 있다면 구분자로 설정해 각각 단어에 맞는 모든 가게 정보를 받아오는 로직
    private Predicate buildSearchPredicate(String keyword) {
        QStore store = QStore.store;
        BooleanExpression condition = store.isNotNull(); // 기본 조건

        if (keyword.trim().isEmpty()) {
            return condition;
        }

        // 🔹 공백 포함 검색어: 각 단어 포함된 가게의 "합집합" (OR)
        if (keyword.contains(" ")) {
            String[] words = keyword.trim().split("\\s+");
            BooleanExpression nameCondition = store.storeName.containsIgnoreCase(words[0]);
            for (int i = 1; i < words.length; i++) {
                nameCondition = nameCondition.or(store.storeName.containsIgnoreCase(words[i]));
            }
            condition = condition.and(nameCondition);
        }
        // 🔹 공백 없는 검색어: 전체 키워드 포함된 가게만 (AND)
        else {
            condition = condition.and(store.storeName.containsIgnoreCase(keyword));
        }

        return condition;
    }



    //모든 가게를 정렬 우선순위: 가나다 → 영어 대문자 → 영어 소문자 → 특수문자 순서로 이름이 동일한 경우: 최신순으로 정렬하는 로직
    public List<StoreResponseDto> getAllStores(Long cursorId) {
        List<Store> stores = storeQueryDsl.getAllStores(cursorId);

        return stores.stream()
                .map(StoreResponseDto::from)
                .toList();
    }





}
