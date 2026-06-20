package com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
@FieldDefaults(level = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {
    @Column(name = "delete_flag", nullable = false, columnDefinition = "BIT DEFAULT 0")
    boolean deleteFlag;

    @CreatedDate
    @Column(updatable = false)
    LocalDateTime createdAt;

    @CreatedBy
    @Column(updatable = false, columnDefinition = "nvarchar(255)")
    String createdBy;

    @LastModifiedDate
    @Column(insertable = false)
    LocalDateTime updatedAt;

    @LastModifiedBy
    @Column(insertable = false, columnDefinition = "nvarchar(255)")
    String updatedBy;
}
