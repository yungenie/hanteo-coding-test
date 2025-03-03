package com.yunjin.hanteo.task01.repository;

import com.yunjin.hanteo.task01.domain.Category;

import java.util.List;

public interface CategoryRepository {
    Category findParentOrDefault(Long parentId); // 부모 카테고리 조회
    void saveCategory(Category category); // 카테고리 저장
    Category findByCategoryId(Long categoryId);
    List<Category> findByCategoryName(String categoryName);
    Long getNextCategoryId();
}
