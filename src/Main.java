/*
-- ПРИНЦИП РАБОТЫ --
Таблица реализованна на массиве, в котором находится голова
связного списка, а его элементы - это объекты класса Bucket, который содержит ключ и значение
и указатель на следующий элемент.
Индексы вычисляются с помощью метода деления
Коллизии разрешаются методом цепочек.
Рехеширование происходит при коэффициенти заполнения выше 0.75

 -- ДОКАЗАТЕЛЬСТВО КОРРЕКТНОСТИ --
Метод get находит индекс элемент по ключу с помощью хеширования, а дальше идет по сязному списку,
пока не найдет нужный ключ и выводит значение, если не находит, то выводит None

Метод delete находит индекс элемент по ключу с помощью хеширования, а дальше идет по сязному списку,
пока не найдет нужный ключ и удаляет его из связного списка, если не находит, то выводит None

Метод put находит индекс элемент по ключу с помощью хеширования, если по этому индексу лежит null
то добавляет новую голову списка, если же там есть список, то он идет по нему проверяя наличие ключа,
если он есть та значение заменяется, если нет, то добавляется новый узел

-- ВРЕМЕННАЯ СЛОЖНОСТЬ --
Операции put, get, delete в среднем имеют сложность O(1)
Общая сложность будет O(n), если свести коллизии к O(1)
где n - количество операций
Так же операция рехеширования занимает O(n), но она используется редко.
n - колво элементов в Map

-- ПРОСТРАНСТВЕННАЯ СЛОЖНОСТЬ --
Пространственная сложность зависит от величинамы массива и составляет O(k)
k - размер Map
Пространственная сложность всех переменных и массива для рехеширования O(1)


Id попытки 89964722
 */



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.OptionalInt;

public class Main {
    public static void main(String[] args) throws IOException {
        BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
        int n = Integer.parseInt(bf.readLine());
        StringBuilder sb = new StringBuilder();
        MyHashMap<Integer, Integer> hashMap = new MyHashMap<>(17);
        for (int i = 0; i < n; i++) {
            String str = bf.readLine();
            if (str.contains("get")) {
                OptionalInt ans = hashMap.get(Integer.parseInt(str.split(" ")[1]));
                if (ans.isEmpty()) {
                    sb.append("None \n");
                } else {
                    sb.append(ans.getAsInt() + "\n");
                }
            }
            if (str.contains("delete")) {
                OptionalInt ans = hashMap.delete(Integer.parseInt(str.split(" ")[1]));
                if (ans.isEmpty()) {
                    sb.append("None \n");
                } else {
                    sb.append(ans.getAsInt() + "\n");
                }
            }
            if (str.contains("put")) {
                hashMap.put(Integer.parseInt(str.split(" ")[1]), Integer.parseInt(str.split(" ")[2]));
            }
        }
        System.out.println(sb);

    }


    static class MyHashMap<K, V> {
        Bucket<K, V>[] map;
        int size;
        int[] sizes = new int[]{17, 31, 67, 127, 257, 509, 1021, 2053, 4099, 8191,
                16381, 32749, 65521, 131071, 262139, 524287, 1048573, 2097143};
        int realLen = 0;

        public MyHashMap(int capacity) {
            this.map = new Bucket[capacity];
            size = 0;
        }

        public void put(K key, V value) {
            if ((float) size / map.length > 0.75) {
                realLen++;
                rehash(sizes[realLen]);
            }
            int index = (int) key % sizes[realLen];
            if (index < 0) {
                index += sizes[realLen];
            }
            Bucket<K, V> newBucket = new Bucket<>(key, value);

            if (map[index] == null) {
                map[index] = newBucket;
            } else {
                Bucket<K, V> current = map[index];

                while (current.next != null) {
                    if (current.key.equals(key)) {
                        current.value = value;
                    }
                    current = current.next;
                }
                if (current.key.equals(key)) {
                    current.value = value;
                } else {
                    current.next = newBucket;
                }
            }
            size++;
        }

        public OptionalInt get(K key) {
            int index = (int) key % sizes[realLen];
            if (index < 0) {
                index += sizes[realLen];
            }
            Bucket<K, V> current = map[index];
            while (current != null) {
                if (current.key.equals(key)) {
                    return OptionalInt.of((int) current.value);
                }
                current = current.next;
            }
            return OptionalInt.empty();

        }

        public OptionalInt delete(K key) {
            int index = (int) key % sizes[realLen];
            if (index < 0) {
                index += sizes[realLen];
            }
            Bucket<K, V> current = map[index];
            Bucket<K, V> prev = null;

            while (current != null) {
                if (current.key.equals(key)) {
                    if (prev == null) {
                        map[index] = current.next;
                    } else {
                        prev.next = current.next;
                    }
                    size--;
                    return OptionalInt.of((int) current.value);
                }
                prev = current;
                current = current.next;
            }
            return OptionalInt.empty();
        }


        public void rehash(int newCapacity) {
            Bucket<K, V>[] oldMap = map;
            map = new Bucket[newCapacity];
            size = 0;
            for (Bucket<K, V> entry : oldMap) {
                while (entry != null) {
                    put(entry.key, entry.value);
                    entry = entry.next;
                }
            }
        }

        static class Bucket<K, V> {
            K key;
            V value;
            Bucket<K, V> next;

            Bucket(K key, V value) {
                this.key = key;
                this.value = value;
                this.next = null;
            }
        }
    }
}


