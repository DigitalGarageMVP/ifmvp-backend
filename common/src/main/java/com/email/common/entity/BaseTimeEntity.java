package com.email.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
* 엔티티 공통 시간 정보 클래스입니다.
* 모든 엔티티에서 상속받아 사용합니다.
*/
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseTimeEntity {
   
   @CreatedDate
   @Column(name = "created_at", updatable = false)
   private LocalDateTime createdAt;
   
   @LastModifiedDate
   @Column(name = "updated_at")
   private LocalDateTime updatedAt;
   
   @CreatedBy
   @Column(name = "created_by", updatable = false)
   private String createdBy;
   
   @LastModifiedBy
   @Column(name = "updated_by")
   private String updatedBy;
}
