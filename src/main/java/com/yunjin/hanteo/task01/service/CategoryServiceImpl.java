package com.yunjin.hanteo.task01.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yunjin.hanteo.task01.repository.CategoryRepository;
import com.yunjin.hanteo.task01.domain.Category;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final Gson gson;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    @Override
    public void addCategory(List<Long> parentIds, Long categoryId, String categoryName) {
        Category newCategory = new Category(categoryId, categoryName);

        if (parentIds != null) {
            for (Long parentId : parentIds) {
                Category parent = categoryRepository.findParentOrDefault(parentId);
                parent.addChildCategory(newCategory);
                newCategory.getParentCategoryIds().add(parentId);
            }
        }

        categoryRepository.saveCategory(newCategory);
    }

    @Override
    public String findByCategoryId(Long categoryId) {
        Category category = categoryRepository.findByCategoryId(categoryId);
        return gson.toJson(category != null ? category : Map.of("message", "검색 결과 없음"));
    }

    @Override
    public String findByCategoryName(String categoryName) {
        List<Category> categoryList = categoryRepository.findByCategoryName(categoryName);
        return gson.toJson((categoryList != null && !categoryList.isEmpty()) ? categoryList : Collections.emptyList());
    }

    @Override
    public Long getNextCategoryId() {
        return categoryRepository.getNextCategoryId();
    }

}
