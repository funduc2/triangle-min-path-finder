package finder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class TriangleMinPath {

    /**
     * Контейнер результата: сумма + путь (значения)
     * @param minimumSum Минимальная сумма пути
     * @param pathValues Значения на оптимальном пути сверху вниз
     */
    record SumAndPath(int minimumSum, List<Integer> pathValues) {}

    public static void main(String[] args) {
        var triangle = readTriangle();
        SumAndPath sumAndPath = findMinimumPathSum(triangle);
        printResult(sumAndPath);
    }

    /**
     * Считывание треугольника
     */
    private static List<List<Integer>> readTriangle() {
        try {
            Scanner sc = new Scanner(System.in);
            System.out.print("Введите треугольник (например [[2],[3,4],[6,5,7],[4,1,8,3]]): ");
            // Считываем строку с консоли
            String line = sc.nextLine();

            if (line == null) {
                throw new IllegalArgumentException("Пустой ввод.");
            }
            line = line.trim();
            if (!line.startsWith("[[") || !line.endsWith("]]")) {
                throw new IllegalArgumentException("Ожидался формат вида [[2],[3,4],...].");
            }

            // Убираем внешние двойные скобки
            String inner = line.substring(2, line.length() - 2).trim();
            if (inner.isEmpty()) {
                throw new IllegalArgumentException("Треугольник пуст.");
            }

            // Делим на строки по паттерну "],["
            String[] rowTokens = inner.split("\\],\\[");

            // Проверка: минимум две строки
            if (rowTokens.length < 2) {
                throw new IllegalArgumentException("Треугольник должен содержать как минимум две строки.");
            }

            List<List<Integer>> triangle = new ArrayList<>();
            for (String row : rowTokens) {
                // Разбиваем строку "2,3,4" по запятым
                String[] nums = row.split(",");
                List<Integer> rowList = new ArrayList<>();
                for (String num : nums) {
                    rowList.add(Integer.parseInt(num));
                }
                triangle.add(rowList);
            }
            return triangle;
        } catch (IllegalArgumentException e) {
            System.out.println("Некорректный формат данных, попробуйте ещё раз. " + e.getMessage());
            return readTriangle();
        } catch (Exception e) {
            System.out.println("Некорректный формат данных, попробуйте ещё раз");
            return readTriangle();
        }
    }

    /**
     * Вывод результата
     */
    private static void printResult(SumAndPath result) {
        System.out.println("Результат");
        System.out.print("Минимальный путь: ");
        var pathValues = result.pathValues;
        for (int i = 0; i < pathValues.size(); i++) {
            // Печатаем очередное значение из пути
            System.out.print(pathValues.get(i));
            // Если это не последний элемент, печатаем разделяющую стрелку "→"
            if (i < pathValues.size() - 1) System.out.print(" \u2192 ");
        }
        // Переход на другую строку и печать результата
        System.out.println();
        System.out.println("Результат: " + result.minimumSum);
    }

    /**
     * Поиск суммы и пути
     */
    public static SumAndPath findMinimumPathSum(List<List<Integer>> triangle) {
        // Если треугольник пустой, возвращаем нули
        if (triangle == null || triangle.isEmpty()) {
            return new SumAndPath(0, Collections.emptyList());
        }

        // Определение высоты треугольника
        int height = triangle.size();
        // Создание таблицы минимальных сумм от вершины до основания
        int[][] minSumToBottom = new int[height][height];
        // Создание таблицы пути по «стрелочкам»: true — идти вправо (col+1), false — вниз (col)
        boolean[][] nextStepIsRight = new boolean[height][height];

        // Заполняем основание: нижнюю строку таблицы минимальных сумм
        for (int col = 0; col < triangle.get(height - 1).size(); col++) {
            // Двигаемся по основанию треугольника
            int cellValue = triangle.get(height - 1).get(col);
            // И записываем соответствующие числа в таблицу минимальных сумм
            minSumToBottom[height - 1][col] = cellValue;
        }

        // Идем снизу (от основания) вверх и считаем минимальные суммы
        for (int row = height - 2; row >= 0; row--) {
            // Определяем с какой строкой треугольника работаем далее
            List<Integer> currentRow = triangle.get(row);
            // Проходим по всем колонкам этой строки
            for (int col = 0; col < currentRow.size(); col++) {
                // Сумма прямо вниз (row+1, col).
                int sumViaDirect = minSumToBottom[row + 1][col];
                // Сумма вниз со сдвигом в право (row+1, col+1)
                int sumViaRight = minSumToBottom[row + 1][col + 1];
                // Текущее значение клетки
                int here = currentRow.get(col);

                // Если правый путь меньше, выбираем его
                if (sumViaRight < sumViaDirect) {
                    // Сохраняем минимальную сумму
                    minSumToBottom[row][col] = here + sumViaRight;
                    // Сохраняем направление: шаг вправо
                    nextStepIsRight[row][col] = true;
                } else {
                    // Иначе идем вниз
                    minSumToBottom[row][col] = here + sumViaDirect;
                    // Сохраняем направление: шаг вниз
                    nextStepIsRight[row][col] = false;
                }
            }
        }

        // Восстанавливаем путь по «стрелочкам»: создаем список значений на пути
        List<Integer> path = new ArrayList<>(height);
        // Начинаем в вершине: (0,0)
        int col = 0;
        // Идем по всем строкам, кроме последней. На каждой делаем шаг к выбранному значению
        for (int row = 0; row < height - 1; row++) {
            // Добавляем значение текущей клетки в путь
            path.add(triangle.get(row).get(col));
            // Если указатель вправо, сдвигаем колонку
            if (nextStepIsRight[row][col]) {
                col = col + 1;
            }
            // Иначе остаемся в той же колонке (идем просто вниз)
        }
        // Добавляем последний элемент (на основании)
        path.add(triangle.get(height - 1).get(col));

        // Возвращаем минимальную сумму пути и сам путь
        return new SumAndPath(minSumToBottom[0][0], path);
    }
}
