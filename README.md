# 프로젝트 구조
[hanteo](https://github.com/yungenie/hanteo-coding-test/tree/main/src/main/java/com/yunjin/hanteo)  
    [L task01 (게시판 카테고리 검색)](https://github.com/yungenie/hanteo-coding-test/tree/main/src/main/java/com/yunjin/hanteo/task01)  
    [L task02 (동전 조합 경우의 수)](https://github.com/yungenie/hanteo-coding-test/tree/main/src/main/java/com/yunjin/hanteo/task02)  

# 1번 문제
## 구현 언어
- 기능 : Java, Gson
- 테스트 : Spring Boot (junit, assertj)

## 설명

### 정리
1. 트리 구조로 부모-자식 관계 표현
2. 재귀 탐색을 통해 List<Category> 로 하위 모든 노드 접근
3. 이름이 같지만 ID가 다른 카테고리를 구분하기 위해 List<Long> 에 부모 ID 저장
4. 빠른 검색을 위해 HashMap & Set 활용
5. 중복 카테고리(익명게시판 등) 처리를 위해 Set<Category> 사용
6. 멀티스레드 환경 대응을 위해 ConcurrentHashMap 적용

### 트리 구조로 표현되는 카테고리 데이터
```java
0 루트
├── 100 남자
│     ├── 101 엑소
│     │      ├── 1  공지사항
│     │      ├── 2  첸
│     │      ├── 3  백현
│     │      ├── 4  시우민
│     ├── 102 방탄소년단
│            ├── 5  공지사항
│            ├── 6  익명게시판
│            ├── 7  뷔
├── 200 여자
│      ├── 201 블랙핑크
│            ├── 8  공지사항
│            ├── 6  익명게시판 (중복)
│            ├── 9  로제
```
게시판의 여러 형태로 카테고리 구분을 위해, 카테고리 간 부모-자식 관계를 갖는 **Tree 자료구조**로 데이터를 표현할 수 있습니다.

### 카테고리 데이터 구조 정의
```java
public class Category {
    private Long categoryId;
    private String categoryName;
    private List<Long> parentCategoryIds;
    private List<Category> childCategories;
    
    // ... 생략
}
```
각 카테고리는 필수적으로 카테고리 식별자(categoryId), 카테고리 이름(categoryName) 을 가져야 하며, 부모-자식 관계를 표현하기 위한 하위 카테고리(childCategories)와 부모 카테고리(parentCategoryIds)를 포함합니다.

- 설계 고려 사항
    - childCategories : 트리 구조에서 **하위 카테고리를 탐색**할 수 있도록 포함합니다. (재귀적 객체 접근 방식)
    - parentCategoryIds : **익명게시판** 처럼 여러 부모 카테고리를 가질 수 있는 경우를 고려하여 List 자료구조 사용했습니다.

### 빠른 검색을 위한 데이터 저장 구조
```java
public class CategoryRepositoryImpl implements CategoryRepository {
    private final Map<Long, Category> searchByCategoryIdMap;
    private final Map<String, Set<Category>> searchByCategoryNameMap;

    // ... 생략
}
```
카테고리를 빠르게 검색할 수 있도록 2가지 자료구조를 사용합니다.

- 설계 고려 사항
  - 카테고리 식별자(ID) 검색 (searchByCategoryIdMap)
      - 카테고리 식별자는 고유한 값(PK) 이므로 HashMap<Long, Category> 를 사용합니다.
      - categoryId를 키(key)로 사용해 O(1) 시간 복잡도로 빠르게 검색 가능합니다.
  - 카테고리 이름 검색 (searchByCategoryNameMap)
      - **공지사항** 처럼 이름은 같지만 ID가 다른 카테고리가 존재합니다.
      - 따라서 HashMap<String, Set<Category>> 형태로 저장하여 이름을 기준으로 여러 개의 카테고리 검색이 가능합니다.
      - Set<Category> 사용으로 중복 저장된 카테고리 저장을 방지합니다.
  - 동시성 문제 해결 (ConcurrentHashMap)
    - 멀티스레드 환경에서 동시 접근 문제를 방지하기 위해 ConcurrentHashMap 사용하여 안전하게 데이터를 추가할 수 있도록 처리 했습니다.

# 2번 문제
## 설명
### 접근 방식 (DP 풀이)
- 각 동전을 무한히 사용할 수 있으며, 특정 합계를 만드는 모든 방법의 수를 동적 계획법(Dynamic Programming, DP) 을 사용하여 해결할 수 있습니다.
- 모든 조합을 출력할 수 있는 백트래킹(DFS) 방식은 완전 탐색으로 모든 조합을 탐색하므로 불필요한 중복 계산 발생으로 동전의 수(N)이 커질 수록 실행 시간이 증가하여 최악의 경우 O(2^N)의 시간이 걸립니다.
- 이전 경우의 수 값을 저장하고 재사용(Memoization)하는 방식을 사용해서 중복 계산을 방지하여 DFS보다 실행 속도가 빠르며, DP 배열만 사용하면 추가적인 Stack 호출 없이 메모리도 절약 가능하여 효율적인 방식입니다.

### 시간복잡도
- O(N * sum) N은 동전의 개수, sum은 주어진 합산
- 1차원 DP 배열을 사용하는 공간복잡도 O(sum)

### Java 코드 구현
```java
public class Task02 {

    /**
     * 동전으로 특정 합계를 만드는 모든 방법의 수 (동전 중복 사용 가능)
     * @param sum : 정수 합산
     * @param coins : 정수 배열
     * @return 주어진 동전 조합으로 sum을 만드는 모든 방법의 수
     */
    private int getCoinWays(int sum, int[] coins) {

        // dp 정의
        int[] dp = new int[sum + 1]; // 합계 0부터 sum 까지 다양한 동전 조합의 모든 경우의 수
        dp[0] = 1; // 동전 단일로 사용한 경우 1로 초기화

        // dp 점화식
        for (int coin : coins) { // 여러 동전 조합으로 만들 수 있는 합산의 모든 경우의 수
            for (int i = coin; i <= sum; i++) {
                dp[i] += dp[i - coin];
            }
        }

        return dp[sum];
    }

    public static void main(String[] args) {
        Task02 T = new Task02();

        System.out.print("입력 1번 결과 : ");
        System.out.println(T.getCoinWays(4, new int[]{1, 2, 3}));
        System.out.print("입력 2번 결과 : ");
        System.out.println(T.getCoinWays(10, new int[]{2, 5, 3, 6}));
    }
}


```

### 접근 방식 풀이
#### 예제 1번을 통한 설명
- 입력: sum = 4, coins[] = {1,2,3},
- 출력: 4
- 설명: {1, 1, 1, 1}, {1, 1, 2}, {2, 2}, {1, 3}

![풀이1.png](/docs/img/task02_1.png)
![풀이1.png](/docs/img/task02_2.png)

