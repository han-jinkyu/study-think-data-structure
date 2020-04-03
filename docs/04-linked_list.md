# 4. LinkedList 클래스

## MyLinkedList 메서드 분류하기

### indexOf
```java
@Override
public int indexOf(Object target) {
    Node node = head;
    for (int i = 0; i < size; i++) {
        if (equals(target, node.data)) {
            return i;
        }
        node = node.next;
    }
    return -1;
}
```

- equals는 상수시간
- 반복은 n번 실행
- 즉 `O(n)`

### add
```java
@Override
public void add(int index, E element) {
    if (index == 0) {
        head = new Node(element, head);
    } else {
        Node prev = getNode(index - 1);
        prev.next = new Node(element, prev.next);
    }
    size++;
}
```

-  `index == 0`은 예외사항
- getNode는 선형시간
- 그 외는 상수시간
- 즉 `O(n)`

### remove
```java
@Override
public E remove(int index) {
    Node old;
    if (index == 0) {
        old = head;
        head = old.next;
    } else {
        Node prev = getNode(index - 1);
        old = prev.next;
        prev.next = old.next;
    }
    size--;
    return old.data;
}
```

- getNode는 선형
- 이외는 상수시간
- 즉 `O(n)`


## MyArrayList와 MyLinkedList 비교

| 구분                | MyArrayList | MyLinkedList |
|:-------------------|:-----------:|:------------:|
|add(끝)              | 1           | n            |
|add(시작)            | n           | 1            |
|add(일반적)           | n           | n            |
|get/set             | 1           | n            |
|indexOf/lastIndexOf | n           | n            |
|isEmpty/size        | 1           | 1            |
|remove(끝)           | 1           | n            |
|remove(시작)         | n           | 1            |
|remove(일반적)        | n           | n           |


---
[Home](../README.md)
