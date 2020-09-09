# 1일차
기존에 있던 프로젝트를 급히 마무리하고, 새로운 프로젝트를 시작.  
먼저 Spring Security 적용, Redis 적용을 마무리 했고, JPA도 설정을 해놓고 스키마 셋팅완료.  
다음 일차는 전날차에 계획하기로 했다.    
# 2일차
## 계획
1. 카테고리(Category) Depth형태로 구현
2. 상품(Item) 객체 생성  
3. 리뷰(ItemReview) 객체 생성
4. 3일차 작성
## 회고
테스트 코드를 짜는데 JPA Repository를 Mock으로 등록이 가능할것 같은데.  
아직 잘 모르겠다.  
Review 테스트코드를 등록하려면 Category, Item을 먼저 등록해줘야하고..  
방법을 찾아봐야겠다
# 3일차
1. 2일차 회고 찾아보기
2. 카테고리에 있는 필드 채우기
3. 상품에 있는 필드 채우기
4. 리뷰에 있는 필드 채우기
5. 4일차 작성

## 회고
Service Layer는 다른 Layer를 침범해서는 안될것 같은데 이미 나는 침범하고 있는것 같다.  
2일차때 짠 테스트코드는 Repository 테스트코드가 맞는것 같다.  

# 4일차
주말이 끼어서 많이는 하지 못할것 같다.  
1. 2일차때 테스트코드 수정 + 3일차 테스트코드 작성
2. 장바구니 추가 (basket)
3. 옵션 추가 ( 1 : 0..1 ) ( review, item )
4. 5일차 작성

## 회고
review에는 옵션이 필요 없다.  
구매내역을 OTO 할것인데 필요할리가 없다.  
나중에 되면은 구매내역에 있는 Item도 제거할 이유가 있을것 같다.  

# 5일차
1. 주문내역 추가  
2. 주문하기 로직 추가 
3. 6일차 작성

## 회고
나의 테스트코드는 무척이나 난잡하며 반복된다.  
이것을 최대한 줄이도록 노력해보자.  

# 6일차
1. 재고 추가
2. 7일차 작성

## 회고
출장을 다녀와서 신경을 잘 쓰지를 못했다.  
다시 파이팅 해야겠다.  
이벤트 퍼블리싱방법을 사용했는데 매우 만족한다  
핵심은
AbstractAggregateRoot를 사용하여 이벤트를 등록하고, @Componenet, @EventListener 조합으로 이벤트 Listener를 하면 된다.  
이벤트를 등록하면 save때 Listener가 시작이 되니 참고하자.  
save를 실행안하면은 이벤트는 동작이 안된다.  

# 7일차
1. 재고 감소
2. 8일차 작성

## 회고
테스트 케이스가 너무 지저분하다.  
한번 리펙토링이 필요할 때다.  
그리고 MockBean과 Mock의 차이점을 대충 알것같다.  
Mock은 Spring Container에 들어가지 않아 다른 Component에 Mock객체가 주입이 되지 않을경우 Spring의 IoC, DI가 작동된다.  
MockBean은 Spring Container에 주입되어 위와 같은 문제는 없을것이다.  
그리고 any...의 함수가 있는데 테스트 케이스에 어떤 값이 들어와도 허용하도록 하는것이다.  
any()를 쓰면은 값을 비교할떄 eq("") 로 수정해줘야 한다.  

# 8일차
1. 테스트코드 리팩토링
2. JMeter로 재고 감소 테스트
~~3. 취소시 재고 증가~~
4. 9일차 작성

## 회고
테스트코드는 어느정도 수정했다.  
나중에 한번 더 리팩토링을 해야겠다.  

그리고 JMeter TPS 측정시 100~150사이가 나오고,
재고가 부족하면은 300TPS정도 나온다.  
물록 MYSQL 은 Docker로 뛰었을때다.  
그리고 update문에 stock을 where로 넣어서 return 값으로 재고확인을 하도록 했다.  
그러면 option마다 lock이 걸릴것이다(option의 pk가 index가 타기에 row lock이 걸릴것.)  
1만개의 재고가 있을경우 문제없이 진행이 되었다.  

