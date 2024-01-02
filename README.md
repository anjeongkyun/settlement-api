<h1>카카오페이 1/N 정산 기능 만들기 with EDA</h1>

**<h2>목차</h2>**

- [기술 스택](#기술-스택)
- [Application 실행 방법](#Application-실행-방법)
- [단위 테스트 실행 방법](#단위-테스트-실행-방법)
- [API Spec](#API-Spec)
- [도메인 의존성](#도메인-의존성)
- [서비스 구성](#서비스-구성)

**<h2>기술 스택</h2>**

- Kotlin (jdk 17)
- Spring Boot (v3.1.1)
- Spring Data MongoDB
- Docker
  - MongoDB
- JUnit
  - Test Fixture는 [AutoParams](https://github.com/AutoParams/AutoParams) 사용

**<h2>Application 실행 방법</h2>**

```
$ git clone https://github.com/anjeongkyun/settlement.git
$ cd settlement-api
$ docker-compose up #mongo 실행
$ ./gradlew clean api:build && java -jar api/build/libs/api-0.0.1-SNAPSHOT.jar
```

- 접속 URI: http:localhost:8080

**<h2>단위 테스트 실행 방법</h2>**
<img width="600" alt="277338008-2ff2d8a0-f02c-4b61-9a4a-32d95e224139" src="https://github.com/anjeongkyun/settlement/assets/97106584/dd539280-09f6-425d-b5d5-8843cc203cb7">
```
$ git clone https://github.com/anjeongkyun/settlement.git
$ cd settlement-api
$ make test
```

**<h2>API Spec</h2>**

**<h3>정산 요청 API**</h3>

- Curl Command

```
curl -X POST \
  -H "Content-Type: application/json" \
  -H "X-USER-ID: a532fb86-1459-4121-a040-4pad142e3qd6" \
  -d '{
    "price": {
        "amount": 100000.0,
        "currency": "KRW"
    },
    "recipientIds": ["d798fb86-7657-4071-a040-7c6d844e4cd6", "6ba7b810-9dad-11d1-80b4-00c04fd430c8", "2aq7b8h0-2d3a-18v1-8t14-91c02ad48018"]
}' \
  http://localhost:8080/settlements/commands/request-settlement
```

- 정산 요청 API 입니다.
- 정산 시 요청자의 정보로 정산이 완료된 하나의 트랜잭션이 생성됩니다.
- 다른 Recipient는 요청된 정산 송금 API를 통해 정산 처리를 할 수 있습니다.

**<h3>요청된 정산 송금 API</h3>**

- Curl Command

```
curl -X POST \
  -H "Content-Type: application/json" \
  -H "X-USER-ID: d798fb86-7657-4071-a040-7c6d844e4cd6" \
  -d '{
    "settlementId": "정산 ID",
    "price": {
        "amount": 25000.0,
        "currency": "KRW"
    }
}' \
  http://localhost:8080/settlements/commands/transfer-requested-settlement
```

- 정산 요청을 받은 사용자가 송금을 하기 위한 API 입니다.
- (settlementId는 아래 `요청한 정산 전체 내역 조회 API` or `요청받은 정산 전체 내역 조회 API`를 요청하여 획득할 수 있습니다.)
- Settlement Aggregate의 내부에 적재되는 Transactions Price 합이 정산의 금액과 동일해질 경우 정산 완료 처리됩니다.
  - e.g.) X(요청자)가 A ~ C(수신자)에게 10만원을 1/n 정산 요청했을 경우. A와 B는 이미 송금이 완료되어있을 때,
    - C가 정산을 위해 마지막으로 송금 했을 때 C의 송금 금액 과 X, A, B Transaction의 Price 합(25,000 + 25,000 \* 3)이 Settlement의 Price와 같으면 정산 완료 처리가 됩니다.

**<h3>요청한 정산 전체 내역 조회 API</h3>**

- Curl Command

```
curl -X GET \
  -H "X-USER-ID: a532fb86-1459-4121-a040-4pad142e3qd6" \
  http://localhost:8080/settlements/queries/get-settlements-for-requester
```

- 자신이 요청한 정산하기 전체 리스트를 조회하는 API입니다.
  - 요청 헤더 X-USER-ID는 requesterId의 논리가 됩니다.

**<h3>요청받은 정산 전체 내역 조회 API</h3>**

- Curl Command

```
curl -X GET \
  -H "X-USER-ID: d798fb86-7657-4071-a040-7c6d844e4cd6" \
  http://localhost:8080/settlements/queries/get-settlements-for-recipient
```

- 요청받은 정산하기 전체 리스트를 조회하는 API입니다.
  - 요청 헤더 X-USER-ID는 recipientId의 논리가 됩니다.

**<h3>미정산 유저 이벤트 발행 API</h3>**

- Curl Command

```
curl -X POST \
  -H "Content-Type: application/json" \
  -d '{
    "settlementId": "정산 ID"
}' \
  http://localhost:8080/settlements/commands/publish-un-settled-user-event
```

- 해당 API는 미정산된 유저 이벤트를 발행하는 역할을 합니다.
- (settlementId는 아래 `요청한 정산 전체 내역 조회 API` or `요청받은 정산 전체 내역 조회 API`를 요청하여 획득할 수 있습니다.)
- 추 후 알림 도메인이 생기면 해당 도메인에서 이벤트를 구독 및 소비하여 정산 서버에게 정산ID로 조회하여 정보를 획득하여 해당 유저에게 알림을 보내는 플로우를 가집니다.

<br>

**<h4>API TEST 방법</h4>**

1. 정산 요청 API를 통해 정산 데이터를 생성해줍니다.
2. 요청한 정산 전체 내역 조회 API를 통해 정산 ID를 조회합니다.
3. 이 때, 요청받은 정산 전체 내역 조회 API도 정산 요청할 때 recipients의 ID를 통해 조회 할 수도 있습니다.
4. 요청된 정산 송금 API를 요청하여 모든 recipients가 송금을 완료했다면, 해당 정산은 정산완료 상태(SettlementStatus.SETTLED)가 되는 것을 테스트 해볼 수 있습니다. (정산 완료 된 것은 2, 3번 항목의 조회 API를 통해 확인할 수 있습니다.)

**리마인드 알림 테스트**

1. 미정산 유저 이벤트 발행 API를 요청합니다.

<br>

**<h2>도메인 의존성</h2>**

- **고려 대상 도메인(DDD 기반 MSA 구조로 설계)**
  - User (유저)
  - Settlement (정산)
  - Notification (알림)
- **의존성 방향**
  - Settlement --> User를 의존합니다.
  - Notification --> Settlement를 의존합니다.
    - 정산을 하는데 있어 알림 도메인의 사이드 이펙트를 최소화 하기 위해 위와 같이 의존성 방향을 설계하였습니다.
    - EDA를 기반하여 이벤트를 통해 도메인간 데이터를 비동기로 싱크하도록 구성하였습니다.
      - e.g.) 알림 도메인에서 정산 요청 이벤트 구독 -> 정산 요청 완료 이벤트 발행 -> 알림 도메인에서 정산 도메인 조회 -> 사용자 알림 발행

**<h2>서비스 구성</h2>**

- **Clean Arichitecture을 기반으로 멀티 모듈을 구성하여 모듈간 강력한 의존성 관리할 수 있도록 구성했습니다.**
  - **Api**
    - Application Configuration, Bean 주입, Controller 등 외부(Spring)에 대한 의존성을 갖는 모듈입니다.
  - **Contracts**
    - 통신 계약 모델이 작성되는 모듈입니다. e.g.) DTO(Data Transfer Object), VO(Value Object) 등
  - **Domain-Model**
    - 도메인 논리를 Domain-Model에만 응집시키고, 외부 모듈에 대한 의존 관리는 Interface로 구성했습니다.
      - 따라서, 외부 모듈인 Data-Access, Gateway, Publisher의 구현체 모듈은 언제든지 다른 프레임워크, 라이브러리로 교체 될 수 있습니다.
    - 도메인 논리를 구현하는 UseCase와 Domain Entity, 외부 모듈(gateway, publisher 등)로부터의 Interface가 정의되어있습니다.
      - UseCase는 하나의 책임만 가질 수 있도록 구성하였고, 추 후 명령과 조회의 책임을 분리할 수 있도록 command와 query 모델을 분리하여 작성하였습니다.
        - 응집된 테스팅을 위해 서비스 로직이 단일 책임 원칙을 가지도록 구성했습니다.
  - **Data-Access**
    - 데이터 접근에 대한 컨텍스트를 가진 모듈입니다. Data Access에 관련된 구현체들이 담겨져있습니다.
      - e.g.) Repository 구현체, Data Model(Document), Domain Model의 Entity로 변환시키는 Data Mapper
  - **Publisher**
    - Event Driven Arichitecture로 구성되어, 이벤트를 발행하는 구현체가 담긴 모듈입니다. 현재 구현체에 대한 내용은 없지만, 실제 구현을 한다면 EventBridge, SQS, SNS, Kafka 등의 메세지 브로커의 의존성이 추가될 수 있습니다.
  - **Gateway**
    - 외부 통신을 위한 모듈입니다. 현재 구현체에 대한 내용은 없지만, 실제 구현을 한다면 Webclient, RestTemplate 등 외부 서비스와 통신을 위한 의존성이 추가될 수 있습니다.
  - **Unit-Test**
    - 테스트가 작성되는 모듈입니다.
      - 현재 Test는 도메인 논리가 포함되어있는 UseCase에 대한 테스트 코드가 작성되어있습니다.
- **각 도메인 서비스 간 Loose Coupling과 추 후 확장성을 고려하여 Event Driven Arichitecture로 구성했습니다.**
  - e.g.) 정산 요청이 완료된 후, 알림 도메인으로 알림을 보내라는 요청을 보내는 것이 아닌, `SettlementRequestedEvent`를 발행하여 해당 이벤트를 구독하고 있는 알림 서비스는 이벤트를 컨슘하여, 정산 서비스로부터 해당 정산 정보를 조회(zero payload 방식)하여 정보를 획득하여 알림을 보내는 방식으로 구성했습니다.
  - 리마인드 알림 기능은 현재 API 호출을 통해 이벤트를 발행되는 것으로 구현이 되어있지만, 추 후 배치잡을 통해서 주기적으로 이벤트를 발행할 수 있는 구조로 구현했습니다.
- **Test는 Embedded Database가 아닌 동일한 환경(DB)에서 테스팅 하기 위해 Docker로 Mongo Container를 띄운 후 통합 테스트로 구성했습니다.**
  - 외부 모듈에 대한 구현체는 **Test Double**의 Spy와 Stub을 이용하여 테스트를 진행했습니다.
  - 테스팅 실행 편의를 위해 makefile을 구성하여, 테스트 해볼 수 있습니다.
- **예외 처리는 InvalidRequestException과 InvalidCommandException을 정의 한 뒤, 내부 프로퍼티에서 key와 reason을 통해 처리할 수 있도록 구성했습니다.**
  - key와 reason을 통해 어느 프로퍼티가 어떤 이유로 발생했는지 catch 할 수 있습니다.
    - e.g.) key: "settlementId", reason: "NotFound"
