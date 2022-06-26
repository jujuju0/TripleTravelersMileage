# Triple Back-end 사전 과제

트리플 여행자 클럽 마일리지 서비스 개발

## 기술스택
* JAVA 17

* SPRING BOOT 2.7.1

* GRADLE 7.4.1

* MySQL 8.0.29

* JPA


## 실행방법

#### **jar 파일 경로**

\TripleTravelersMileage\build\libs\TripleTravelersMileage-0.0.1-SNAPSHOT.jar

```bash
cd {target}

java -jar TripleTravelersMileage-0.0.1-SNAPSHOT.jar
```

## API 명세

```text
1. REVIEW EVENT
   [API ID] http://localhost:8080/events
   [METHOD] POST
   [DETAIL] 리뷰 추가, 수정, 삭제 API

2. USER TOTAL POINT 조회
   [API ID] http://localhost:8080/point/{userId}
   [METHOD] GET
   [DETAIL] 특정 유저의 현재 누적 포인트 조회 API

3. USER POINT 상세 히스토리 조회
   [API ID] http://localhost:8080/point/detail/{userId}
   [METHOD] GET
   [DETAIL] 특정 유저의 포인트 상세 내역 조회 API
```

## DB 자료 경로
* #### 테이블 생성 DDL 및 테스트 데이터

    \TripleTravelersMileage\src\main\resources\DB\TripleDB.sql

* #### E-R Diagram

    \TripleTravelersMileage\src\main\resources\DB\TripleERD.PNG