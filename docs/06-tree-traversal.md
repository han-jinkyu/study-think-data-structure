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

---
[Home](../README.md)
