# 10. 해싱

## 해싱
MyLinearMap 클래스의 성능을 향상하고자 MyLinearMap 객체를 포함하는 MyBetterMap을 만든다.
내장된 맵에 따라 키를 나누므로 각 맵의 엔트리 개수는 줄어든다. 즉 findEntry 메서드의 속도가 빨라지게 된다.

```java
public class MyBetterMap<K, V> implements Map<K, V> {
	protected List<MyLinearMap<K, V>> maps;

	public MyBetterMap(int k) {
		makeMaps(k);
	}

	protected void makeMaps(int k) {
		maps = new ArrayList<MyLinearMap<K, V>>(k);
		for (int i=0; i<k; i++) {
			maps.add(new MyLinearMap<K, V>());
		}
	}
}
```

- 생성자는 k를 인자로 받아 얼마나 많은 맵을 사용할지 정의한다.
- 핵심은 키를 살펴보고 어느 맵에 넣을지 결정하는 것이다.
    - 한가지는 무작위로 선택하는 것. 하지만 추적이 어렵다.
    - 더 나은 접근법은 `해시 함수(hash function)`을 이용하는 것이다.
    - 해시 함수는 Object를 인수로 받아 해시 코드라는 정수를 반환한다.
    - 자바는 모든 Object 객체에서 hashCode라는 메서드를 제공한다.
    
```java
protected MyLinearMap<K, V> chooseMap(Object key) {
    int index = key==null ? 0 : Math.abs(key.hashCode()) % maps.size();
    return maps.get(index);
}
```

- chooseMap 헬퍼 메서드는 key를 분석하여 임의의 맵을 선택한다.
- 계산식의 결과가 항상 같이 때문에 key가 같다면 항상 같은 맵을 선택할 것이다.

```java
@Override
public V get(Object key) {
    MyLinearMap<K, V> map = chooseMap(key);
    return map.get(key);
}

@Override
public V put(K key, V value) {
    MyLinearMap<K, V> map = chooseMap(key);
    return map.put(key, value);
}
```

- get과 put 메서드는 키에 따른 맵을 골라 값을 넣어나 뺀다

성능에 대해 생각해본다면 다음과 같다.

- n개의 엔트리를 k개의 하위 맵으로 나누면 맵당 엔트리는 평균 n/k개가 된다.
- 키를 조회할 때 해시 코드를 계산해야 되는데 이 때 시간이 걸린다.
- k개로 나누었으므로 각 MyLinearMap은 검색시간이 k배 빨라졌을 것이다.
- 하지만 실행시간은 여전히 n에 비례하므로 MyBetterMap은 여전히 선형 시간이다.

## 해싱의 동작 방식
해시 함수의 근본적인 요구사항은 같은 객체라면 매번 같은 해시 코드가 생성되어야 한다는 것이다.
이는 불변 객체(immutable object)일 때는 상대적으로 쉽지만 가변 객체(mutable object)일 때는 좀 더 고민해야 한다.

아래 SillyString이라는 클래스를 통해 자신만의 해시 함수를 정의하는 방법을 알아본다.

```java
public class SillyString {
	private final String innerString;

	public SillyString(String innerString) {
		this.innerString = innerString;
	}

	public String toString() {
		return innerString;
	}
    
    @Override
    public boolean equals(Object other) {
        return this.toString().equals(other.toString());
    }

    @Override
    public int hashCode() {
        int total = 0;
        for (int i=0; i<innerString.length(); i++) {
            total += innerString.charAt(i);
        }
        System.out.println(total);
        return total;
    }
}
```

- equals와 hashCode 메서드를 모두 오버라이드한다. 
    - 제대로 동작하려면 equals 메서드는 hashCode와 일치해야 한다.
    - 단 hashCode가 같다고 해서 같은 객체라고 볼 수는 없다.  
- 이 해시 함수는 정확하게 동작하나 좋은 성능을 보장하지는 않는다.
    - 문자열 내의 문자가 순서가 다르더라도 같은 해시 코드를 생성한다.
    - 심지어 'ac'와 'bb'는 같은 해시 코드를 생성할 것이다.
- 만약 많은 객체가 동일한 해시 코드를 갖는다면 특정 하위 맵에 객체가 몰리게 된다.
    - 즉 해시 함수의 목표 중 하나는 '균등해야(uniform) 한다'는 것이다. 값이 골고루 퍼지도록 많들어야 된다는 것이다. [참조](https://en.wikipedia.org/wiki/Hash_function)
    
## 해싱과 변형
String 클래스는 불변하며 SillyString 역시 innerString 변수가 final로 선언되었으므로 불변이다. 즉 항상 같은 해시코드를 갖게 된다.

아래는 SillyString과 비슷하지만 인스턴스 변수로 문자 배열을 사용한다는 점이 다른 SillyArray 클래스다.
```java
public class SillyArray {
	private final char[] array;

	public SillyArray(char[] array) {
		this.array = array;
	}

	public String toString() {
		return Arrays.toString(array);
	}
	
	public void setChar(int i, char c) {
		this.array[i] = c;
	}
	
	@Override
	public boolean equals(Object other) {
		return this.toString().equals(other.toString());
	}
	
	@Override
	public int hashCode() {
		int total = 0;
		for (int i=0; i<array.length; i++) {
			total += array[i];
		}
		System.out.println(total);
		return total;
	}
}
```

- setChar를 이용해서 배열을 변경할 수 있다.
- 변경하면 해시 코드가 변형된다. 이렇게 되면 해시 코드가 달라서 잘못된 하위맵을 조회하게 된다.
- 따라서 가변 객체를 키로 이용하는 것은 위험하다.

---
[Home](../README.md)
