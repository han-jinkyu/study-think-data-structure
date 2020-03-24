# 2. 알고리즘 분석

## 알고리즘 분석
어느 클래스가 더 좋은지 결정하려면 둘 다 시도해보고 각각 시간이 얼마나 걸리는지 
확인해보면 된다. 이런 접근을 `프로파일링`이라고 한다. 하지만 몇 가지 문제점이 있다.

1. 모두 구현해봐야 된다
1. 컴퓨터 성능에 의존한다
1. 입력에 사용되는 데이터에 의존하기도 한다

이를 `알고리즘 분석`으로 해결할 수 있다. 이를 위해선 몇 가지 가정이 필요하다.

1. 알고리즘을 이루는 기본 연산을 식별하여 연산 수를 센다
1. 기대하는 입력 데이터에 대한 평균값을 분석한다. 불가능하면 최악의 수를 생각한다.
1. 작은 문제에선 최상의 성능을 보여주지만 큰 문제에선 반대의 경우가 있을 수 있다.
   이를 염두해야 한다.
   
이런 분석을 통하면 대부분 아래의 범주로 표현할 수 있다.

|           종류          | 표현식 |
|------------------------|:-----:|
| 상수시간 (constant time) | 1     |
| 선형 (linear)           | n     |
| 이차 (quadratic)        | n^2   |


## 선택정렬
```java
public class SelectionSort {

	/**
	 * i와 j의 위치에 있는 값을 바꿉니다
	 */
	public static void swapElements(int[] array, int i, int j) {
		int temp = array[i];
		array[i] = array[j];
		array[j] = temp;
	}

	/**
	 * start로부터 시작하는 최솟값의 위치를 찾고(start 포함)
	 * 배열의 마지막 위치로 갑니다
	 */
	public static int indexLowest(int[] array, int start) {
		int lowIndex = start;
		for (int i = start; i < array.length; i++) {
			if (array[i] < array[lowIndex]) {
				lowIndex = i;
			}
		}
		return lowIndex;
	}

	/**
	 * 선택 정렬을 사용하여 요소를 정렬한다
	 */
	public static void selectionSort(int[] array) {
		for (int i = 0; i < array.length; i++) {
			int j = indexLowest(array, i);
			swapElements(array, i, j);
		}
	}
}
```

1. swapElements
    - 배열의 두 요소의 값을 바꾼다. 
    - 이는 `상수시간`이다.
1. indexLowest
    - start부터 시작하여 반복하며 가장 작은 요소를 찾는다.
    - 이는 `선형`이다.
1. selectionSort
    - 반복문을 돌면서 indexLowest와 swapElements를 실행한다.
    - indexLowest는 선형이고 swapElements는 상수.
    - 반복하면서 선형 메서드인 indexLowest를 실행하므로 `이차`다.
    
## 빅오 표기법

|           종류          | 빅오 표기법 |
|------------------------|:--------:|
| 상수시간 (constant time) | O(1)     |
| 선형 (linear)           | O(n)     |
| 이차 (quadratic)        | O(n^2)   |

서로 섞여있다면 제일 큰 차수로 표현된다. 예를 들어, `상수시간`과 `선형`이 같이 존재하면 
이는 `선형`이 된다.

## 실습
다음의 클래스에서 다음의 메서드를 구현한다. 그리고 테스트를 통과하면 된다.

- MyArrayList.java
- MyArrayListTest.java

```java
@Override
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
    
```java
@Override
public int indexOf(Object target) {
    for (int i = 0; i < size; i++) {
        boolean equals = Objects.equals(target, get(i));
        if (equals) return i;
    }
    return -1;
}
```

```java
@Override
public T remove(int index) {
    T old = array[index];
    for (int i = index; i < size - 1; i++) {
        array[i] = array[i + 1];
    }
    size--;
    return old;
}
```

```java
@Override
public T set(int index, T element) {
    T old = get(index);
    array[index] = element;
    return old;
}
```

---
[Home](../README.md)