3번 취소시 재고 증가는 9일차에 진행하도록 하겠다.    

아래는 TPS 측정, 동시성 이다.  
![캐시없이 tps 측정](images/no-cache-tps.png)
![캐시없이 동시성](images/no-cache-동시성.png)

# 9일차
1. 취소시 재고 증가
2. 10일차 작성

## 회고
테스트코드 재작성 필요할듯하다.  
EventPublish는 어떻게 테스트할건가..?
그리고 Service 테스트인데 Service를 테스트 하지 않고, 도메인 테스트를 하는 테스트를 작성한것이 보인다.  
왜그랬을까..  
다시 작성해야겠다.
  
# 10일차
1. 테스트코드 수정 및 재작성 필요시 작성
2. 11일차 작성

## 회고
테스트코드를 slice형태로 바꿧다.  
그리고 eventPublish의 테스트는 SpringBootTest로 진행을 했다.  
나쁘지 않게 깔끔하게 된것 같다.  

# 11일차
1. Redis 연결 및 테스트
2. 12일차 작성

## 회고
그닥 그렇다!

# 12일차
1. 상품 주문시 redis로 재고관리
2. 상품 주문 TPS 측정 
3. 13일차 작성

## 회고
좋지못한일이 8월23일날 있었다.  
그로인해 커밋을 잘 하지못했다.  

아래는 TPS 측정이다.

![캐시 tps 측정](images/cache-tps.png)
![캐시 동시성](images/cache-동시성.png)

# 13일차
1. 상품 재고 차감 후 rollback시 재고도 rollback
2. 14일차 작성

# 14일차
1. 12, 13일차 테스트코드 작성
2. kafka 설치
3. 15일차 작성

## 회고
Kafka 설정을 잘못했더니 오류가 발생했다.  
설정에 대해서 좀 더 조사해보자.
https://team-platform.tistory.com/32
https://www.popit.kr/kafka-%EC%9A%B4%EC%98%81%EC%9E%90%EA%B0%80-%EB%A7%90%ED%95%98%EB%8A%94-topic-replication/
https://www.popit.kr/kafka-%EC%9A%B4%EC%98%81%EC%9E%90%EA%B0%80-%EB%A7%90%ED%95%98%EB%8A%94-replication-factor-%EB%B3%80%EA%B2%BD/
https://stackoverflow.com/questions/50895344/spring-kafka-and-number-of-topic-consumers
### 공부한것
1. NewTopic을 Bean 으로 등록하면 Topic이 생성된다. 이미 생성이 되어있으면은 안됨
2. ENABLE_AUTO_COMMIT_CONFIG을 true로 하고, AUTO_COMMIT_INTERVAL_MS_CONFIG의 값을 설정해주면은 AUTO_COMMIT_INTERVAL_MS_CONFIG 설정값 마다 offset이 commit이 된다.  
3. 만약 commit이 안되면은 해당 토픽의 데이터를 중복으로 읽을 수 있다
4. AUTO_COMMIT_INTERVAL_MS_CONFIG 를 false로 하면은 수동적으로 commit이 가능하다. 중요한것은 수동으로
5. KafkaListenerContainerFactory의 구현체는 ConcurrentKafkaListenerContainerFactory 밖에 없다
6. ConcurrentKafkaListenerContainerFactory의 concurrency는 동시에 데이터를 읽을 갯수이다.

# 15일차
1. Kafka 코드 적용하기(test말고)
2. 물건 주문시 메세지 발행하기
3. 16일차 작성

## 회고
아주 간단한거 같다..

# 16일차
1. 다른 서브 프로젝트에서 Kafka에서 Consume해와서 N초뒤에 랜덤적으로 결제완료 / 결제불가로 변경
2. 17일차 작성
  