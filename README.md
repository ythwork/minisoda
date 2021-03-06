# minisoda
### 목차
  - 프로젝트 소개
  - 로그인 
  - 데이터베이스
  - 도메인 계층
  - 리포지토리 계층
  - 서비스 계층
  - 웹 계층
---

## 프로젝트 소개
  - 간단한 송금 REST API 프로젝트를 진행했습니다.
  - 로그인 구현에는 Spring Security와 jjwt를 사용했습니다. 
  - REST API 구현에는 HATEOAS(Hypermedia As The Engine Of Application State)를 활용했습니다.  
  - 로그인부터 시작해 데이터베이스 설계, 도메인, 리포지터리, 서비스, 웹 계층에 걸쳐 구현하며 고민했던 점들에 대해 설명하겠습니다.
  
  - 프로젝트 구성 
    - Spring boot 2.3.4  
    - Spring Security
    - jjwt
    - JPA 
    - mariadb 
    - STS4
---

## 데이터베이스
  ERD
  ![minisoda_erd](https://user-images.githubusercontent.com/24449555/98623009-2760ab00-234e-11eb-90fd-df3deb921cfe.png)
  - 이번 프로젝트에서 사용한 데이터베이스의 스키마입니다. 깃허브에 minisoda.sql 파일에 DDL SQL을 모아두었습니다.
  스토리지 엔진은 디폴트 엔진인 InnoDB를 사용했습니다. 그 이유는 다음과 같습니다. 인덱스는 일반적으로 B+ 트리를 이용해 튜플에 대한 참조를 정렬합니다. B+ 트리는 메모리가 아니라 하드디스크에 상주하는 자료구조입니다. 하드디스크에서 데이터를 가져오려면 수백만 클럭이 소요되는데 이는 메모리에 비하면 매우 느립니다. 이에 BST의 장점은 가져오면서 하드디스크에 대한 접근 횟수를 최대한 줄여보고자 만들어졌습니다. BST의 장점은 자료의 삽입, 삭제, 탐색을 모두 O(log n)에 할 수 있다는 점입니다. B + 트리의 전 모델이라고 할 수 있는 B 트리는 BST의 노드가 키 값을 하나만 가지는데 반해 한 노드에 키가 여러개 있습니다. 이렇게 노드에 키를 많이 저장함으로써 접근 횟수를 줄일 수 있었습니다. B+ 트리는 이에 더해 리프 노드가 링크드 리스트로 연결되어 있습니다. 예를 들어 SELECT ... WHERE date BETWEEN t1 AND t2 라는 쿼리를 실행하고 이때 date에 인덱스가 있다면 t1에 대해 탐색을 한번하고 t2에 대해 탐색을 한번 하면 리프 노드에서 그 구간 사이의 모든 키 값을 탐색할 수 있습니다. InnoDB 엔진는 리프노드가 튜플에 대한 참조가 아니라 실제 튜플이라는 특징이 있습니다. PK는 자연스럽게 clustered key가 되어 pk를 where 절에서 검색 조건으로 쓰면 매우 빠른 특징이 있습니다. JPA는 composite key의 경우 surrogate key를 권장하는데 컨트롤러 메스드에서 @PathVariable로 id를 넘겨받아 service에서 튜플을 탐색하면 pk에 대한 탐색이 되므로 매우 빠르게 탐색할 수 있습니다. 제 개인적인 의견으로는 jpa의 pk전략과 InnoDB의 클러스터드 인덱스 정책은 매우 잘 어울린다고 생각합니다.    

  - 고려사항
    1. 정규화
      - 정규화는 partial dependency(2차)나 transitive dependency(3차)를 제거하며 테이블을 쪼개는 작업입니다. 필요할 때 조인을 통해 데이터를 합쳐 사용합니다. 
      - 일반적으로 스키마를 설계할 때 2차나 3차 등을 의식하며 테이블을 만들지는 않는 것 같습니다. 서로 '관련 있는' 필드를 한군데로 모으고 다른 테이블과 관계(relation)을 형성합니다. 프로그래밍 패러다임으로서의 OOP와 많이 달라보이지만 관계형 데이터베이스 이론은 OOP에서 파생했습니다. 물론 집합론의 영향이 더 크긴 하지만 정규화 이론을 들여다보면 자연스레 OOP가 떠오릅니다.
      
    2. 테이블 설명
      - member : 웹 서비스의 회원입니다. 이름을 first_name과 last_name으로 나누었는데 이번 프로젝트에서 구현하지는 않았지만 getName()이라는 getter 메서드를 두어 api에서 full_name 등과 함께 호환할 수 있습니다. JPA에서 entity로 구성할 때는 country ~ house_number는 address라는 임베디드 타입으로 만들었습니다. 이메일 컬럼에 인덱스를 만들었는데 이 인덱스는 중복 회원을 거르는데 사용합니다. 유저 인증에 사용하기 위해 username과 password 컬럼을 구성했습니다. password는 bcrypt로 암호화되어 저장됩니다.
      - role : ROLE_USER와 ROLE_ADMIN 등 인가(Authorization)에 필요한 권한 테이블입니다.
      - member_role : member와 role이 M:N 관계이므로 이를 나타내기 위해 구현된 테이블입니다. JPA에서는 @ManyToMany로 따로 엔티티 클래스를 구현하지는 않았습니다.
      - openapi : 은행 통합 REST API를 간략하게 나타냈습니다. 외부 서비스이므로 api로 계좌 정보를 받아와야겠지만 미니 프로젝트이므로 테이블로 표현했습니다. 
      - bankcode : 은행 코드입니다. 카테고리나 기관은 코드로 다루는 게 나은데 그렇게 하지 않으면 튜플에 '소다은행'과 '소디은행'(오타)을 다르게 보고 만약 pk라면 새로운 키를 생성할 수도 있으므로 안전하게 코드 테이블을 만드는 것이 좋습니다. 
      - account : 사용자가 웹서비스에 등록한 계좌 정보입니다. 이 계좌를 통해 송금 서비스를 이용할 수 있습니다.
      - transaction : 거래 목록을 나타냅니다. account에 등록된 계좌를 이용해 송금을 할 수 있습니다. 잔고 등은 사용자가 다른 서비스나 은행 업무 등을 통해 변동될 수 있으므로 송금할 때마다 openapi에서 받아와 사용합니다. 이때 중요한 점이 트랜잭션의 격리 수준입니다. 일반적인 웹서비스는 데이터의 일관성보다 동시성을 중요하게 여기기 때문에 READ COMMITTED 수준에 비즈니스 특성에 따라 REPEATABLE READ를 사용합니다. 스프링의 경우 @version을 통해 애플리케이션 수준에서 이를 구현할 수 있습니다. 송금 서비스의 경우 송금할 때 READ COMMITTED은 위험할 것 같습니다. REPEATABLE READ에 더해 쓰기 잠금이 필요합니다. JPA에서는 비관적 락 PESSIMISTIC_WRITE 모드를 사용합니다. 내부적으로는 SELECT ... FOR UPDATE를 이용해 row level에서 쓰기 잠금을 합니다. 
      
    3. 송금할 때 트랜잭션 격리 수준과 잠금
      - 최근 DB는 대부분의 경우 락이 아니라 MVCC(multi-version concurrency control)로 REPEATABLE READ를 구현하여 우수한 동시성을 보장합니다. 락이라면 매우 많은 대기 시간이 발생하겠지만 MVCC는 non-blocking에 가깝습니다. 테이블에 접근하면 바로 요청한 row를 받아볼 수 있습니다. 구현 방식에 차이는 있지만 제가 사용한 InnoDB 엔진의 경우 rollback segment를 이용합니다. 그러므로 실제 테이블에는 항상 최신의 데이터가 있습니다. 누군가 테이블에 최신 데이터를 업데이트한다고 해도 현재 트랜잭션 내에서 조회했던 데이터가 변경되지는 않습니다. 내가 가진 SCN보다 최신의 데이터는 포함하지 않고 rollback segment에서 내 SCN보다 작은 가장 최신의 데이터를 가져와 튜플에 추가합니다. 이제 REPEATABLE READ를 트랜잭션 격리 수준으로 선택한다고 해도 성능상의 이슈는 없을 것 같습니다. 
 
      - 처음 송금을 고려했을 때는 격리수준을 SERIALIZABLE로 해서 모든 로우에 대해 읽기 잠금을 걸어 데이터의 완벽한 동일성을 추구해야 하지 않을까 생각했습니다. SELECT ... LOCK IN SHARE MODE가 걸리면 다른 커넥션에서 읽기 잠금에 대해서는 함께 공유하지만 쓰기 잠금(UPDATE 구문도 포함)의 경우 읽기 잠금이 풀릴 때까지 기다립니다. 하지만 동시성에는 치명적인 약점이 되므로 실제 운영에 적용할 수는 없을 것 같습니다. 데이터의 동일성과 커넥션의 동시성을 동시에 만족할 수는 없으므로 적당한 절충안을 찾아내야 합니다. 제 의견으로는 일단 REPEATABLE READ 수준에서 송금할 때는 튜플 단위로 쓰기 잠금을 걸어야 하지 않을까 생각했습니다. 이때는 SELECT ... FOR UPDATE를 사용해 로우 수준에서 쓰기 잠금을 거는 것이므로 JPA의 비관적 락을 사용하면 괜찮을 것 같습니다. 단순히 UPDATE 구문에서 사용하는 락을 쓴다면 조금 비효율적일 수 있습니다. UPDATE 구문의 경우 WHERE 절에 인덱스를 가진 컬럼이 있다면 이 인덱스에서 참조한 모든 로우를 잠급니다. WHERE 절에 두번째 조건이 있다면 실제 일치 범위는 잠긴 로우보다 적게 됩니다. 즉, 불필요하게 잠긴 로우가 발생하게 됩니다.
      
      - 이번 프로젝트의 경우 비관적 락을 사용해볼까 하고 고민하다가 실제 로직을 살펴보니 송금할 때 여러 로우를 탐색하지 않고 openapi에서 두 개의 계좌 정보만 가져오기 때문에 UPDATE를 사용해도 불필요하게 잠기는 로우는 생기지 않을 것으로 판단해 JPA의 dirty checking 기능을 그대로 사용하기로 했습니다. dirty checking은 1차 캐시에 있는 스냅샷을 이용해 트랜잭션을 커밋할 때 update query를 만들어 동기화 하는 기법입니다.
---

## 로그인
  com.ythwork.soda.security
  - 고려사항
    1. AuthenticationTokenFilter는 인증에 사용할 필터입니다. 클라이언트가 요청을 보내면 'Authorization: Bearer [JWT-TOKEN]' 헤더가 있습니다. jwtManager로 먼저 인증(Authentication)을 하고 인증을 통과하면 Member 객체를 Authentication 객체에 담아둡니다.이후에 컨트롤러 메서드에서 @AuthenticationPrincipal 애너테이션으로 받아서 사용합니다. 
    2. Spring Security를 사용하기 위해서는 UserDetails와 UserDetailsService를 구현해야 합니다. Member 객체에 Auth 객체를 참조할 auth 필드를 두고 UserDetails 인터페이스를 구현합니다. Auth 객체는 username과 password 그리고 roles 를 가지고 있습니다. roles는 이후에 SimpleGrantedAuthority 객체로 변환해 authorities로 반환됩니다.
    3. JwtManager는 jjwt를 이용해 구현했습니다. jwt 토큰을 생성 및 인증하는 기능을 제공합니다. 클라이언트가 로그인을 요청하면 MemberController가 jwtManager를 통해 토큰을 발급합니다. 이 토큰이 JWT(JSON web token)입니다. JWT은 header.payload.signature로 구성된 토큰입니다. payload에는 키와 값으로 구성된 claim을 넣을 수 있습니다. 어떤 데이터는 상관없이 넣을 수 있습니다. 이는 매우 유용하지만 보안상의 약점이 될지도 모르겠습니다. 이후 클라이언트는 로그인 상태가 필요한 엔드포인트에 접속할 때마다 Authorization 헤더에 JWT 값을 추가해 요청해야 합니다. 
    4. 클라이언트가 요청한 리퀘스트는 필터 체인을 거치게 되는데 이때 UserPasswordAuthenticationFilter 전에 AuthenticationTokenFilter를 설치해 인증을 완료합니다. 이때 SecurityContextHolder 에 있는 컨텍스트에 Authentication 객체를 만들어 넣어둡니다. 이렇게 하면 애플리케이션 전체에서 SecurityContextHolder.getContext().getAuthentication()을 통해 Authentication 객체를 받아올 수 있습니다. 
    5. SecurityConfiguration은 스프링 시큐리티를 사용하기 위한 설정입니다. 가장 중요한 점은 configure(WebSecurity)와 configure(HttpSecurity)를 오버라이딩하는 것입니다. "member/login"이나 "member/register" 같은 경우 인증을 거치지 않아도 모든 사용자가 접속 가능해야 합니다. 많이 오해하는 부분이 이렇게 모든 사용자가 접속해야 하는 엔드 포인트들에 대해 HttpSecurity를 설정할 때 http.authorizeRequests().antMatchers(...).permitAll()을 하면 AuthenticationTokenFilter가 비활성화되어 작동하지 않을 것이라는 생각입니다. 하지만 AuthenticationTokenFilter는 인증 필터이고 permitAll()은 인증된 사용자들에게 모든 권한을 주겠다는 것으로 권한 부여(authorization)에 대한 기능을 제공하지 인증(authentication)과는 전혀 상관없습니다. 그러므로 인증 필터를 사용하지 않기 위해서는 WebSecurity를 설정하면서 web.ignoring().antMachers()를 사용해야 합니다. 처음에 이 부분을 간과하여 모든 요청에 대해 인증 필터를 거치고 JWT가 없다는 익셉션에 발생하여 해결하느라 애를 먹었습니다.

---

## 도메인
  com.ythwork.soda.domain
  - 고려사항
    1. pk에 @GeneratedValue의 경우 처음엔 AUTO를 설정해 데이터베이스 의존적이지 않게 하려 했습니다. 하지만 얼마 지나지 않아 JPA가 nextval을 통해 sequence를 얻으려해 에러가 발생하는 것을 발견하고 MariaDB에 맞게 IDENTITY로 변경했습니다. 
    2. member 엔티티의 경우 주소와 관련된 부분을 임베디드 타입인 address로 만들어 사용했습니다. 
    3. 연관 관계 매핑의 경우 대부분 @ManyToOne이 있는 엔티티의 setter 메서드에서 컬렉션에 엔티티를 추가하는 형태로 구현했습니다.
    4. @OneToMany 등이 있는 컬럼의 경우 fetch 타입을 모두 LAZY로 설정해 프록시를 통한 지연 로딩을 사용하도록 했습니다. @OneToMany의 경우 명시하지 않으면 디폴트가 지연 로딩입니다. 추후 로직을 살펴보며 필요한 시점이 비슷한 경우 fetch join을 통해 접근 횟수를 한번으로 줄일 수 있는 EAGER로 변경할 것입니다.  
    5. Openapi에서는 잔고를 나타내는 balance의 데이터 타입 경계에 대해 잠시 고민했습니다. 통장 잔고가 음수일 수는 없으니 데이터베이스에는 BIGINT UNSIGNED를 설정했습니다. 자바의 경우 이전에는 아예 unsigned가 없었고 최근에는 있다고는 들었지만 사용을 권장하지는 않습니다. 그럼 Long의 상한 경계인 9223372036854775807보다 큰 값이 들어오면 데이터 손실이 발생합니다. 데이터베이스는 unsigned이므로 상한 경계가 18446744073709551615입니다. 하지만 현실을 곰곰히 생각해보니 Long의 상한 경계에 달하는 금액을 송금하는 경우는 없을 것으로 판단해 Long을 그대로 사용하기로 했습니다. 
---

## 리포지토리
  com.ythwork.soda.data
  - 고려사항
    1. 리포지토리 계층은 스프링 데이터 JPA에서 너무 많은 일을 해주므로 많은 고민을 하지 않았습니다. JpaRepository 인터페이스를 사용했고 데이터베이스에 만들어둔 인덱스를 사용하기 위해 몇 개의 쿼리 메서드를 선언했습니다. 예를 들어 OpenapiRepository에는 findByBankcodeAndAccountNumber(Bankcode bankcode, String accountNumber) 쿼리 메서드가 자동으로 JPQL을 생성해주는데 데이터베이스에 ALTER TABLE openapi ADD INDEX ix_bankcode_acnt_num (bankcode_id, account_number); 로 미리 인덱스를 만들어 두어 훨씬 빠르게 탐색할 수 있도록 했습니다.
    2. 스프링이 많은 부분을 담당해준다고 해도 JPA의 작동 방식에 대해서는 어느 정도 알고 있어야 할 것 같습니다. 자동화는 생산성을 증대시키지만 에러가 발생했을 때 대응하는 시간을 늘려 개발자들을 곤란하게 만들기 때문입니다. 정확히 알고 있어야 할 부분은 다음과 같습니다. 
      - 퍼시스턴스 컨텍스트의 개념
      - 1차 캐시의 역할과 스냅샵의 의미 
      - 쓰기 지연 SQL 저장소
      - dirty checking 작동 방식
    3. JPA를 처음 공부하면서 마이바티스로 동적 쿼리 만들면서 SQL을 사용하지 왜 한 계층을 더 두어 추상화하려는 걸까라고 생각한 적이 있습니다. 하지만 이제 JPA의 사용은 필수인 것 같습니다. 다양한 이점이 있겠지만 제 생각에 가장 큰 장점은 데이터베이스에 대한 의존성 제거와 1차 캐시를 통해 데이터베이스 접근 횟수를 줄임으로써 성능을 개선했다는 것입니다. 
---

## 서비스
  com.ythwork.soda.service
  - 고려사항
    1. 서비스 계층에서는 @Transactional에 대한 이해가 가장 중요하다고 생각합니다. 특히 스프링 컨테이너는 트랜잭션이 시작되면 퍼시스턴스 컨텍스트를 생성하고 트랜잭션이 끝나면 퍼시스턴스 컨텍스트를 삭제합니다. 이러한 지식이 없다면 JPQL이나 네이티브 SQL을 사용해 커스터마이징할 때 많은 혼란에 빠질 것 같습니다. 스프링 컨테이너가 관리하는 트랜잭션의 몇가지 특징은 다음과 같습니다.
      - 웹 서비스의 기본적인 격리 수준은 READ COMMITTED으로 판단합니다. 이에 더해 @Version을 이용해 낙관적 락을 구현하거나 비관적 락을 통해 데이터베이스의 쓰기 락을 제공합니다.
      - 기본적으로 트랜잭션 도중 다시 트랜잭션을 시작하면 부모의 트랜잭션에 통합됩니다. 
      - 익셉션이 발생했을 때 uncheck exception 즉, RuntimeExeption의 서브클래스는 롤백됩니다. check exception은 롤백되지 않는데 rollbackFor=Exception.class를 설정하면 모두 롤백할 수 있습니다. 
    2. 메서드 반환 타입을 엔티티 객체가 아니라 DTO 객체를 이용해 반환했습니다. REST API를 구성할 때 HATEOAS를 이용해 관련 링크를 붙여 EntityModel 객체를 반환했는데 curl로 테스트를 해보니 연관 객체 참조 때문에 JSON이 무한히 반복되는 현상을 발견했기 때문입니다. (좀 더 관례적인 사용법이 있을지도 모릅니다만 클라이언트에게 필요한 만큼의 데이터만 전달한다는 취지로 그대로 진행했습니다.)
---

## 웹 계층
  com.ythwork.soda.web
  - 고려사항
    1. 예전에 REST API를 구성할 때는 단순히 ResponseEntity에서 상태코드와 바디를 설정해 클라이언트에 전달했습니다. 하지만 최근에는 하이퍼미디어를 사용해 반환되는 리소스에 관련된 하이퍼링크를 추가하는 HATEOAS(Hypermedia As The Engine Of Application State)를 활용하는 것 같습니다. 저도 최근 프로젝트에서는 hateoas를 도입해 작업하고 있습니다. 
    2. 하이퍼링크를 추가하면 클라이언트는 URL을 하드코딩하지 않고 유연하게 로직을 처리할 수 있게 됩니다. 서버 쪽에서는 변경 사항이 있을 때 기존 URL을 변경할 수 있어 부담이 적습니다. 데이터와 함께 링크를 가지고 있는 EntityModel 객체를 생성하는 것이 가장 중요한 부분인데 이때는 RepresentationModelAssembler<MemberInfo, EntityModel<MemberInfo>>에 있는 toModel()을 오버라이딩해서 엔티티 객체를 EntityModel 객체로 변환할 assembler 클래스를 만들고 @Component를 붙여 스프링 빈으로 만들어둡니다. 이후 의존성 주입을 통해 컨트롤러에서 사용하게 됩니다. 
    3. EntityModel에 링크를 붙일 때는 linkTo()와 methodOn()을 사용합니다. 사용예는 다음과 같습니다. 
      - return EntityModel.of(accountInfo, 
				linkTo(methodOn(AccountController.class).getAccount(accountInfo.getAccountId())).withSelfRel(),
				linkTo(methodOn(AccountController.class).allAccounts()).withRel("accounts"));
    4. EntityModel의 컬렉션은 컬렉션을 추상화한 CollectionModel 객체를 사용합니다. 사용예는 EntityModel과 매우 유사합니다.
    5. 이러한 형태의 HATEOAS를 HAL(Hypertext Application Language)이라고 하는데 이를 이용하면 api를 반환받은 클라이언트가 다음에 어떤 행동을 취해야 하는지 오류 없이 안내할 수 있습니다. 예를 들어보겠습니다. TransactionController를 보면 송금 거래내역인 Transaction은 POST 메서드로 /transaction을 요청하면 만들어집니다. 이때 TransactionStatus는 IN_PROCESS로 설정됩니다. 이때 반환되는 json을 보면 IN_PROCESS일 때 송금을 완료하는 경우와 송금을 취소하는 경우의 링크를 전달해 클라이언트가 다음 행동을 할 수 있도록 도와줍니다. 만약 TransactionStatus가 COMPLETE나 FAILED일 경우에는 자신의 트랜잭션을 확인할 수 있는 링크와 전체 트랜잭션을 보는 링크만 전달됩니다. 클라이언트는 현재 상황에서 허락되어지지 않는 상태로 가지 않고 결정할 수 있는 다음 행동들에 대해 옵션이 주어지는 것입니다. 만약 허락되어지지 않은 상태로 가려하면 어떤 이유로 안되는지에 대한 메시지를 전달합니다.
    6. Specification<Transaction>을 반환하는 정적 메서드의 모임인 TransactionSpec과 클라이언트로부터 검색 조건을 받아오기 위한 TransactionFilter 클래스를 만들어 검색 기능을 구현했습니다. 회원, 송금 계좌, 거래 시간, 거래내역 상태, 송금액에 따라 해당하는 거래내역을 조회할 수 있습니다.  
---

## curl test 결과
  실제 작동 여부를 curl을 통해 확인해보았습니다. 아래는 몇가지 결과를 정리한 것입니다. 
### 회원 등록
  - request
    - curl -vX POST localhost:8080/member/register  -H 'Content-type:application/json' -d '{"firstName" : "영실", "lastName" : "장", "address" : {"country" : "대한민국", "province" : "경기도", "city" : "한양시", "street" : "도성로", "houseNumber" : "123"}, "phoneNumber" : "0101112223", "email" : "youhgsil@gmail.com", "username" : "youngsil", "password" : "secret", "roles" : ["USER"]}'
  - response
    - HTTP/1.1 201
    - Location: http://localhost:8080/member/login
    - {"fullName":"장영실","address":{"country":"대한민국","province":"경기도","city":"한양시","street":"도성로","houseNumber":"123"},"phoneNumber":"0101112223","email":"youhgsil@gmail.com","memberId":31,"_links":{"self":{"href":"http://localhost:8080/member/31"}}}
 
### 로그인
  - request
    - curl -vX POST localhost:8080/member/login -H 'Content-type:application/json' -d '{"username" : "youngsil", "password" : "secret"}'
  - response
    - {"jwt":"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ5b3VuZ3NpbCIsImlhdCI6MTYwNDk4MzAyOCwiZXhwIjoxNjA0OTgzNjI4fQ.Bx7xmSlkGMqqKIjLZDd35u6YGBkjzqB8ta3JB0T0yhA6t7N-zmZ9ICLbWlYVxnyLrT0rd3-A4AYwW0BhY6hmhw","memberInfo":{"fullName":"장영실","address":{"country":"대한민국","province":"경기도","city":"한양시","street":"도성로","houseNumber":"123"},"phoneNumber":"0101112223","email":"youhgsil@gmail.com","memberId":31},"_links":{"member":{"href":"http://localhost:8080/member/31"}}}
 
### 회원 조회 - case 1. 토큰 없이 본인 조회 -> 권한이 없으므로 에러 
  - request
    - curl -v localhost:8080/member/31
  - response
    - HTTP/1.1 403
    - {"timestamp":"2020-11-10T04:38:40.994+00:00","status":403,"error":"Forbidden","message":"","path":"/member/31"}

### 회원 조회 - case 2. 헤더에 토큰 포함 본인 조회 -> OK 
  - request
    - curl -v localhost:8080/member/31 -H 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ5b3VuZ3NpbCIsImlhdCI6MTYwNDk4MzAyOCwiZXhwIjoxNjA0OTgzNjI4fQ.Bx7xmSlkGMqqKIjLZDd35u6YGBkjzqB8ta3JB0T0yhA6t7N-zmZ9ICLbWlYVxnyLrT0rd3-A4AYwW0BhY6hmhw'
  - response
    - {"fullName":"장영실","address":{"country":"대한민국","province":"경기도","city":"한양시","street":"도성로","houseNumber":"123"},"phoneNumber":"0101112223","email":"youhgsil@gmail.com","memberId":31,"_links":{"self":{"href":"http://localhost:8080/member/31"}}}

### 회원 조회 - case 3. 다른 회원 조회 ->  에러
  - request
    - curl -v localhost:8080/member/27 -H 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ5b3VuZ3NpbCIsImlhdCI6MTYwNDk4MzAyOCwiZXhwIjoxNjA0OTgzNjI4fQ.Bx7xmSlkGMqqKIjLZDd35u6YGBkjzqB8ta3JB0T0yhA6t7N-zmZ9ICLbWlYVxnyLrT0rd3-A4AYwW0BhY6hmhw'
  - response
    - HTTP/1.1 405
    - 멤버 자신의 정보에만 접근이 가능합니다.

### 회원 탈퇴
  - request
    - curl -vX DELETE localhost:8080/member/32 -H 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJvYmFtYSIsImlhdCI6MTYwNDk4MzU5NCwiZXhwIjoxNjA0OTg0MTk0fQ.1eN4thydR1JDviJ2oMZU0jWubuB95PvPvtHS5mTdME3mz_ObCPMUACKE2n6gRx-CtLz4weK63uy-1ecGhjT80A'
  - response
    - HTTP/1.1 204
    
### 계좌 등록
  - request
    - curl -vX POST localhost:8080/account -H 'Content-type:application/json' -H 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ5b3VuZ3NpbCIsImlhdCI6MTYwNDk4NDI4NSwiZXhwIjoxNjA0OTg0ODg1fQ.Z2nuJiMgT2OWOfEuEIkjXaihrB5rO9FFRy6IOL6y_llPrCF7WijrvVyn2bdMcFMDURP0Y7x7RXObD5ezKz05IA' -d '{"memberId" : "31", "code" : "B BANK", "accountNumber" : "222-33-4321"}'
  - response
    - Location: http://localhost:8080/account/14
    - {"owner":"장영실","bankcode":"B BANK","accountNumber":"222-33-4321","balance":36000,"accountId":14,"_links":{"self":{"href":"http://localhost:8080/account/14"},"accounts":{"href":"http://localhost:8080/account"}}}
    
### 계좌 조회
  - request
    - curl -v localhost:8080/account/14 -H 'Content-type:application/json' -H 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ5b3VuZ3NpbCIsImlhdCI6MTYwNDk4NDI4NSwiZXhwIjoxNjA0OTg0ODg1fQ.Z2nuJiMgT2OWOfEuEIkjXaihrB5rO9FFRy6IOL6y_llPrCF7WijrvVyn2bdMcFMDURP0Y7x7RXObD5ezKz05IA'
  - response
    - {"owner":"장영실","bankcode":"B BANK","accountNumber":"222-33-4321","balance":36000,"accountId":14,"_links":{"self":{"href":"http://localhost:8080/account/14"},"accounts":{"href":"http://localhost:8080/account"}}}
    
### 계좌 목록 조회 - 본인의 계좌 정보만 조회 가능
  - request
    - curl -v localhost:8080/account -H 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ5dGh3b3JrIiwiaWF0IjoxNjA0OTg0NzEwLCJleHAiOjE2MDQ5ODUzMTB9.VsXwCXNbCI-kErfcOXb4gjhySuIB20GwqhfTR2XAbLCUH9f9rQx9OILX-4Pk05_gmPuXVLKoLek2ClBrQVwEtQ'
  - response
    - {"_embedded":{"accountInfoList":[{"owner":"양태환","bankcode":"A BANK","accountNumber":"123-45-6789","balance":45000,"accountId":8,"_links":{"self":{"href":"http://localhost:8080/account/8"},"accounts":{"href":"http://localhost:8080/account"}}},{"owner":"양태환","bankcode":"B BANK","accountNumber":"111-22-3333","balance":19500,"accountId":9,"_links":{"self":{"href":"http://localhost:8080/account/9"},"accounts":{"href":"http://localhost:8080/account"}}},{"owner":"양태환","bankcode":"C BANK","accountNumber":"222-33-4321","balance":10000,"accountId":10,"_links":{"self":{"href":"http://localhost:8080/account/10"},"accounts":{"href":"http://localhost:8080/account"}}}]},"_links":{"self":{"href":"http://localhost:8080/account"}}}
 
### 거래 내역 생성
  - request
    - curl -vX POST localhost:8080/transaction -H 'Content-type:application/json' -d '{"memberId" : "31", "sendAcntId" : "14", "recvcode" : "A BANK", "recvAcntNum" : "123-45-6789", "amount" : "35000"}' -H 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ5b3VuZ3NpbCIsImlhdCI6MTYwNDk4NTExMywiZXhwIjoxNjA0OTg1NzEzfQ.WLu49igG_xoBCEQLieSw8Mt_kaUJBuxUmXCf-t3Vh_Uw7B1KmW_4Tr4vr4y9lp_yepLkXwp99DUsuPLK17bL9Q'
  - response
    - {"memberId":31,"sendcode":"B BANK","sendAcntNum":"222-33-4321","recvcode":"A BANK","recvAcntNum":"123-45-6789","amount":35000,"afterBalance":0,"transactionStatus":"IN_PROCESS","processAt":"2020-11-10T05:12:25.168+00:00","transactionId":22,"_links":{"transactions":{"href":"http://localhost:8080/transaction"},"self":{"href":"http://localhost:8080/transaction/22"},"complete":{"href":"http://localhost:8080/transaction/22/complete"},"cancel":{"href":"http://localhost:8080/transaction/22/cancel"}}}
    
### 거래 내역 검색 - Case 1.  거래내역 상태가 IN_PROCESS일 때 
  - request
    - curl -vX GET localhost:8080/transaction -H 'Content-type:application/json' -d '{"memberId" : "", "sendAcntId" : "", "from" : "", "to" : "", "status" : "IN_PROCESS", "amount" : ""}' -H 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ5b3VuZ3NpbCIsImlhdCI6MTYwNDk4NTExMywiZXhwIjoxNjA0OTg1NzEzfQ.WLu49igG_xoBCEQLieSw8Mt_kaUJBuxUmXCf-t3Vh_Uw7B1KmW_4Tr4vr4y9lp_yepLkXwp99DUsuPLK17bL9Q'
  - response
    - {"_embedded":{"transactionInfoList":[{"memberId":31,"sendcode":"B BANK","sendAcntNum":"222-33-4321","recvcode":"A BANK","recvAcntNum":"123-45-6789","amount":35000,"afterBalance":0,"transactionStatus":"IN_PROCESS","processAt":"2020-11-10T05:12:25.000+00:00","transactionId":22,"_links":{"transactions":{"href":"http://localhost:8080/transaction"},"self":{"href":"http://localhost:8080/transaction/22"},"complete":{"href":"http://localhost:8080/transaction/22/complete"},"cancel":{"href":"http://localhost:8080/transaction/22/cancel"}}}]},"_links":{"self":{"href":"http://localhost:8080/transaction"}}}
    
### 거래 내역 검색 - Case 2.  거래 내역 상태가 SUCCEEDED일 때
  - request
    - curl -vX GET localhost:8080/transaction -H 'Content-type:application/json' -d '{"memberId" : "", "sendAcntId" : "", "from" : "", "to" : "", "status" : "SUCCEEDED", "amount" : ""}' -H 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzdW5zaW4iLCJpYXQiOjE2MDQ5ODU1MzksImV4cCI6MTYwNDk4NjEzOX0.uCOkSRdnu1SB7vyjist2sZbWbCTvovNsmKHoViSVLt5baWhSgNo1OL7EgEcXJf5__niGFtT82ndUTvLMHMGzPw'
  - response
    - {"_embedded":{"transactionInfoList":[{"memberId":30,"sendcode":"A BANK","sendAcntNum":"333-44-1234","recvcode":"B BANK","recvAcntNum":"222-33-4321","amount":30000,"afterBalance":0,"transactionStatus":"SUCCEEDED","processAt":"2020-11-09T13:51:27.000+00:00","transactionId":19,"_links":{"transactions":{"href":"http://localhost:8080/transaction"},"self":{"href":"http://localhost:8080/transaction/19"}}}]},"_links":{"self":{"href":"http://localhost:8080/transaction"}}}

### 거래 내역 검색 - Case 3. 거래 내역 상태가 FAILED 일 때
  - request
    - curl -vX GET localhost:8080/transaction -H 'Content-type:application/json' -d '{"memberId" : "", "sendAcntId" : "", "from" : "", "to" : "", "status" : "FAILED", "amount" : ""}' -H 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ5b3VuZ3NpbCIsImlhdCI6MTYwNDk4Njg5MiwiZXhwIjoxNjA0OTg3NDkyfQ.jgFi1UYPCc4rxUs_X5xsnx9HXnC5l3pET9JdKIrQhcdw3OdV8gTZNru-fXNXBy2sa_4isvv7XuNDmHC45PpyKw'
  - response
    - {"_embedded":{"transactionInfoList":[{"memberId":31,"sendcode":"B BANK","sendAcntNum":"222-33-4321","recvcode":"B BANK","recvAcntNum":"111-22-3333","amount":6000,"afterBalance":-5000,"transactionStatus":"FAILED","processAt":"2020-11-10T05:45:29.000+00:00","transactionId":26,"_links":{"transactions":{"href":"http://localhost:8080/transaction"},"self":{"href":"http://localhost:8080/transaction/26"}}}]},"_links":{"self":{"href":"http://localhost:8080/transaction"}}}

### 거래 내역 1건 조회
  - request
    - curl -v localhost:8080/transaction/22 -H 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ5b3VuZ3NpbCIsImlhdCI6MTYwNDk4NTgxOSwiZXhwIjoxNjA0OTg2NDE5fQ.yi1gzlD35bvkag90oExPrx2IdbtOLUb27e4TIbqhKhoUXE6li4SobudGVVJnCLb8cvr34Sq3CSA_e_hIvm8xBA'
  - response
    - {"memberId":31,"sendcode":"B BANK","sendAcntNum":"222-33-4321","recvcode":"A BANK","recvAcntNum":"123-45-6789","amount":35000,"afterBalance":0,"transactionStatus":"IN_PROCESS","processAt":"2020-11-10T05:12:25.000+00:00","transactionId":22,"_links":{"transactions":{"href":"http://localhost:8080/transaction"},"self":{"href":"http://localhost:8080/transaction/22"},"complete":{"href":"http://localhost:8080/transaction/22/complete"},"cancel":{"href":"http://localhost:8080/transaction/22/cancel"}}}

### 송금 완료
  - request
    - curl -vX PUT localhost:8080/transaction/22/complete -H 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ5b3VuZ3NpbCIsImlhdCI6MTYwNDk4NTgxOSwiZXhwIjoxNjA0OTg2NDE5fQ.yi1gzlD35bvkag90oExPrx2IdbtOLUb27e4TIbqhKhoUXE6li4SobudGVVJnCLb8cvr34Sq3CSA_e_hIvm8xBA'
  - response
    - {"memberId":31,"sendcode":"B BANK","sendAcntNum":"222-33-4321","recvcode":"A BANK","recvAcntNum":"123-45-6789","amount":35000,"afterBalance":1000,"transactionStatus":"SUCCEEDED","processAt":"2020-11-10T05:12:25.000+00:00","transactionId":22,"_links":{"transactions":{"href":"http://localhost:8080/transaction"},"self":{"href":"http://localhost:8080/transaction/22"}}}
    
### 송금 취소
  - request
    - curl -vX DELETE localhost:8080/transaction/24/cancel -H 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ5dGh3b3JrIiwiaWF0IjoxNjA0OTg2NDQyLCJleHAiOjE2MDQ5ODcwNDJ9.hxlPYdPp3wUF3hBpoBKD-wiBJkKd0tGDr4H7QtP84jdhwLGZY5klxUfiLKqe2zA3BvVdhoI823DHqrdKs_ZGmg'
  - response
    - {"memberId":27,"sendcode":"A BANK","sendAcntNum":"123-45-6789","recvcode":"B BANK","recvAcntNum":"222-33-4321","amount":200000,"afterBalance":0,"transactionStatus":"CANCELED","processAt":"2020-11-10T05:35:58.000+00:00","transactionId":24,"_links":{"transactions":{"href":"http://localhost:8080/transaction"}}}
    
### 송금 실패
  - request
    - curl -vX PUT localhost:8080/transaction/25/complete -H 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ5dGh3b3JrIiwiaWF0IjoxNjA0OTg2NDQyLCJleHAiOjE2MDQ5ODcwNDJ9.hxlPYdPp3wUF3hBpoBKD-wiBJkKd0tGDr4H7QtP84jdhwLGZY5klxUfiLKqe2zA3BvVdhoI823DHqrdKs_ZGmg'
  - response
    - {"memberId":27,"sendcode":"A BANK","sendAcntNum":"123-45-6789","recvcode":"B BANK","recvAcntNum":"222-33-4321","amount":200000,"afterBalance":-120000,"transactionStatus":"FAILED","processAt":"2020-11-10T05:38:08.000+00:00","transactionId":25,"_links":{"transactions":{"href":"http://localhost:8080/transaction"},"self":{"href":"http://localhost:8080/transaction/25"}}}
---



    
