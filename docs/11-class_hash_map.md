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

---
[Home](../README.md)
