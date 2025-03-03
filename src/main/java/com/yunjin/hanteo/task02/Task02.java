package com.yunjin.hanteo.task02;

import java.util.Arrays;
import java.util.Scanner;

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

        // 입력
        Scanner sc = new Scanner(System.in);
        System.out.println("합계를 입력하세요. : ");
        int sum = Integer.parseInt(sc.nextLine());

        System.out.println("동전들을 입력하세요. : ");
        int[] coins = Arrays.stream(sc.nextLine().split(" "))
                .mapToInt(Integer::parseInt)
                .toArray();

        // 결과 출력
        int result = T.getCoinWays(sum, coins);
        System.out.println("출력 : " + result);

        sc.close();
    }
}
