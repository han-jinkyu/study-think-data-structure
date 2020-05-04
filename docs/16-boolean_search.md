# 16. 불리언 검색

## 크롤러 해답

- crawl (WikiCrawler.java)
    1. 큐가 비어 있으면 페이지를 인덱싱하지 않았다는 것으로 null 반환.
    2. 큐가 비어 있지 않으면 큐에서 URL을 꺼낸다.
    3. URL이 인덱싱되어 있다면 다시 인덱싱하지 않고 null 반환.
    4. 페이지 내용을 읽는다. 테스트 모드면 파일에서 읽고 아니면 웹에서 읽는다.
    5. 페이지를 인덱싱한다. 
    6. 페이지를 파싱하고 내부 링크에 큐를 넣는다.
    7. 인덱싱한 페이지의 URL을 반환한다.


## 정보 검색

다음 단계에 할 것은 `검색 도구 구현`이다. 아래와 같은 동작들을 일반적인 용어로 `정보 검색(information retrieval)`이라고 한다.

1. 사용자가 검색어를 입력하고 결과를 보는 인터페이스
2. 각 검색어를 가져와서 검색어를 포함하는 페이지를 반환하는 조회 메커니즘
3. 다수의 검색어로부터 검색 결과를 조합하는 메커니즘
4. 검색 결과의 순위와 정렬 알고리즘


## 불리언 검색

대부분 검색 엔진은 `불리언 검색 (boolean search)`을 수행한다. 불리언 검색은 불리언 로직을 사용하여 다수의 검색어로부터의 결과를 조합하는 것이다. 아래처럼 검색어와 연산을 함께 포함한 표현을 `쿼리 (query)`라고 한다.

- 'java AND programming'으로 검색하면 'java'와 'programming' 검색어를 모두 포함한 페이지만 반환해야 한다
- 'java OR programming'으로 검색하면 둘 중 한 단어는 포함하지만, 둘 다 포함할 필요는 없다
- 'java -indonesia'로 검색하면 'java'는 포함하지만, 'indonesia'를 포함해서는 안 된다


- `AND`: 교집합 (intersection)
- `OR`: 합집합 (union)
- `-`: 차집합 (difference)


## 실습

- WikiSearch.java
- WikiSearchTest.java
- Card.java

- 정보 검색의 관점에서 `관련성 점수(relevance score)`는 쿼리에서 추론한 사용자의 요구를 페이지가 얼마나 잘 반영하였는지를 나타낸 점수다.
- 관련성 점수를 매기는 대부분의 방법은 어떤 페이지에 검색어가 몇 번이나 등장하였는지를 의미하는 `용어 빈도(term frequency)`에 기반을 둔다.
    - `단일 검색어`를 포함한 쿼리일 때, 페이지의 관련성은 `TF`다. 즉, 페이지에 검색어가 등장한 횟수다.
    - `다수의 검색어`를 포함한 쿼리일 때, 페이지의 관련성은 `TF의 합`이다. 즉, 검색어가 등장한 총 횟수다. 
- 일반적인 관련성 점수는 `용어 빈도-역 문서 빈도(term frequency-inverse documents frequency)`를 나타내는 [TF-IDF](https://en.wikipedia.org/wiki/Tf–idf)이다


## Comparable과 Comparator

- Comparable 인터페이스는 `compareTo 메서드`를 제공한다.
    - compareTo 메서드의 명세는 this가 that보다 작으면 음수를 반환하고, this가 더 크면 양수를 반환하고, 두 개가 같으면 0을 반환하게 되어 있다.
- Comparable 인터페이스를 구현하고 있다면 Collections.sort 메서드를 이용하여 정렬할 수 있다.
    - 인자로 컬렉션만 넘긴다면 `자연 순서(natural order)`로 정렬한다.
    - 다른 순서로 정렬하고 싶다면 `Comparator 객체`를 주입하여 다른 순서로 정렬할 수 있다.
- Comparator 객체를 정의한다면 `compare 메서드`를 구현할 수 있다. 

---
[Home](../README.md)
