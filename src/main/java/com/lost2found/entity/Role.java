package com.lost2found.entity;

import com.lost2found.common.entity.BaseEntity;

import java.time.LocalDateTime;

/**
 * Role Entity POJO for Firestore database.
 */
public class Role extends BaseEntity {

    private RoleName name;

    public Role() {
        super();
    }

    public Role(RoleName name) {
        super();
        this.name = name;
    }

    public Role(String id, RoleName name, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(id, createdAt, updatedAt);
        this.name = name;
    }

    public RoleName getName() {
        return name;
    }

    public void setName(RoleName name) {
        this.name = name;
    }
}
