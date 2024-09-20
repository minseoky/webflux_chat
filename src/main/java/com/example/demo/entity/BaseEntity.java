package com.example.demo.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDateTime;

@Getter
@Setter
public abstract class BaseEntity {

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("deleted_at")
    private LocalDateTime deletedAt; // 논삭

    public BaseEntity() {
        this.createdAt = LocalDateTime.now();
        this.deletedAt = null;
    }
}
