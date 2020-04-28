# 14. 영속성

## 레디스
- 자료구조는 실행 중인 자바 프로그램의 메모리에 저장되는데 프로그램이 멈추면 전부 사라진다. 이를 `휘발성(volatile)`이라 한다.
- 데이터를 생성한 프로그램이 종료되어도 지속되는 데이터를 `영속적(persistence)`라고 한다. 파일 시스템에 저장된 파일이나 데이터베이스에 저장된 데이터 등이 그러하다.

- 데이터를 영속적으로 만드는 가장 간단한 방법은 프로그램이 종료되기 전에 자료구조를 변환하여 파일에 저장하는 것이다. 그리고 프로그램이 재시작되면 다시 불러들여 재구성한다. 하지만 이 방법에는 몇 가지 문제점이 있다.
    1. 대량의 자료구조를 읽고 쓰면 느려진다.
    2. 전체 자료구조는 단일 실행 프로그램의 메모리에 다 들어가지 않는다.
    3. 프로그램이 비정상적으로 종료되면 마지막으로 실행된 이후의 데이터가 사라진다.
- 이보다 나은 대안으로 데이터베이스를 활용하는 것이다.

- Redis는 자바 자료구조와 유사한 자료구조를 제공한다.
    - 자바 List와 유사한 `문자열 리스트`
    - 자바 Map과 유사한 `해시`
    - 자바 Set과 유사한 `문자열 집합`
 
    
## 레디스 클라이언트와 서버
- 책에서는 [레디스투고(RedisToGo)](https://redistogo.com)를 이용하였으나 나는 [Docker](https://hub.docker.com/_/redis/)를 이용하여 사용하기로 하였다.

```shell
# Redis 실행 (없다면 설치 함)
$ docker run --name my-redis -p 6379:6379 -d redis

# 잘 실행되었는지 확인
$ docker ps
```

## 레디스 기반 인덱스 만들기

아래는 이번에 실습할 파일들이다.
- JedisMaker.java
- JedisIndex.java
- JedisIndexTest.java
- WikiFetcher.java

아래는 이전에 실습한 파일들이다.
- Index.java
- TermCounter.java
- WikiNodeIterable.java

## 레디스 데이터 타입
레디스는 기본적으로 String 타입의 키와 다양한 데이터 중 하나를 넣을 수 있는 맵 구조로 되어 있다.

```java
// String
jedis.set("mykey", "myvalue");
String value = jedis.get("mykey");
System.out.println("Got value: " + value);
```
- Map.put 메서드와 유사한 jedis.set을 사용하여 String 값을 넣는다.

```java
// Set
jedis.sadd("myset", "element1", "element2", "element3");
System.out.println("element2 is member: " + jedis.sismember("myset", "element2"));
```

- 자바의 Set<String>과 유사한 `set` 구조.
- Jedis.sadd를 호출하여 추가할 수 있다.
- set이 존재하지 않으면 레디스가 생성한다.
- Jedis.sismember 메서드는 요소가 존재하는지를 검사하며, 상수 시간 연산이다.

```java
// List
jedis.rpush("mylist", "element1", "element2", "element3");
System.out.println("element at index 1: " + jedis.lindex("mylist", 1));
```

- 자바의 List<String>과 유사한 `list` 구조.
- Jedis.rpush 메서드는 list의 오른쪽 끝에 요소를 추가한다.
- list 역시 존재하지 않으면 레디스가 생성한다.
- Jedis.lindex 메서드는 정수 인덱스를 받아 지정된 요소를 반환하며, 상수 시간 연산이다.


```java
// Hash
jedis.hset("myhash", "word1", Integer.toString(2));
jedis.hincrBy("myhash", "word2", 1);
System.out.println("frequency of word1: " + jedis.hget("myhash", "word1"));
System.out.println("frequency of word2: " + jedis.hget("myhash", "word2"));
```

- 자바의 Map<String, String>과 유사한 `hash` 구조.
- Jedis.hset 메서드는 hash에 새로운 엔트리를 추가한다.
- String 값을 저장하므로 Jedis.hget을 호출하였다면 형변환해야 한다.
- `hash` 구조의 두 번째 키를 `필드`라고 부른다.
- Jedis.hincryby 메서드 같은 특별한 메서드도 있어서 필드의 값을 정수로 취급하여 늘려준다.
- `hash`에 엔트리를 넣고 가져오고 증가시키는 작업은 모두 상수 시간 연산이다.

---
[Home](../README.md)
