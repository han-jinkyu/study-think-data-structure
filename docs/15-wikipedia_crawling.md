# 15. 위키피디아 크롤링
 
## 레디스 기반의 인덱서

- indexPage
```java
public void indexPage(String url, Elements paragraphs) {
    TermCounter tc = new TermCounter(url);
    tc.processElements(paragraphs);

    Transaction t = jedis.multi();

    for (String term : tc.keySet()) {
        add(term, tc);
        jedis.hset(termCounterKey(url), term, tc.get(term).toString());
    }

    t.exec();
}
```

- getCounts
```java
public Map<String, Integer> getCounts(String term) {
    Set<String> urls = getURLs(term);
    return urls.stream()
        .map(url -> new AbstractMap.SimpleEntry<>(url, getCount(url, term)))
        .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
}

public Set<String> getURLs(String term) {
    return jedis.smembers(urlSetKey(term));
 }

public Integer getCount(String url, String term) {
    String value = jedis.hget(termCounterKey(url), term);
    if (value == null || value.isEmpty()) return 0;
    return Integer.parseInt(value);
}
```


## 조회 성능 분석

N개의 페이지를 인덱싱하고 M개의 고유한 검색어를 검색하는 경우?

- 검색어를 조회할 때 getCounts 메서드를 호출하면
    - 다음과 같은 순서를 진행한다
        1. 맵을 생성한다
        2. getURLs 메서드를 호출하여 URL 집합을 가져온다
        3. 집합에 있는 각 URL에 대해 getCount 메서드를 호출하고 맵에 추가한다
    - 일반적인 검색어는 N만큼 클 수 있다.
- 개별로 레디스에 전송하면 느리지만 트랜잭션을 이용하여 한꺼번에 처리하면 빠르다.


## 인덱싱 성능 분석

한 페이지를 인덱싱하는 데 걸리는 시간은?

- DOM 트리를 순회하고 모든 TextNode 객체를 찾고 문자열을 검색어로 쪼개야 한다. 이는 단어 개수에 비례한다.
- 각 검색어에 대해 HashMap에 있는 카운터를 증가시키는데 이는 상수 시간이다.
- 따라서 TermCounter를 만드는 시간은 `페이지의 단어 개수에 비례`한다.

---

- 각 단어에 대해서 다음 동작이 이루어진다.
    - URLSet에 요소를 추가한다.
    - 레디스 TermCounter에 요소를 추가한다.
- 이들은 모두 상수 시간이므로 TermCounter를 푸시하는 총 시간은 `고유 검색어 개수에 선형`이다.

- `페이지의 단어 개수`는 `고유 검색어 개수`보다 많으므로 전체 복잡도는 `페이지의 단어 개수`에 비례한다.

---

- 이는 O(M)이므로 매우 느려진다. 따라서 매우 일반적인 단어는 인덱싱하지 않는다.
- 일반적인 검색 엔진은 불용어(stop words)라는 일반적인 단어는 인덱싱하지 않는다. [위키참조](https://en.wikipedia.org/wiki/Stop_words)


## 그래프 순회

- 일반적인 웹 크롤러(web crawler)는 다음과 같은 동작을 한다.
    - 시작 페이지를 로드하고 내용을 인덱싱한다.
    - 페이지에 있는 모든 링크를 찾고 연결된 URL들을 컬렉션에 추가한다.
    - 컬렉션을 반복하며 페이지를 로딩하고 이 페이지를 인덱싱하고 새로운 URL을 추가한다.
    - 이미 인덱싱된 URL을 찾으면 건너뛴다.

- 웹을 일종의 [그래프](https://en.wikipedia.org/wiki/Graph_(discrete_mathematics))로 생각하면
    - 각 페이지는 노드(node)고 각 링크는 한 노드에서 다른 노드로의 단방향 선이다.
    - 그래프 순회는 URL을 저장한 컬렉션에 따라서 크롤러가 수행할 순회 방식이 결정된다.
        - 선입선출(FIFO) 큐 => 너비 우선 탐색(breadth-first traversal)
        - 후입선출(LIFO) 스택 => 깊이 우선 탐색(depth-first traversal)
        - 일반적으로 컬렉션에 있는 엔트리에 우선순위를 부여할 수 있다.
        

## 실습

- WikiCrawler.java
```java
public String crawl(boolean testing) throws IOException {
    String url = queue.poll();
    if (!testing && index.isIndexed(url)) {
        return null;
    }

    Elements paragraphs = testing 
        ? wf.readWikipedia(url) 
        : wf.fetchWikipedia(url);
    index.indexPage(url, paragraphs);
    queueInternalLinks(paragraphs);
    return url;
}

void queueInternalLinks(Elements paragraphs) {
    List<String> urls = paragraphs.select("a[href]")
        .stream()
        .filter(elem -> elem.attr("href").startsWith("/wiki"))
        .map(elem -> elem.absUrl("href"))
        .collect(Collectors.toList());
    queue.addAll(urls);
}
```

- WikiCrawlerTest.java
- JedisIndex.java

---
[Home](../README.md)
