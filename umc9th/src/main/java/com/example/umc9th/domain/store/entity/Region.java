package com.example.umc9th.domain.store.entity;

import com.example.umc9th.domain.store.enums.District;
import com.example.umc9th.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "region")
@EntityListeners(AuditingEntityListener.class)
public class Region extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "region_id")
    private Long regionId;

    @Column(name = "region_name")
    private String regionName;

    @Column(name = "district")
    @Enumerated(EnumType.STRING)
    private District district;

    @OneToMany(mappedBy = "region")
    private List<Store>storeList=new ArrayList<>();
}
