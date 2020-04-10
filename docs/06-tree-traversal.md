# 6. 트리 순회

## 검색 엔진
검색 엔진의 필수 요소는 다음과 같다

1. 크롤링(Crawling)
    - 웹 페이지를 다운로드, 파싱하고 링크 추출하는 프로그램
1. 인덱싱(Indexing)
    - 검색어를 조회하고 해당 검색어를 포함하는 페이지를 찾는 데 필요한 자료구조
1. 검색(Retrieval)
    - 인덱스에서 결과를 수집, 검색어와 가장 관련된 페이지를 식별하는 방법
    
## HTML 파싱하기
HTML의 최소한의 형태는 다음과 같다.

```html
<!DOCTYPE html>
<html>
    <head>
        <title>This is a title</title>
    </head>
    <body>
        <p>Hello world!</p>
    </body>
</html>
```

HTML 파싱의 결과는 본문과 태그 같은 문서 요소를 담고 있는 문서 객체 모델(Document Object Model; DOM) 트리다. 이 트리는 노드로 이루어진 자료 구조로 각 노드는 텍스트와 태그, 다른 문서 요소를 나타낸다.

노드 간의 관계는 문서 구조로 결정한다. 루트 노드는 `<html>`이며 그 안에는 `<head>`와 `<body>`에 대한 링크가 담겨 있다. 이 두 노드는 루트 노드의 자식(Children) 노드가 된다. 

각 노드는 자식 노드에 대한 링크, 부모(Parent) 노드에 댛나 링크를 포함하고 있어서 트리를 위, 아래로 탐색할 수 있다.

## jsoup 사용하기
jsoup 라이브러리를 사용하면 웹 페이지를 다운로드하고 파싱하고 DOM 트리를 탐색하기 용이하다. 
예제 WikiNodeExample.java를 참고한다.

```java
String url = "https://en.wikipedia.org/wiki/Java_(programming_language)";
		
// download and parse the document
Connection conn = Jsoup.connect(url);
Document doc = conn.get();

// select the content text and pull out the paragraphs.
Element content = doc.getElementById("mw-content-text");
Elements paras = content.select("p");
```

- Document 객체는 트리를 탐색하고 노드를 선택하는 메서드를 제공한다
- getElementById는 String을 인자로 받아 id 속성을 갖는 요소를 트리에서 찾고 Element를 반환한다
- select는 String 인자를 받아 일치하는 태그를 가진 요소를 모두 반환하며 Elements 형태로 반환한다
- Node 클래스는 DOM에서의 노드를 뜻하며 하위 클래스로 Element, TextNode, DataNode, Comment 등이 존재한다
- Elements 클래스는 ArrayList를 확장한다

## DOM 트리 반복하기
DOM 트리의 노드를 반복하는 클래스 WikiNodeIterable을 참고한다. 이 클래스를 사용하는 법은 WikiNodeExample.java를 참고한다.

```java
Elements paras = content.select("p");
Element firstPara = paras.get(0);

Iterable<Node> iter = new WikiNodeIterable(firstPara);
for (Node node: iter) {
    if (node instanceof TextNode) {
        System.out.print(node);
    }
}
```

위는 텍스트 노드만 출력하게 하고 다른 노드(예를 들면 Element)는 무시하므로 결과는 텍스트만 출력하게 된다.

## 깊이 우선 탐색
트리를 탐색하는 방법 중 하나인 깊이 우선 탐색(Depth-First Search; DFS)에 대하 알아본다.

- 트리의 루트에서 시작하여 첫 번째 자식 노드를 탐색한다. 
- 자식이 자식을 가지고 있다면 다시 첫 번째 자식을 탐색한다. 
- 자식이 없는 노드에 도달하면 부모 노드를 거슬러 올라가 그 다음 두 번째 자식을 탐색한다. 
    - 만약 자식이 없으면 다시 부모로 거슬러 올라간다. 
- 루트의 마지막 노드까지 탐색하면 종료한다.

DFS 구현방법은 `재귀적 방법`과 `반복적 방법`이 존재한다.

```java
private static void recursiveDFS(Node node) {
    if (node instanceof TextNode) {
        System.out.print(node);
    }
    for (Node child: node.childNodes()) {
        recursiveDFS(child);
    }
}
```
재귀적 방법은 구현이 간단하다. 루트부터 시작하여 자신이 TextNode라면 출력하고 자식이 존재한다면 자식을 순서대로 각각 recursiveDFS를 호출하게 하면 된다.

자식 노드를 탐색하기 전에 내용을 출력하므로 전위 순회(Pre-order)에 해당한다. [Wiki참조](https://ko.wikipedia.org/wiki/트리_순회)

재귀적 호출을 하면 recursiveDFS 메서드는 `호출 스택(Callstack)`을 사용하여 자식 노드를 추적하고 올바른 순서로 자식 노드를 처리한다. 
따라서 대안으로 `스택(Stack)` 자료구조를 사용하여 노드를 추적할 수도 있다.

## 스택
리스트와 유사한 자료구조로, 요소의 순서를 유지하는 컬렉션이다. 일반적으로 스택이 제공하는 메서드는 다음과 같다. 

- push : 스택의 최상단에 요소를 추가
- pop : 스택의 최상단에 있는 요소를 제거하고 반환한다
- peek : 최상단의 요소를 반환하지만 스택을 수정하지는 않는다
- isEmpty : 스택이 비어 있는지 알려준다

pop 메서드는 항상 최상단의 요소를 반환하므로 후입선출(Last In, First Out; LIFO)로도 불린다. 반면 큐(Queue)는 입력한 순서대로 요소를 반환하므로 선입선출(First In, First Out; FIFO)로 불린다.

자바로 스택을 구현하는 데는 3가지 선택사항이 존재한다.

1. 기존 ArrayList나 LinkedList 클래스를 사용한다. ArrayList를 사용한다면 요소의 맨 끝에 넣고 제거해야 한다. 이 작업은 상수 시간이기 때문이다.
1. Stack 클래스를 사용하여 스택 메서드의 표준 구현을 사용한다. 하지만 이 클래스는 오래된 자바 버전이므로 이후에 나온 JCF와 일치하지 않는다.
1. 가장 좋은 방법인 ArrayDeque 클래스 같은 Deque 인터페이스를 구현한 클래스를 사용한다.

`Deque(Double Ended Queue; 발음 Deck)`는 '양쪽에 끝이 있는 큐'다. Deque 인터페이스는 push, pop, peek, isEmpty 메서드를 제공하므로 `Deque을 스택으로 사용`할 수 있다.

## 반복적 DFS
다음은 ArrayDeque을 사용하여 Node 객체의 스택을 표현하는 반복적 DFS다. (WikiNodeExample.java)

```java
private static void iterativeDFS(Node root) {
    Deque<Node> stack = new ArrayDeque<Node>();
    stack.push(root);

    // if the stack is empty, we're done
    while (!stack.isEmpty()) {

        // otherwise pop the next Node off the stack
        Node node = stack.pop();
        if (node instanceof TextNode) {
            System.out.print(node);
        }

        // push the children onto the stack in reverse order
        List<Node> nodes = new ArrayList<Node>(node.childNodes());
        Collections.reverse(nodes);
        
        for (Node child: nodes) {
            stack.push(child);
        }
    }
}
```

- 반복적 DFS의 장점은 자바 Iterator로 구현하기 쉽다는 점이다.
- Deque 인터페이스는 ArrayDeque 말고도 다른 클래스를 제공한다.
    - LinkedList는 List와 Deque 인터페이스를 둘 다 구현한다.
    
---
[Home](../README.md)
