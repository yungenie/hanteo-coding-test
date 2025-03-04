package com.yunjin.hanteo.task01.repository;

import com.yunjin.hanteo.task01.domain.Category;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class CategoryRepositoryImpl implements CategoryRepository {
    private final Category root;
    private final Map<Long, Category> searchByCategoryIdMap;
    private final Map<String, Set<Category>> searchByCategoryNameMap;
    private final AtomicLong idGenerator;

    public CategoryRepositoryImpl() {
        this.root = new Category(0L, "루트");
        this.searchByCategoryIdMap = new ConcurrentHashMap<>();
        this.searchByCategoryNameMap = new ConcurrentHashMap<>();
        searchByCategoryIdMap.put(0L, root);
        idGenerator = new AtomicLong(0);
    }

    @Override
    public Category findParentOrDefault(Long parentId) {
        return searchByCategoryIdMap.getOrDefault(parentId, root);
    }

    @Override
    public void saveCategory(Category category) {
        searchByCategoryIdMap.put(category.getCategoryId(), category);
        searchByCategoryNameMap
                .computeIfAbsent(category.getCategoryName(), k -> ConcurrentHashMap.newKeySet())
                .add(category);
    }

    @Override
    public Category findByCategoryId(Long categoryId) {
        return searchByCategoryIdMap.get(categoryId);
    }

    @Override
    public List<Category> findByCategoryName(String categoryName) {
        return new ArrayList<>(searchByCategoryNameMap.getOrDefault(categoryName, new HashSet<>()));
    }

    @Override
    public Long getNextCategoryId() {
        return idGenerator.incrementAndGet(); // 원자적 증가
    }
}
