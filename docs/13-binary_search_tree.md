# 13. 이진 탐색 트리

## 단순한 MyTreeMap 클래스

```java
private Node findNode(Object target) {
    if (target == null) {
        throw new IllegalArgumentException();
    }

    @SuppressWarnings("unchecked")
    Comparable<? super K> k = (Comparable<? super K>) target;

    // the actual search
    Node node = root;
    while (node != null) {
        int cmp = k.compareTo(node.key);
        if (cmp < 0)
            node = node.left;
        else if (cmp > 0)
            node = node.right;
        else
            return node;
    }
    return null;
}
```

- findNode 메서드는 containsKey와 get 메서드에서 호출하는 private 메서드로, Map 인터페이스에는 속하지 않는다
    - findNode에서 target은 null은 유효한 키 값이 아니다
- compareTo 메서드를 호출하기 전에 target 인자를 형변환하여 Comparable 객체로 만들어야 한다.

## 값 탐색하기

```java
@Override
public boolean containsValue(Object target) {
    return containsValueHelper(root, target);
}

private boolean containsValueHelper(Node node, Object target) {
    if (node == null) {
        return false;
    }
    if (equals(target, node.value)) {
        return true;
    }
    if (containsValueHelper(node.left, target)) {
        return true;
    }
    if (containsValueHelper(node.right, target)) {
        return true;
    }
    return false;
}
```

- containsValue 메서드는 값을 검색해야 하므로 전체 트리를 검색해야 한다. 이는 재귀적으로 구현할 수 있다.
    - 첫 번째 if문은 재귀의 기저 사례(base case)를 검사한다. node가 null이면 대상을 찾지 못하고 트리의 바닥에 다다른 것이므로 false를 반환한다. 두 트리 가운데 한 쪽 트리에 해당하는 얘기며, 다른 한 쪽은 가능성이 있다.
    - 두 번째 if문은 원하는 것을 찾았는지 확인한다.
    - 세 번쨰 if문은 왼쪽 하위 트리에서 target을 찾는 재귀적인 호출을 한다.
    - 네 번째 if문은 오른쪽 하위 트리에서 target을 찾는 재귀적은 호출을 한다.
- 모든 노드를 방문하므로 실행시간은 노드에 반비례한다.

## put 메서드 구현하기

```java
@Override
public V put(K key, V value) {
    if (key == null) {
        throw new NullPointerException();
    }
    if (root == null) {
        root = new Node(key, value);
        size++;
        return null;
    }
    return putHelper(root, key, value);
}

private V putHelper(Node node, K key, V value) {
    @SuppressWarnings("unchecked")
    Comparable<? super K> k = (Comparable<? super K>) key;
    int cmp = k.compareTo(node.key);
    
    if (cmp < 0) {
        if (node.left == null) {
            node.left = new Node(key, value);
            size++;
            return null;
        } else {
            return putHelper(node.left, key, value);
        }
    }
    if (cmp > 0) {
        if (node.right == null) {
            node.right = new Node(key, value);
            size++;
            return null;
        } else {
            return putHelper(node.right, key, value);
        }
    }
    V oldValue = node.value;
    node.value = value;
    return oldValue;
}
``` 

- put 메서드는 다음 두 가지를 처리해야 한다.
    - 주어진 키가 트리에 이미 있으면 값을 대체하고 기존 값을 반환한다.
    - 주어진 키가 트리에 없으면 올바른 위치에 새로운 노드를 추가해야 한다.
- get 메서드처럼 compareTo 메서드를 호출하여 어느 트리 경로를 따라가야 할지 확인한다.
    - cmp < 0이면 주어진 키가 작으므로 왼쪽으로 가야 한다.
        - 왼쪽 하위 트리가 비었다면 바닥에 이른 것이므로 새로운 노드를 생성하여 추가한다.
        - 왼쪽 하위 트리가 비어 있지 않다면 검색을 위해 재귀 호출을 한다.
    - cmp > 0이면 주어진 키가 크므로 오른쪽으로 가야 한다. 

---
[Home](../README.md)
