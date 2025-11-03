package com.example.umc9th.domain.store.repository;

import com.example.umc9th.domain.store.entity.QStore;
import com.example.umc9th.domain.store.entity.Store;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.umc9th.domain.store.entity.QStore.store;

@Service
@RequiredArgsConstructor
public class StoreQueryDslImpl implements StoreQueryDsl{
    private final EntityManager entityManager;

    @Override
    public List<Store> searchStore(Predicate predicate){

        //JPA 셋팅
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);

        //Q클래스 생성
        QStore Store = store;

        return queryFactory
                .selectFrom(store)
                .where(predicate)
                .fetch();
    }

    //모든 가게를 정렬 우선순위: 가나다 → 영어 대문자 → 영어 소문자 → 특수문자 순서로 이름이 동일한 경우: 최신순으로 정렬하는 로직
    @Override
    public List<Store> getAllStores(Long cursorId) {

        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        QStore store = QStore.store;

        NumberExpression<Integer> caseExpr = Expressions.numberTemplate(Integer.class,
                """
                        CASE 
                            WHEN LEFT({0},1) BETWEEN '가' AND '힣' THEN 1
                            WHEN LEFT({0},1) BETWEEN 'A' AND 'Z' THEN 2
                            WHEN LEFT({0},1) BETWEEN 'a' AND 'z' THEN 3
                            ELSE 4
                        END
                        """,
                store.storeName
        );

        // 커서가 없으면 첫 페이지
        int pageSize = 10;
        BooleanExpression cursorCondition = null;

        if (cursorId != null) {
            // 2️⃣ 커서 기준 엔티티 조회
            Store cursorStore = queryFactory
                    .selectFrom(store)
                    .where(store.storeId.eq(cursorId))
                    .fetchOne();

            if (cursorStore != null) {
                // 3️⃣ 커서의 case 값 미리 계산
                NumberExpression<Integer> cursorCase = Expressions.numberTemplate(Integer.class,
                        """
                                CASE 
                                    WHEN LEFT({0},1) BETWEEN '가' AND '힣' THEN 1
                                    WHEN LEFT({0},1) BETWEEN 'A' AND 'Z' THEN 2
                                    WHEN LEFT({0},1) BETWEEN 'a' AND 'z' THEN 3
                                    ELSE 4
                                END
                                """,
                        cursorStore.getStoreName()
                );

                // 4️⃣ 정렬 기준별 커서 조건
                BooleanExpression higherGroup = caseExpr.gt(cursorCase);
                BooleanExpression higherName = caseExpr.eq(cursorCase)
                        .and(store.storeName.gt(cursorStore.getStoreName()));
                BooleanExpression laterCreated = store.storeName.eq(cursorStore.getStoreName())
                        .and(store.created_at.lt(cursorStore.getCreated_at()));

                // 5️⃣ 조건 합치기
                cursorCondition = higherGroup.or(higherName).or(laterCreated);
            }
        }

        // 6️⃣ 최종 쿼리 실행
        return queryFactory
                .selectFrom(store)
                .where(cursorCondition != null ? cursorCondition : Expressions.TRUE.isTrue())
                .orderBy(
                        caseExpr.asc(),
                        store.storeName.asc(),
                        store.created_at.desc()
                )
                .limit(pageSize)
                .fetch();

    }
}
