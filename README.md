# Stempo Server &middot; [![GitHub License](https://img.shields.io/github/license/KKKK-Stempo/stempo-server)](https://github.com/KKKK-Stempo/stempo-server/blob/develop/LICENSE) [![Spring Boot Gradle CI](https://github.com/KKKK-Stempo/stempo-server/actions/workflows/spring-boot-gradle-ci.yml/badge.svg)](https://github.com/KKKK-Stempo/stempo-server/actions/workflows/spring-boot-gradle-ci.yml)

Stempo는 뇌성마비 및 후천적 장애 환자들의 리듬기반 재활 훈련을 돕기 위한 프로젝트입니다. Stempo는 환자들의 보행 훈련을 지원하며, 사용자의 보행 기록을 분석하여 맞춤형 훈련 리듬을 제공합니다. 이 프로젝트는 장애 환자들이 일상 생활에서 신체 능력을 향상시키고 재활 속도를 높일 수 있도록 돕는 것을 목표로 합니다.

- **맞춤형 리듬 제공**: 사용자의 데이터를 기반으로 개인 맞춤형 훈련 리듬을 제공하여 보행 능력을 향상.
- **보행 훈련 기록 관리**: 사용자의 보행 훈련 기록을 저장하고, 분석하여 연속 훈련 일수, 훈련 횟수, 정확도 등을 시각적으로 제공.
- **과제 관리**: 사용자에게 주어지는 재활 과제를 관리하고, 완료 상태를 기록하며 진척도를 추적.

백엔드 시스템뿐만 아니라 안드로이드 시스템도 모두 공개되어 있습니다. 관심이 있으신 분들은 [여기](https://github.com/KKKK-Stempo/stempo-android)에서 안드로이드 리포지토리도 확인해보세요.

## 프로젝트 구조
```markdown
└── domain
    ├── application
    │   ├── event
    │   ├── exception
    │   ├── handler
    │   └── service
    ├── domain
    │   ├── model
    │   └── repository
    ├── persistence
    │   ├── entity
    │   ├── mapper
    │   └── repository
    └── presentation
        ├── controller
        ├── dto
        └── mapper

└── global
    ├── auth
    │   ├── application
    │   ├── exception
    │   ├── filter
    │   ├── jwt
    │   └── util
    ├── config
    ├── dto
    ├── exception
    ├── handler
    └── util
```

### 패키지 구성
- `domain`: 애플리케이션의 핵심 비즈니스 로직이 위치한 계층입니다. `application`, `domain`, `persistence`, `presentation`으로 구분되어 있습니다.
    - **application**: 비즈니스 로직을 처리하는 서비스 클래스와, 이벤트, 예외 처리 로직들이 포함됩니다. 실제 비즈니스 연산을 수행하며, 외부와의 상호작용을 담당합니다.
        - **event**: 비즈니스 이벤트를 정의하는 클래스들이 포함됩니다.
        - **exception**: 비즈니스 로직에서 발생할 수 있는 예외 처리 클래스들이 포함됩니다.
        - **handler**: 운영체제나 파일 시스템과 같은 시스템 리소스와 직접 상호작용하는 로직들이 포함됩니다.
        - **service**: 인바운드 포트를 구현한 서비스 계층으로, 주요 비즈니스 로직이 구현됩니다.
    - **domain**: 비즈니스 로직과 관련된 핵심 도메인 규칙이 정의된 계층입니다. 여기에는 주요 비즈니스 로직을 처리하는 모델과 리포지토리 인터페이스가 포함됩니다.
        - **model**: 비즈니스 규칙에 맞는 도메인 객체들이 위치하며, 주로 애플리케이션에서 사용되는 핵심 도메인 로직을 담고 있습니다.
        - **repository**: 도메인 모델의 영속성을 처리하기 위한 리포지토리 인터페이스가 위치합니다.
    - **persistence**: 데이터베이스와의 상호작용을 처리하는 계층입니다. 여기에는 엔티티와 리포지토리 구현체, 데이터 매핑을 위한 매퍼가 포함됩니다.
        - **entity**: 데이터베이스 테이블과 직접 매핑되는 엔티티 클래스들이 포함됩니다. 도메인의 비즈니스 로직과는 분리되어 데이터 저장 및 조회와 관련된 역할을 담당합니다.
        - **mapper**: 데이터베이스와 도메인 객체 간의 매핑을 담당하는 로직이 위치합니다.
        - **repository**: domain 패키지의 리포지토리 인터페이스를 실제로 구현하여 데이터베이스 연동 로직을 처리하는 클래스들이 포함됩니다.
    - **presentation**: 사용자의 요청을 처리하고 응답을 반환하는 계층입니다. 주로 컨트롤러와 DTO가 포함되어 있으며, 클라이언트와 직접 상호작용하는 역할을 담당합니다.
        - **controller**: HTTP 요청을 처리하고, 비즈니스 로직에 전달하며, 응답을 반환하는 컨트롤러 클래스들이 포함됩니다.
        - **dto**: 데이터를 전송하거나 받기 위한 데이터 전송 객체(DTO)가 포함됩니다.
        - **mapper**: DTO와 도메인 객체 간의 매핑을 담당하는 로직이 위치합니다.
- `global`: 프로젝트 전반에 걸쳐 공통적으로 사용되는 클래스들을 포함하는 계층입니다.
    - **auth**: 인증 및 권한 부여와 관련된 로직을 처리합니다.
        - **application**: 인증 및 권한 관련 비즈니스 로직이 포함됩니다.
        - **exception**: 인증 과정에서 발생할 수 있는 예외 처리 클래스들이 위치합니다.
        - **filter**: 인증 및 권한 검증 필터 클래스들이 포함됩니다.
        - **jwt**: JWT 토큰 생성, 검증과 관련된 클래스들이 포함됩니다.
        - **util**: 인증 관련 유틸리티 클래스들이 포함됩니다.
    - **config**: 애플리케이션 설정 관련 클래스가 포함되어 있습니다. 데이터베이스, 보안, 환경 설정 등이 이곳에 위치합니다.
    - **dto**: 프로젝트 전반에서 사용되는 공통적인 데이터 전송 객체들이 포함되어 있습니다.
    - **exception**: 프로젝트 전역에서 발생할 수 있는 예외를 처리하는 클래스들이 위치합니다.
    - **handler**: 전역적인 예외 처리 및 로깅을 담당하는 핸들러 클래스들이 포함됩니다.
    - **util**: 다양한 공통 유틸리티 기능을 제공하는 클래스들이 포함됩니다.

## 라이선스
이 프로젝트는 GNU 일반 공중 사용 허가서(GPL) v3.0에 따라 라이선스가 부여됩니다. 자세한 내용은 [LICENSE](https://github.com/KKKK-Stempo/stempo-server?tab=GPL-3.0-1-ov-file#readme) 파일을 확인하세요.

## 기여자
<a href="https://github.com/KKKK-Stempo/stempo-server/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=KKKK-Stempo/stempo-server" />
</a>
