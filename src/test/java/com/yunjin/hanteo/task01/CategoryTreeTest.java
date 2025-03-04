package com.yunjin.hanteo.task01;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.yunjin.hanteo.task01.domain.Category;
import com.yunjin.hanteo.task01.repository.CategoryRepositoryImpl;
import com.yunjin.hanteo.task01.service.CategoryService;
import com.yunjin.hanteo.task01.service.CategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.function.Function;

class CategoryTreeTest {
    private CategoryService categoryService;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @BeforeEach
    @DisplayName("초기 데이터 세팅")
    void setUp() {
        categoryService = new CategoryServiceImpl(new CategoryRepositoryImpl());
        categoryService.addCategory(null, 0L, "루트");
        categoryService.addCategory(List.of(0L), 100L, "남자");
        categoryService.addCategory(List.of(100L), 101L, "엑소");
        categoryService.addCategory(List.of(101L), 1L, "공지사항");
        categoryService.addCategory(List.of(101L), 2L, "첸");
        categoryService.addCategory(List.of(101L), 3L, "백현");
        categoryService.addCategory(List.of(101L), 4L, "시우민");
        categoryService.addCategory(List.of(100L), 102L, "방탄소년단");
        categoryService.addCategory(List.of(102L), 5L, "공지사항");
        categoryService.addCategory(List.of(102L), 7L, "뷔");
        categoryService.addCategory(List.of(0L), 200L, "여자");
        categoryService.addCategory(List.of(200L), 201L, "블랙핑크");
        categoryService.addCategory(List.of(201L), 8L, "공지사항");
        categoryService.addCategory(List.of(201L), 9L, "로제");
        categoryService.addCategory(List.of(102L, 201L), 6L, "익명게시판");
    }

    /**
     * 재귀적으로 모든 카테고리의 속성을 수집하는 메서드
     * @param category : 현재 탐색 중인 카테고리
     * @param collector : 수집할 데이터를 저장할 Set (ID 또는 Name)
     * @param mapper : Category 객체에서 원하는 속성을 추출하는 함수 (람다)
     */
    private <T> void collectCategoryAttributes(Category category, Set<T> collector, Function<Category, T> mapper) {
        if (category == null) return;
        for (Category child : category.getChildCategories()) {
            collector.add(mapper.apply(child)); // 지정된 속성 추출 후 추가
            collectCategoryAttributes(child, collector, mapper); // 재귀 탐색
        }
    }

    @Test
    @DisplayName("카테고리 ID - 전체 검색")
    void findAllByCategoryId() {
        String jsonResult = categoryService.findByCategoryId(0L);
        Category category = gson.fromJson(jsonResult, Category.class);

        // 검색 결과 검증
        assertThat(category).isNotNull();
        assertThat(category.getCategoryId()).isEqualTo(0L);
        assertThat(category.getCategoryName()).isEqualTo("루트");

        // 하위 카테고리 포함 검증
        Set<Long> expectedCategoryIds = Set.of(
                100L, 101L, 1L, 2L, 3L, 4L,
                102L, 5L, 7L, 6L,
                200L, 201L, 8L, 9L
        );
        Set<Long> actualCategoryIds = new HashSet<>();
        collectCategoryAttributes(category, actualCategoryIds, Category::getCategoryId);

        assertThat(actualCategoryIds).containsExactlyInAnyOrderElementsOf(expectedCategoryIds);

        System.out.println(jsonResult);
    }

    @Test
    @DisplayName("카테고리 ID - 단일 검색 (엑소)")
    void findOneByCategoryId_Exo() {
        String jsonResult = categoryService.findByCategoryId(101L);
        Category category = gson.fromJson(jsonResult, Category.class);

        // 검색 결과 검증
        assertThat(category).isNotNull();
        assertThat(category.getCategoryId()).isEqualTo(101L);
        assertThat(category.getCategoryName()).isEqualTo("엑소");

        // 하위 카테고리 포함 검증
        Set<Long> expectedCategoryIds = Set.of(1L, 2L, 3L, 4L);
        Set<Long> actualCategoryIds = new HashSet<>();
        collectCategoryAttributes(category, actualCategoryIds, Category::getCategoryId);

        assertThat(actualCategoryIds).containsExactlyInAnyOrderElementsOf(expectedCategoryIds);

        System.out.println(jsonResult);
    }

    @Test
    @DisplayName("카테고리 ID 검색 - 단일 검색 (여자)")
    void findOneByCategoryId_Women() {
        String jsonResult = categoryService.findByCategoryId(200L);
        Category category = gson.fromJson(jsonResult, Category.class);

        // 검색 결과 검증
        assertThat(category).isNotNull();
        assertThat(category.getCategoryId()).isEqualTo(200L);
        assertThat(category.getCategoryName()).isEqualTo("여자");

        // 하위 카테고리 포함 검증
        Set<Long> expectedCategoryIds = Set.of(201L, 8L, 6L, 9L);
        Set<Long> actualCategoryIds = new HashSet<>();
        collectCategoryAttributes(category, actualCategoryIds, Category::getCategoryId);

        assertThat(actualCategoryIds).containsExactlyInAnyOrderElementsOf(expectedCategoryIds);

        System.out.println(jsonResult);
    }

