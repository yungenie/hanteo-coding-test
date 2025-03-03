package com.yunjin.hanteo.task01.service;

import java.util.List;

public interface CategoryService {
    void addCategory(List<Long> parentIds, Long categoryId, String categoryName);
    String findByCategoryId(Long categoryId);
    String findByCategoryName(String categoryName);
    Long getNextCategoryId();
}