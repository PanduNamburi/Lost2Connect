package com.lost2found.dto;

import com.lost2found.entity.ItemCategory;
import com.lost2found.entity.ItemStatus;

/**
 * Filter DTO for querying and searching Found Item listings.
 */
public class FoundItemSearchFilter {

    private String keyword;
    private ItemCategory category;
    private String city;
    private ItemStatus status;

    public FoundItemSearchFilter() {
    }

    public FoundItemSearchFilter(String keyword, ItemCategory category, String city, ItemStatus status) {
        this.keyword = keyword;
        this.category = category;
        this.city = city;
        this.status = status;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public ItemCategory getCategory() {
        return category;
    }

    public void setCategory(ItemCategory category) {
        this.category = category;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public ItemStatus getStatus() {
        return status;
    }

    public void setStatus(ItemStatus status) {
        this.status = status;
    }
}
