# 7. 철학으로 가는 길

## 목표
'철학으로 가는 길' 추측을 테스트하는 웹 크롤러를 개발한다.

- WikiNodeExample.java
    - DOM 트리에서 재귀적 방법과 반복적 방법으로 DFS를 구현한 코드를 담고 있다.
- WikiNodeIterable.java
    - DOM 트리를 탐색하는 Iterable 클래스를 포함한다.
- WikiFetcher.java
    - jsoup 라이브러리를 활용하여 위키피디아 페이지를 다운로드하는 유틸리티 클래스.
- WikiPhilosophy.java
    - 이번 예제에서 작성할 코드의 개요를 담고 있다.
    
## Iterable과 Iterator
재귀적 DFS와 비교할 때 반복적 DFS의 이점은 Iterator 객체로 래핑하기 좋다는 것이다.
WikiNodeIterable.java를 살펴보면 WikiNodeIterable은 Iterable<Node> 인터페이스를 구현하고 있다. 따라서 다음과 같이 반복문에 사용할 수 있다.

```java
Node root = ...
Iterable<Node> iter = new WikiNodeIterable(root);
for (Node node : iter) {
    visit(node);
}
```

WikiNodeIterable 클래스의 구현은 전통적인 공식을 따른다.

1. 생성자는 루트 노드에 대한 참조를 인자로 받아 저장한다.
2. iterator 메서드는 Iterator 객체를 생성하여 반환한다.

```java
public class WikiNodeIterable implements Iterable<Node> {
    private Node root;
    
    public WikiNodeIterable(Node root) {
        this.root = root;
    }

    @Override
    public Iterator<Node> iterator()  {
        return new WikiNodeIterator(root);
    }
}
```

내부 클래스인 WikiNodeIterator가 실제 모든 작업을 수행한다.

```java
private class WikiNodeIterator implements Iterator<Node> {
    Deque<Node> stack;

    public WikiNodeIterator(Node node) {
        stack = new ArrayDeque<Node>();
        stack.push(root);
    }

    @Override
    public boolean hasNext() {
        return !stack.isEmpty();
    }

    @Override
    public Node next() {
        if (stack.isEmpty()) {
            throw new NoSuchElementException();
        }

        Node node = stack.pop();
        List<Node> nodes = new ArrayList<Node>(node.childNodes());
        Collections.reverse(nodes);
        for (Node child: nodes) {
            stack.push(child);
        }
        return node;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
``` 

이 클래스는 메서드 3개로 나뉘게 된다.

1. 생성자는 스택(ArrayDeque)을 초기화하고 그 안에 루트 노드를 추가한다.
2. isEmpty 메서드는 스택이 비었는지 확인힌다.
3. next 메서드는 스택에서 다음 노드를 pop하고 자식 노드들은 역순으로 스택에 push한 후 pop한 node를 반환한다. 누군가 빈 Iterator에서 next 메서드를 호출하면 예외를 던진다.

## WikiFetcher
WikiFetcher 클래스는 다음과 같은 역할을 합니다.

1. 위키피디아의 페이지를 다운로드하여 HTML을 파싱하고 본문을 선택하는 코드를 캡슐화한다.
2. 너무 많은 페이지를 다운로드함으로써 서버의 서비스 약관을 위반하지 않기 위해 요청 사이의 시간을 측정하고 충분한 시간이 확보되지 않는다면 적절한 시간동안 동작을 지연(sleep)한다. 기본값은 1초.

```java
public class WikiFetcher {
	private long lastRequestTime = -1;
	private long minInterval = 1000;

	public Elements fetchWikipedia(String url) throws IOException {
		sleepIfNeeded();

		Connection conn = Jsoup.connect(url);
		Document doc = conn.get();
		Element content = doc.getElementById("mw-content-text");
		Elements paras = content.select("p");
		return paras;
	}

	private void sleepIfNeeded() {
		if (lastRequestTime != -1) {
			long currentTime = System.currentTimeMillis();
			long nextRequestTime = lastRequestTime + minInterval;
			if (currentTime < nextRequestTime) {
				try {
					Thread.sleep(nextRequestTime - currentTime);
				} catch (InterruptedException e) {
					System.err.println("Warning: sleep interrupted in fetchWikipedia.");
				}
			}
		}
		lastRequestTime = System.currentTimeMillis();
	}
}
``` 

- fetchWikipedia는 URL을 인자로 받아 페이지 본문의 각 단락을 위한 DOM 객체를 담은 Elements를 반환한다.
- sleepIfNeeded는 바로 앞 요청과의 시간 간격을 검사하여 minInterval 미만이면 동작을 지연한다.

```java
WikiFetcher wf = new WikiFetcher();

for (String url : urlList) {
    Elements paragraphs = wf.fetchWikipedia(url);
    processParagraphs(paragraphs);
}
```

- WikiFetcher는 하나만 생성하고 이것으로 모든 요청을 처리해야만 지연을 강제할 수 있다.
- 누군가 오용할 가능성이 있기 때문에 싱글턴으로 만드는 것이 더 좋을 수 있다.

## 실습
- WikiPhilosophy.java

위 파일에 존재하는 WikiPhilosophy 클래스는 다음과 같은 순서로 페이지를 크롤링하고 '철학' 페이지를 찾아간다.
 
1. URL을 가져와서 페이지를 다운로드하고 파싱한다.
2. 결과 DOM 트리를 탐색하여 첫번째 '유효한' 노드를 찾는다.
    1. 링크는 사이드바 또는 박스아웃이 아닌 페이지 본문에서 찾는다.
    2. 링크는 이탤릭체(<i>, <em>)나 괄호 안에 없어야 한다.
    3. 외부 링크와 현재 페이지에 대한 링크, 레드 링크는 건너뛴다.
    4. 일부 버전에서 텍스트가 대문자로 시작한다면 링크는 건너뛴다.
3. 페이지에 링크가 없거나 이미 본 페이지라면 실패를 표시하고 종료한다.
4. 링크가 위키피디아의 철학 페이지와 일치하면 종료한다.
5. 그렇지 않으면 1로 돌아간다.

---
[Home](../README.md)
