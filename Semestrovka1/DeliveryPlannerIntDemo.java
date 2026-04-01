package Semestrovka;

import java.util.ArrayList;
import java.util.List;

public class DeliveryPlannerIntDemo {

    public static void main(String[] args) {
        // Задача: в пункте доставки выбираем следующий заказ по минимальному ETA (в минутах).
        // Чем меньше ETA, тем раньше заказ должен уйти в работу.
        FibonacciHeap heap = new FibonacciHeap();

        // 1) Добавление заказов (ETA в минутах)
        int[] etas = {35, 12, 28, 7, 19, 42};
        for (int eta : etas) {
            heap.add(eta);
        }
        System.out.println("После add, размер: " + heap.size());

        // 2) Поиск
        System.out.println("Есть заказ с ETA=28? " + heap.search(28));
        System.out.println("Есть заказ с ETA=15? " + heap.search(15));

        // 3) Удаление (например, заказ отменили)
        System.out.println("Удалили заказ ETA=19: " + heap.delete(19));
        System.out.println("Удалили заказ ETA=99: " + heap.delete(99));

        // Решение задачи: получаем порядок обработки доставок (от меньшего ETA к большему)
        List<Integer> processingOrder = new ArrayList<>();
        while (!heap.isEmpty()) {
            processingOrder.add(heap.extractMin());
        }


        System.out.println("Порядок обработки доставок (ETA): " + processingOrder);
    }
}

