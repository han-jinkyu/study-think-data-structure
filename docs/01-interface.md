# 1. 인터페이스
리스트는 두 종류가 존재한다. 각각의 동작 방법과 장단점을 살펴본다.
- ArrayList
- LinkedList

## 자바에서의 interface
자바 interface는 메서드 집합을 의미한다. interface를 구현하는 클래스는 
interface가 추상화한 메서드를 구체화하여야 한다.

```java
public interface Comparable<T> {
    public int compareTo(T o);
}

public final class Integer extends Number implements Comparable<Integer> {
    
    public int compareTo(Integer anotherInteger) {
        int thisVal = this.value;
        int anotherVal = anotherInteger.value;

        if (thisVal < anotherVal) return -1;
        else if (thisVal < anotherVal) return 1;
        else return 0;
    }
}
```
## List interface
JCF(Java Collection Framework)에서는 List라는 interface를 정의하고
ArrayList와 LinkedList를 제공한다. 그렇기 때문에 List interface가 갖고 있는
`add, get, remove 등의 특정 메서드를 제공`해야 한다. 또한 이러한 특성으로 인해
List형으로 선언한다면 둘을 손쉽게 교체할 수 있다.

```java
public class ListClient {
    private List list;
   
    public ListClient() {
        // 어느 것을 적용하든지 문제 없다!
        this.list = new ArrayList();        // 선택지1
        // this.list = new LinkedList();    // 선택지2
    }   

    public List getList() {
        return this.list;
    }   

    public static void main (String[] args) {
        ListClient client = new ListClient();
        List list = client.getList();
        System.out.println(list);
    }
}
```

## 실습
ListClientExampleTest를 통하여 테스트를 해보면 ArrayList가 아니면 에러가 난다.
ListClientExample의 Constructor 부분에 정의된 LinkedList를 ArrayList로 바꾼다.

- 반환부의 List를 바꾸지 않았지만 에러가 나지 않는다.
- 반환부의 List를 ArrayList로 바꾼다면 '과다 지정(overspecified)'가 된다
- 이는 다시 인터페이스로 돌아가기 위해선 많은 비용이 소요된다

---
[Home](../README.md)
