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
  ERD
  ![minisoda_erd](https://user-images.githubusercontent.com/24449555/97912134-bc343900-1d8f-11eb-838a-6507f232a1e5.jpeg)
  - 이번 프로젝트에서 사용한 데이터베이스의 스키마입니다. 깃허브에 minisoda.sql 파일에 DDL SQL을 모아두었습니다.
  스토리지 엔진은 디폴트 엔진인 InnoDB를 사용했습니다. 
  - 고려사항
    1. 정규화
      - 정규화는 partial dependency(2차)나 transitive dependency(3차)를 제거하며 테이블을 쪼개는 작업입니다. 필요할 때 조인을 통해 데이터를 합쳐 사용합니다. 
      - 일반적으로 스키마를 설계할 때 2차나 3차 등을 의식하며 테이블을 만들지는 않는 것 같습니다. 서로 '관련 있는' 필드를 한군데로 모으고 다른 테이블과 관계(relation)을 형성합니다. 프로그래밍 패러다임으로서의 OOP와 많이 달라보이지만 관계형 데이터베이스 이론은 OOP에서 파생했습니다. 물론 집합론의 영향이 더 크긴 하지만 정규화 이론을 들여다보면 자연스레 OOP가 떠오릅니다.
    2. 테이블 설명
      - member : 웹 서비스의 회원입니다. 이름을 first_name과 last_name으로 나누었는데 이번 프로젝트에서 구현하지는 않았지만 getName()이라는 getter 메서드를 두어 api에서 full_name 등과 함께 호환할 수 있습니다. JPA에서 entity로 구성할 때는 country ~ house_number는 address라는 임베디드 타입으로 만들었습니다.  
      - openapi : 은행 통합 REST API를 간략하게 나타냈습니다. 외부 서비스이므로 api로 계좌 정보를 받아와야겠지만 미니 프로젝트이므로 테이블로 표현했습니다. 
      - bankcode : 은행 코드입니다. 카테고리나 기관은 코드로 다루는 게 나은데 그렇게 하지 않으면 튜플에 '소다은행'과 '소디은행'(오타)을 다르게 보고 새로운 
    
