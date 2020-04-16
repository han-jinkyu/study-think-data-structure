# 9. Map 인터페이스
Map 인터페이스의 구현 중 하나는 해시테이블(hashtable)에 기반을 둔다. TreeMap 클래스와 비슷한 또 다른 하나는 요소를 순서대로 반복할 수 있는 추가 기능을 제공한다. 이런 자료구조를 구현해 보고 성능을 분석해 본다. 

## MyLinearMap 구현하기 
먼저 MyLinearMap 클래스의 빈 부분을 채워보도록 한다. (MyLinearMap.java)

```java
public class MyLinearMap<K, V> implements Map<K, V> {
	private List<Entry> entries = new ArrayList<Entry>();
}
```

- K, V라는 두 개의 타입 파라미터를 받는다
- entries는 Entry 객체를 담은 리스트다. 각 Entry 객체는 키-값 쌍을 포함한다.

```java
public class Entry implements Map.Entry<K, V> {
    private K key;
    private V value;

    public Entry(K key, V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public K getKey() {
        return key;
    }
    @Override
    public V getValue() {
        return value;
    }
    @Override
    public V setValue(V newValue) {
        value = newValue;
        return value;
    }
}
```

## 실습
- MyLinearMap.java
- MyLinearMapTest.java

```java
private Entry findEntry(Object target) {
    return entries.stream()
            .filter(e -> equals(target, e.key))
            .findFirst()
            .orElse(null);
}
```
- equals는 상수시간이다.
- 운이 좋으면 처음에 찾겠지만 운이 나쁘면 entries를 전체 다 확인해야 하므로 선형시간이다.

```java
@Override
public V get(Object key) {
    Optional<Entry> maybeEntry = Optional.ofNullable(findEntry(key));
    return maybeEntry.map(e -> e.value).orElse(null);
}

@Override
public V put(K key, V value) {
    Entry entry = findEntry(key);
    if (entry == null) {
        entries.add(new Entry(key, value));
        return null;
    } else {
        entry.setValue(value);
        return entry.value;
    }
}

@Override
public V remove(Object key) {
    Entry entry = findEntry(key);
    if (entry == null) {
        return null;
    }
    return entries.remove(entry) ? entry.value : null;
}
```

- 모두 findEntry를 사용하고 있으므로 일단 평균적으로 선형시간이다.
- get과 put은 다른 특별한 처리가 없으므로 선형시간이다.
- remove는 삭제시에 시작이나 중간의 요소를 제거해야 한다. 하지만 역시 선형시간이다.

- 핵심 메서드가 선형시간을 필요로 하므로 엔트리 개수가 적으면 유용하겠지만 개선할 여지가 남아 있다.
    - 커다란 하나의 리스트를 다수의 작은 리스트로 쪼갠다.
    - 각 키에 대해 해시코드(hash code)를 사용하여 어느 리스트를 사용할지 선택한다.
    - 리스트의 개수를 늘려서 리스트 당 엔트리 개수를 제한할 수 있다면 상수 시간 맵이 된다!
    
---
[Home](../README.md)