    @Test
    @DisplayName("카테고리 ID 검색 - 단일 검색 (로제)")
    void findOneByCategoryId_Rose() {
        String jsonResult = categoryService.findByCategoryId(9L);
        Category category = gson.fromJson(jsonResult, Category.class);

        // 검색 결과 검증
        assertThat(category).isNotNull();
        assertThat(category.getCategoryId()).isEqualTo(9L);
        assertThat(category.getCategoryName()).isEqualTo("로제");

        System.out.println(jsonResult);
    }

    @Test
    @DisplayName("카테고리 ID 검색 - 단일 검색 (익명게시판)")
    void findOneByCategoryId_AnonymousBoard() {
        String jsonResult = categoryService.findByCategoryId(6L);
        Category category = gson.fromJson(jsonResult, Category.class);

        // 검색 결과 검증
        assertThat(category).isNotNull();
        assertThat(category.getCategoryId()).isEqualTo(6L);
        assertThat(category.getCategoryName()).isEqualTo("익명게시판");

        System.out.println(jsonResult);
    }

    @Test
    @DisplayName("카테고리 ID - 전체 검색 (존재하지 않는 카테고리)")
    void findAllByCategoryId_IsNotFound() {
        String jsonResult = categoryService.findByCategoryId(777777L);
        Type type = new TypeToken<Map<String, String>>() {}.getType();
        Map<String, String> resultMap = gson.fromJson(jsonResult, type);

        // 검색 결과 검증
        assertThat(resultMap).containsEntry("message", "검색 결과 없음");

        System.out.println(jsonResult);
    }

    @Test
    @DisplayName("카테고리명 검색 - 단일 검색 (방탄소년단) ")
    void findAllByCategoryName_Bts() {
        String jsonResult = categoryService.findByCategoryName("방탄소년단");

        Type listType = new TypeToken<List<Category>>() {}.getType();
        List<Category> categories = gson.fromJson(jsonResult, listType);

        // 검색 결과 검증
        assertThat(categories).hasSize(1);
        assertThat(categories).extracting(Category::getCategoryName).containsExactly("방탄소년단");

        // 하위 카테고리 포함 검증
        Set<String> expectedCategoryNames = Set.of("공지사항", "익명게시판", "뷔");
        Set<String> actualCategoryNames = new HashSet<>();

        for (Category category : categories) {
            collectCategoryAttributes(category, actualCategoryNames, Category::getCategoryName);
        }

        assertThat(actualCategoryNames).containsExactlyInAnyOrderElementsOf(expectedCategoryNames);

        System.out.println(jsonResult);
    }

    @Test
    @DisplayName("카테고리명 검색 - 전체 검색 (공지사항) ")
    void findAllByCategoryName_Notice() {
        String jsonResult = categoryService.findByCategoryName("공지사항");
        Type listType = new TypeToken<List<Category>>() {}.getType();
        List<Category> categories = gson.fromJson(jsonResult, listType);

        // 검색 결과 검증
        assertThat(categories).hasSize(3);
        assertThat(categories).allMatch(category -> category.getCategoryName().equals("공지사항"));

        System.out.println(jsonResult);
    }

    @Test
    @DisplayName("카테고리명 검색 - 단일 검색 (익명게시판이 오직 1개로 검색되는지 검증)")
    void findOneByCategoryName_AnonymousIsOnlyOne() {
        String jsonResult = categoryService.findByCategoryName("익명게시판");
        Type listType = new TypeToken<List<Category>>() {}.getType();
        List<Category> categories = gson.fromJson(jsonResult, listType);

        // 검색 결과 검증
        assertThat(categories).hasSize(1);
        assertThat(categories.getFirst().getCategoryName()).isEqualTo("익명게시판");

        System.out.println(jsonResult);
    }

    @Test
    @DisplayName("카테고리명 검색 - 전체 검색 (존재하지 않는 카테고리)")
    void findAllByCategoryName_IsNotFound() {
        String jsonResult = categoryService.findByCategoryName("제니");
        Type listType = new TypeToken<List<Category>>() {}.getType();
        List<Category> categories = gson.fromJson(jsonResult, listType);

        // 검색 결과 검증
        assertThat(categories).hasSize(0);

        System.out.println(jsonResult);
    }

    @Test
    @DisplayName("멀티스레드 환경에서 카테고리 ID 중복 없이 생성되는지 검증")
    void testConcurrentIdGeneration() throws InterruptedException {
        int threadPoolSize = 100;
        int totalTasks = 10000;
        ExecutorService executorService = Executors.newFixedThreadPool(threadPoolSize);
        CountDownLatch latch = new CountDownLatch(threadPoolSize);
        Set<Long> generatedIds = ConcurrentHashMap.newKeySet();

        for (int i = 0; i < threadPoolSize; i++) {
            executorService.execute(() -> {
                try {
                    for (int j = 0; j < totalTasks; j++) {
                        generatedIds.add(categoryService.getNextCategoryId());
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        assertThat(generatedIds.size()).isEqualTo(threadPoolSize * totalTasks);
    }
}
