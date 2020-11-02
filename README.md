# minisoda
### 목차
  - 프로젝트 소개
  - 데이터베이스
  - 트랜잭션과 퍼시스턴스 컨텍스트에 대해
  - 트랜잭션 격리 수준 
  - 클래스 설명 - domain layer
  - 클래스 설명 - repository layer
  - 클래스 설명 - service layer
  - 클래스 설명 - web layer
---

## 프로젝트 소개
  - 기술 면접에 활용하고자 간단한 송금 REST API 프로젝트를 진행했습니다.
  - 지원 포지션이 JPA와 REST를 주로 활용해야 하므로 두 분야에 대해 많이 생각하고 정리하며 구성했습니다.
  - 이번 프로젝트는 데이터베이스와 JPA가 큰 주제를 이루기 때문에 개발 DB로 H2가 아닌 mariaDB를 사용했습니다.
  - REST API 구현에는 HATEOAS(Hypermedia As The Engine Of Application State)를 활용했습니다.  
  - 프로젝트 구성 
    - Spring boot 2.3.4  
    - JPA 
    - mariadb 
    - STS4
---

## 데이터베이스
