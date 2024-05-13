package org.example;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Чтение входных данных
        int N = scanner.nextInt(); // Количество дней
        int K = scanner.nextInt(); // Срок хранения рыбы
        int[] prices = new int[N]; // Массив цен на рыбу

        for (int i = 0; i < N; i++) {
            prices[i] = scanner.nextInt();
        }

        // Вызов функции и вывод результата
        int[] result = minCostOfFish(N, K, prices);
        System.out.println(result[0]); // Вывод минимальной суммы
        for (int i = 0; i < N; i++) {
            System.out.print(result[i + 1] + " "); // Вывод количества купленной рыбы на каждый день
        }
    }

    public static int[] minCostOfFish(int N, int K, int[] prices) {
        int minCost = Integer.MAX_VALUE; // Начальное значение минимальной стоимости
        int totalCost = 0; // Общая стоимость рыбы
        int[] fishBought = new int[N + 1]; // Количество купленной рыбы на каждый день

        for (int i = 0; i < N; i++) {
            if (i >= K) {
                // Обновляем минимальную стоимость рыбы в последние K дней
                minCost = getMinPrice(prices, i - K, i);
            }

            // Покупаем столько рыбы, сколько нужно до следующей покупки
            int buyAmount = 1;
            if (i < N - 1) {
                buyAmount = Math.max(1, (minCost - prices[i + 1]) / prices[i + 1] + 1);
            }

            totalCost += prices[i] * buyAmount; // Обновляем общую стоимость рыбы
            fishBought[i + 1] = buyAmount; // Записываем количество купленной рыбы на текущий день
        }

        return new int[]{totalCost, fishBought};
    }

    // Метод для поиска минимальной цены рыбы в заданном диапазоне дней
    public static int getMinPrice(int[] prices, int start, int end) {
        int minPrice = Integer.MAX_VALUE;
        for (int i = start; i < end; i++) {
            minPrice = Math.min(minPrice, prices[i]);
        }
        return minPrice;
    }
}
