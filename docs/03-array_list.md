# 3. ArrayList 클래스

## MyArrayList 분류하기

### get
```java
public T get(int index) {
    if (index < 0 || index >= size) {
        throw new IndexOutOfBoundsException();
    }
    return array[index];
}
```
get 메서드 안의 내용은 모두 `상수시간`이다.

### set
```java
public T set(int index, T element) {
    T old = get(index);
    array[index] = element;
    return old;
}
```
set 메서드는 get 메서드를 제외한 모든 곳이 상수시간이다. 
get 메서드도 상수시간이었으므로 set 메서드 또한 `상수시간`이다.

### indexOf
```java
public int indexOf(Object target) {
    for (int i = 0; i < size; i++) {
        boolean equals = Objects.equals(target, get(i));
        if (equals) return i;
    }
    return -1;
}
```
indexOf는 반복문을 돌 때마다 equals를 실행한다.
먼저 equals는 크기에 의존하지 않으므로 상수시간으로 상정한다.
따라서 반복문 안의 내용은 상수시간으로 볼 수 있다.

반복문은 운이 좋으면 한 번에, 운이 나쁘면 size만큼 테스트해야 한다.
따라서 배열이 커지면 이에 따라 테스트할 데이터도 많아지므로 `선형`이다.

### remove
```java
public T remove(int index) {
    T old = array[index];
    for (int i = index; i < size - 1; i++) {
        array[i] = array[i + 1];
    }
    size--;
    return old;
}
```
remove는 반복문 이외에는 상수시간이다. 반복문 안의 내용 역시 상수시간이다.
반복문은 운이 좋으면 마지막 데이터 하나, 운이 나쁘면 (전체 데이터 - 1)개를 테스트한다.
따라서 배열이 커지면 이에 따라 테스트할 데이터도 많아지므로 `선형`이다.

---
[Home](../README.md)
