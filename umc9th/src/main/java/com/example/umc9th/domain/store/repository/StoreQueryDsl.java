package com.example.umc9th.domain.store.repository;

import com.example.umc9th.domain.store.entity.Store;
import com.querydsl.core.types.Predicate;

import java.util.List;

public interface StoreQueryDsl {
    List<Store> searchStore(Predicate predicate);
    List<Store> getAllStores(Long cursorId);

}
