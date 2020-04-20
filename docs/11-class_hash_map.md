# 11. HashMap 클래스
이전 장에서 만든 Map 인터페이스의 구현체는 n개의 엔트리와 k개의 하위 맵이 존재한다면 엔트리를 찾는데는 평균적으로 n/k가 소요되고 이는 결국 선형이다. 하지만 n과 함께 k의 수를 늘려나간다면 n/k를 제한할 수 있게 된다.
따라서 하위 맵당 엔트리의 개수가 일정하면 단일 하위 맵을 찾는데는 `상수 시간`이 걸리며, 해시 함수는 일반적으로 `상수 시간`이므로 `put과 get 메서드를 상수 시간`으로 만든다. 

## 실습
- MyHashMap.java

```java
public class MyHashMap<K, V> extends MyBetterMap<K, V> implements Map<K, V> {
	protected static final double FACTOR = 1.0;

	@Override
	public V put(K key, V value) {
		V oldValue = super.put(key, value);

		if (size() > maps.size() * FACTOR) {
			rehash();
		}
		return oldValue;
	}
}
```

- FACTOR는 로드 팩터(load factor)라고 하여 하위 맵당 최대 엔트리 개수를 결정한다.
- `n > k * FACTOR`는 `n/k > FACTOR`를 의미하므로 하위 맵당 임계치를 넘으면 rehash()를 호출한다.

```java
protected void rehash() {
    Collection<Entry<K, V>> items = new ArrayList<>();
    for (MyLinearMap<K, V> map : maps) {
        items.addAll(map.getEntries());
    }

    makeMaps(maps.size() * 2);
    items.forEach(item -> put(item.getKey(), item.getValue()));
}
```

## MyHashMap 분석하기
n과 k가 같이 늘어나면서 MyBetterMap 클래스의 몇몇 핵심 메서드도 상수 시간이 된다.

- containsKey
- get
- remove

하지만 put는 재해시(rehash)를 하지 않는다면 상수 시간이지만, 재해시를 한다면 선형이 된다.
따라서 [분할 상환 분석](03-array_list.md)을 기반으로 판단하면 이는 상수 시간이라고 한다.

하위 맵의 개수 k의 초기값을 2, 로드 팩터가 1이라고 가정했을 때 일련의 키를 넣으면 얼마나 많은 작업이 발생하는지 알아본다. 기본 `작업 단위(unit of work)`로 키를 재해시하며 이 키를 하위 맵에 추가하는 횟수를 세어본다.

1. 첫 번째 키, put => 작업 단위 1
2. 두 번째 키, put => 작업 단위 1
3. 세 번째 키, put => 재해시, 새로운 키 해시 => 작업 단위 2 + 1
4. 네 번째 키, put => 작업 단위 1
5. 다섯 번쨰 키, put => 재해시, 새로운 키 해시 => 작업 단위 4 + 1

## 트레이드 오프

- 핵심 메서드가 상수 시간이 걸리므로 해시 테이블의 크기에 상관 없이 대개 성능이 일정하다. 
  하지만 물리적으로 캐시에 들어갈만한 크기가 아니면 메모리, 메모리도 맞지 않으면 디스크 등으로 내려가며 성능이 떨어질 것이다.
- 키가 아닌 값으로 검색해야 한다면 해싱이 그다지 도움이 되지 않는다는 것이다.
- MyLinearMap에 존재했던 clear 같은 함수는 하위 맵에 비례하여 실행되므로 선형이 된다.

---
[Home](../README.md)
