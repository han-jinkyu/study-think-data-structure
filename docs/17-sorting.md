# 17. 정렬

정렬 알고리즘을 배워야 하는 이유는 다음과 같다.

1. 알아두어야 할 `특수 알고리즘`이 있기 때문이다.
    - `기수 정렬(radius sort)`
    - `제한된 힙 정렬(bounded heap sort)`
    
2. `병합 정렬(merge sort)`는 교육적으로 훌륭한 예이며, 알고리즘 설계에서 가장 중요하고 유용한 전략인 `분할 정복법(divide-conquer-glue)`을 보여주기 때문이다. 또한 알고리즘 성능을 분석할 때 보지 못한 `선형 로그(linearithmic)`라는 증가 차수를 배울 수 있다.

3. 기술 면접관이 이에 관해 질문하기 때문이다.


## 삽입 정렬

> 삽입 정렬(揷入整列, insertion sort)은 자료 배열의 모든 요소를 앞에서부터 차례대로 이미 정렬된 배열 부분과 비교하여, 자신의 위치를 찾아 삽입함으로써 정렬을 완성하는 알고리즘이다. [위키 참조](https://ko.wikipedia.org/wiki/삽입_정렬)

```java
public void insertionSort(List<T> list, Comparator<T> comparator) {
    for (int i=1; i < list.size(); i++) {
        T elt_i = list.get(i);
        int j = i;
        while (j > 0) {
            T elt_j = list.get(j-1);
            if (comparator.compare(elt_i, elt_j) >= 0) {
                break;
            }
            list.set(j, elt_j);
            j--;
        }
        list.set(j, elt_i);
    }
}
```

- 2개의 중첩된 반복문이 있으므로 실행시간은 2차로 추측할 수 있다. 하지만 각 반복문의 실행 횟수가 배열의 크기인 n에 비례하는지 살펴봐야 한다.
    - 외부 반복문은 list.size()까지 반복한다. 따라서 리스트 크기인 n에 선형이다.
    - 내부 반복문은 i에서 0까지 반복하므로 역시 n에 선형적이다.
    
- 최악일 때는 2차지만 다음과 같은 특징이 있다.
    1. 요소가 이미 정렬되어 있거나 거의 정렬되어 있다면 선형이다. 특히 각 요소가 있어야 하는 자리 기준 k 이하의 위치에 있다면 내부 반복문은 k번 이하로 동작하게 되므로 전체 실행시간은 O(kn)이다.
    2. 구현이 단순하므로 오버헤드가 작다. 즉, 실행시간은 최대 an^2이지만 최대 차수의 계수인 a는 아마도 작을 것이다.


---
[Home](../README.md)
