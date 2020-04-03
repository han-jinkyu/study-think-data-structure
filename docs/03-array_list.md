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

### removeAll
```java
public boolean removeAll(Collection<?> collection) {
    boolean flag = true;
    for (Object obj: collection) {
        flag &= remove(obj);
    }
    return flag;
}
```
이 메서드는 remove라는 선형 메서드를 호출한다.
만약 collection의 요소 개수를 m, MyArrayList의 배열의 요소 개수를 n이라고 가정한다면 
이 메서드는 O(nm)이다.
 
- m이 상수(고정)이라면 n에 대해 `선형`이다.
- m이 n에 비례한다면 이 메서드는 `이차`다

`문제 크기 (Problem size)`에 대한 이야기할 때 대상이 어떤 크기들인지에 주의해야 한다.
반복문만 세는 것으로는 정확한 분석을 할 수 없다.

### add
```java
public void add(int index, T element) {
    if (index < 0 || index > size) {
        throw new IndexOutOfBoundsException();
    }
    // add the element to get the resizing
    add(element);

    // shift the elements
    for (int i=size-1; i>index; i--) {
        array[i] = array[i-1];
    }
    // put the new one in the right place
    array[index] = element;
}
```
add 메서드 중 첫번째는 오버로딩되어 있는 add가 있고, 반복문이 존재한다. 이외는 선형시간.
따라서 또 다른 add 메서드를 제외하면 `선형`으로 볼 수 있다. 

```java
public boolean add(T element) {
    if (size >= array.length) {
        T[] bigger = (T[])new Object[size * 2];
        System.arraycopy(array, 0, bigger, 0, array.length);
        array = bigger;
    }
    array[size++] = element;
    return true;
}
```
또 다른 add 메서드는 System.arraycopy가 배열의 크기에 따라 달라지고
그 이외는 상수시간이므로, 이 메서드는 `선형`으로 볼 수 있다.

add는 총 연산횟수가 `2n-2`다. 
이의 평균횟수를 구하려면 데이터 개수인 n으로 나누어서 `2 - 2/n`이 된다.
따라서 이 원칙을 적용하면 가장 큰 차수는 2이므로 add는 상수시간으로 평가할 수 있다.
이런 방식을 `분할 상환 방식`라고 부른다.

### 분할 상환 분석 (Amortized analysis)
일련의 호출에서 평균 시간을 계산하는 알고리즘 분류 방법을 `분할 상환 분석`이라 한다.
일련의 호출을 하는 동안 배열을 복사하는 추가 비용이 분산되거나 분할 상환되었다는 의미라 한다. 
[위키 참조](https://ko.wikipedia.org/wiki/분할상환분석)

## 연결 자료 구조
`연결`이라는 것은 노드(Node)라는 객체가 다른 노드에 대한 참조를 포함한 형태로 저장하는 것을 뜻한다.
연결리스트에서 각 노드는 리스트의 다음 노드를 참조하고 있다. 연결 구조의 다른 예로는 `트리`와 `그래프`가 있다.

```java
public class ListNode {
    public Object data;
    public ListNode next;

    public ListNode() {
        this.data = null;
        this.next = null;
    }
    
    public ListNode(Object data) {
        this.data = data;
        this.next = null;
    }

    public ListNode(Object data, ListNode next) {
        this.data = data;
        this.next = next;
    }
    
    public String toString() {
        return String.format("ListNode(%s)", data.toString());
    }
}
``` 
1. ListNode에는 두 개의 인스턴스 변수가 존재한다.
    - data 변수는 어떤 Object의 참조
    - next 변수는 리스트에서 다음 노드에 대한 참조다.
 
1. 리스트의 마지막 노드에서 관례상 next 변수는 null이다.
1. 리스트 생성은 간단하게 다음과 같다.
    ```java
    ListNode node1 = new ListNode(1);
    ListNode node2 = new ListNode(2);
    ListNode node3 = new ListNode(3);
    
    node1.next = node2;
    node2.next = node3;
    node3.next = null;
    
    ListNode node0 = new ListNode(0, node1);
    System.out.println(node0);
    ```
   
## 실습
- MyLinkedList.java
- MyLinkedListTest.java

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

## 가비지 컬렉션
앞의 실습에서 MyArrayList 클래스는 필요하면 배열이 늘어나지만 줄어들지는 않는다. 
배열은 가비지 컬렉션을 하지 않고 그 요소도 리스트 자체가 파괴될 때까지 가비지 컬렉션 대상이 되지 않는다.

연결 리스트의 한 가지 장점은 요소를 제거하면 리스트 크기가 줄어들며 사용하지 않는 노드는 즉시 가비지 컬렉션 대상이 된다.

```java
public void clear() {
    head = null;
    size = 0;
}
``` 
첫번째 노드를 null로 만들면 첫 번째 노드에 대한 참조를 제거하게 된다. 참조가 사라지므로 가비지 컬렉션 대상이 된다.
그렇게 되면 두번째 노드의 참조가 제거되고 역시 가비지 컬렉션 대상이 된다. 
이 절차가 모든 노드가 사라질 가비지 컬렉션 대상이 될 때까지 계속된다. 

clear 메서드는 상수 시간으로 보이지만 호출할 때 `요소의 개수에 비례하여 가비지 컬렉터가 동작`한다. 
따라서 `선형으로 간주`해야 된다. 이런 것을 `성능 버그(Performance bug)`라고 할 수 있다.

자바와 같은 언어는 가비지 컬렉터처럼 뒤에서 많은 일을 하기에 이런 종류의 버그는 찾기 어렵다.

---
[Home](../README.md)
