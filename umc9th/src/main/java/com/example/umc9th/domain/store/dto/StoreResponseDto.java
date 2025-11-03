package com.example.umc9th.domain.store.dto;

import com.example.umc9th.domain.store.entity.Store;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class StoreResponseDto {

    private Long storeId;
    private String storeName;
    private String storeAddress;
    private Float score;
    private String regionName;
    private String district;

    public static StoreResponseDto from(Store store) {
        return StoreResponseDto.builder()
                .storeId(store.getStoreId())
                .storeName(store.getStoreName())
                .storeAddress(store.getStoreAddress())
                .score(store.getScore())
                .regionName(store.getRegion().getRegionName())
                .district(store.getRegion().getDistrict().getDisplayName())
                .build();
    }
}
