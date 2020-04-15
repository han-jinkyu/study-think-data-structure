# 8. 인덱서

웹 검색에서 인덱스는 검색어를 바탕으로 관련 페이지를 찾을 수 있게 하는 자료구조다. 또한 검색어가 각 페이지에서 몇 번이나 등장하는지 알아내어 가장 관련성이 높은 페이지를 식별할 수 있게 한다.

예를 들어, 'Java'와 'programming'이라는 검색어를 입력하면 두 검색어를 모두 검색하고 집합을 골라낸다. 두 검색어에 관한 페이지를 선택하여 관련성이 적은 페이지는 걸러내고 자바 프로그래밍에 대한 페이지만 찾기를 기대한다.

## 자료구조 선택
인덱스의 가장 기본 연산은 조회(lookup)이다. 특히 검색어를 조회하여 검색어를 포함한 모든 페이지를 찾는 능력이 필요하다.

가장 단순한 구현은 `페이지의 컬렉션`이다. 검색어가 주어지면 페이지 내용을 반복 조사하여 검색어를 포함한 페이지를 선택한다. 하지만 모든 페이지의 단어 수에 비례하여 실행시간이 느리다.

좀더 나은 대안은 `맵(map)`이다. 키-값 쌍으로 이루어진 컬렉션이다. 예를 들어, 키에는 검색어, 값은 횟수 혹은 빈도를 넣는다. (TermCounter 클래스 참조)

자바의 Map 인터페이스는 맵을 구현하는 데 필요한 메서드를 정의하는데 다음과 같다.

- get(key) : 키에 해당하는 값을 반환한다
- put(key, value) : 키-값 쌍을 추가하거나 이미 존재하면 값을 대체한다

자바는 Map 인터페이스의 몇 가지 구현을 제공하는데 이 안에 HashMap과 TreeMap이 존재한다.

검색어의 등장 횟수를 매핑하는 TermCounter 클래스와 함께 Index라는 클래스도 정의한다. 이 클래스는 검색어와 검색어가 등장하는 페이지의 컬렉션을 매핑한다.

페이지의 컬렉션은 두 개 이상의 컬렉션을 조합하여 모든 컬렉션 검색어가 나타나는 페이지를 찾아야 한다. 이 연산은 교집합(intersection) 연산으로, 집합이 수행하는 연산을 정의해놓은 `Set 인터페이스`를 사용해야 한다. 핵심 메서드는 다음과 같다.

- add(element) : 요소를 추가한다. 동일 요소가 이미 존재하면 효과가 없다.
- contains(element) : 주어진 요소가 집합에 포함되어 있는지 확인한다.

자바는 HashSet과 TreeMap 클래스와 같은 구현을 제공한다.

## TermCounter
TermCounter 클래스는 검색어와 검색어가 등장하는 횟수를 매핑한다. (TermCounter.java)

```java
public class TermCounter {

	private Map<String, Integer> map;
	private String label;

	public TermCounter(String label) {
		this.label = label;
		this.map = new HashMap<String, Integer>();
	}
}
```

- map : 검색어와 등장 횟수를 매핑한다.
- label: 검색어의 출처가 되는 문서를 식별한다. 여기서는 URL을 저장한다.

```java
public void put(String term, int count) {
    map.put(term, count);
}

public Integer get(String term) {
    Integer count = map.get(term);
    return count == null ? 0 : count;
}

public void incrementTermCount(String term) {
    put(term, get(term) + 1);
}
``` 

- put : 단순한 래퍼(Wrapper) 메서드다.
- get : map 변수로부터 값을 가져온다. 존재하지 않는다면 0을 반환한다.
- incrementTermCount : map에 저장되어 있는 횟수를 하나 더한다.

```java
public void processElements(Elements paragraphs) {
    for (Node node: paragraphs) {
        processTree(node);
    }
}

public void processTree(Node root) {
    for (Node node: new WikiNodeIterable(root)) {
        if (node instanceof TextNode) {
            processText(((TextNode) node).text());
        }
    }
}

public void processText(String text) {
    String[] array = text.replaceAll("\\pP", " ").
                          toLowerCase().
                          split("\\s+");
    
    for (int i=0; i<array.length; i++) {
        String term = array[i];
        incrementTermCount(term);
    }
}
```
다음은 TermCounter에서 제공하는 웹 페이지를 인덱싱하는데 필요한 보조 메서드다.

- processElements
    - jsoup의 Element 객체 컬렉션인 Elements를 인자로 받는다
    - 컬렉션을 반복하여 실행하고 processTree 메서드를 호출한다
- processTree
    - DOM 트리의 루트를 나타내는 jsoup 라이브러리의 Node 객체를 인자로 받는다.
    - 트리를 반복하여 실행하고 텍스트를 포함한 노드가 존재하면 텍스트를 추출하여 processText 메서드를 호출한다.
- processText
    - 구두점(.)은 공백으로 대체하여 소문자로 변환한다
    - 그리고 이들을 공백으로 나눈다
    - 그런 뒤 각 단어를 추출하여 incrementTermCount 메서드를 호출한다

## 실습
- TermCounter.java
- TermCounterTest.java
- Index.java
- WikiFetcher.java
- WikiNodeIterable.java

### TermCounter
```java
public int size() {
    return map.values().stream().mapToInt(i -> i).sum();
}
```

### Index
검색어와 연관된 집합을 다루는 클래스.

```java
public void indexPage(String url, Elements paragraphs) {
    // make a TermCounter and count the terms in the paragraphs
    TermCounter tc = new TermCounter(url);
    tc.processElements(paragraphs);

    // for each term in the TermCounter, add the TermCounter to the index
    for (String term : tc.keySet()) {
        add(term, tc);
    }
}
``` 

---
[Home](../README.md)
