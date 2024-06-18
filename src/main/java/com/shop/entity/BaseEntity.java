package com.shop.entity;


import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

// BaseEntity 클래스는 엔티티의 생성자와 수정자를 나태내는 정보를 추가로 관리한다.

@EntityListeners(value={AuditingEntityListener.class})

@MappedSuperclass
@Getter
public abstract class BaseEntity extends  BaseTimeEntity{

    @CreatedBy  // 엔티티를 생성할때 사용자 정보를 자동으로 저장
    @Column(updatable = false)  //엔티티가 수정되어도 해당 필드는 변경되지 않는다.
    private String createdBy;

    @LastModifiedDate  //엔티티를 수정한 사용자를 나타내는 정보, 엔티티를 수정할때 사용자 정보를 자동으로 저장
    private String modifiedBy;
}
