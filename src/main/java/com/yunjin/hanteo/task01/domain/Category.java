package com.yunjin.hanteo.task01.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Category {
    private Long categoryId;
    private String categoryName;
    private List<Long> parentCategoryIds;
    private List<Category> childCategories;

    public Category(Long categoryId, String categoryName) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.parentCategoryIds = new ArrayList<>(); // NPE 방지
        this.childCategories = new ArrayList<>(); // NPE 방지
    }

    public void addChildCategory(Category childCategory) {
        childCategories.add(childCategory);
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public List<Long> getParentCategoryIds() {
        return parentCategoryIds;
    }

    public List<Category> getChildCategories() {
        return childCategories;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(categoryId, category.categoryId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(categoryId);
    }

    @Override
    public String toString() {
        return "Category{" +
                "categoryId=" + categoryId +
                ", categoryName='" + categoryName + '\'' +
                ", parentCategoryIds=" + parentCategoryIds +
                ", childCategories=" + childCategories +
                '}';
    }
}
