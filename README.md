# Stempo Server &middot; [![GitHub License](https://img.shields.io/github/license/KKKK-Stempo/stempo-server)](https://github.com/KKKK-Stempo/stempo-server/blob/develop/LICENSE) [![Spring Boot Gradle CI](https://github.com/KKKK-Stempo/stempo-server/actions/workflows/spring-boot-gradle-ci.yml/badge.svg)](https://github.com/KKKK-Stempo/stempo-server/actions/workflows/spring-boot-gradle-ci.yml)

Stempo는 뇌성마비 및 후천적 장애 환자들의 리듬기반 재활 훈련을 돕기 위한 프로젝트입니다. Stempo는 환자들의 보행 훈련을 지원하며, 사용자의 보행 기록을 분석하여 맞춤형 훈련 리듬을 제공합니다. 이 프로젝트는 장애 환자들이 일상 생활에서 신체 능력을 향상시키고 재활 속도를 높일 수 있도록 돕는 것을 목표로 합니다.

- **맞춤형 리듬 제공**: 사용자의 데이터를 기반으로 개인 맞춤형 훈련 리듬을 제공하여 보행 능력을 향상.
- **보행 훈련 기록 관리**: 사용자의 보행 훈련 기록을 저장하고, 분석하여 연속 훈련 일수, 훈련 횟수, 정확도 등을 시각적으로 제공.
- **과제 관리**: 사용자에게 주어지는 재활 과제를 관리하고, 완료 상태를 기록하며 진척도를 추적.

백엔드 시스템뿐만 아니라 안드로이드 시스템도 모두 공개되어 있습니다. 관심이 있으신 분들은 [여기](https://github.com/KKKK-Stempo/stempo-android)에서 안드로이드 리포지토리도 확인해보세요.

## 프로젝트 구조
```markdown
└── stempo-api
    ├── annotation
    ├── config
    ├── controller
    ├── exception
    └── support
        └── yaml

└── stempo-application
    ├── dto
    │   ├── request
    │   └── response
    ├── event
    ├── exception
    ├── handler
    ├── mapper
    └── service

└── stempo-auth
    ├── application
    ├── config
    ├── exception
    ├── filter
    ├── model
    └── util

└── stempo-common
    ├── config
    ├── dto
    ├── exception
    └── util

└── stempo-domain
    ├── model
    └── repository

└── stempo-infrastructure
    ├── config
    ├── entity
    ├── exception
    ├── mapper
    ├── repository
    └── util
```

### 패키지 구성
- `stempo-api`: 사용자 요청을 처리하고 비즈니스 로직과 상호작용하는 모듈입니다.
    - **annotation**: API 응답에 대한 커스텀 어노테이션과 관련된 클래스들을 포함합니다.
    - **config**: API 요청 및 응답에 대한 설정 클래스들이 포함됩니다.
    - **controller**: HTTP 요청을 처리하고, 서비스 계층에 비즈니스 로직을 전달하며, 처리된 결과를 응답으로 반환하는 컨트롤러 클래스들이 포함됩니다.
    - **exception**: API 요청 처리 중 발생할 수 있는 예외를 처리하고 클라이언트에 적절한 응답을 반환하는 클래스들이 포함됩니다.
    - **support**: YAML 파일을 로딩하거나 파싱하는 등의 지원 기능을 제공하는 유틸리티 클래스들이 포함됩니다.

- `stempo-application`: 비즈니스 로직을 처리하고 도메인 객체와 상호작용하는 모듈입니다.
    - **dto**: 클라이언트와 서버 간에 데이터를 전송하기 위한 객체로, 요청 및 응답을 처리하는 역할을 합니다.
    - **event**: 비즈니스 로직 수행 중 발생하는 이벤트를 처리하는 클래스들이 포함됩니다.
    - **exception**: 비즈니스 로직 처리 중 발생할 수 있는 예외를 처리하는 클래스들이 포함됩니다.
    - **handler**: 파일과 같은 시스템 리소스와 상호작용하거나 특정 작업을 처리하는 로직이 포함됩니다.
    - **mapper**: 도메인 객체와 DTO 객체 간의 변환을 처리하는 클래스들이 포함됩니다.
    - **service**: 실제 비즈니스 로직을 처리하고 데이터를 조작하는 서비스 클래스들이 포함됩니다.

- `stempo-auth`: 사용자 인증 및 권한 부여를 처리하는 모듈입니다.
    - **application**: JWT 기반의 인증 및 사용자 정보를 처리하는 서비스 계층의 클래스들이 포함됩니다.
    - **config**: 인증 및 보안 설정을 관리하는 클래스들이 포함됩니다.
    - **exception**: 인증 및 권한 부여 과정에서 발생하는 예외를 처리하는 클래스들이 포함됩니다.
    - **filter**: HTTP 요청을 가로채어 인증 및 권한 부여를 처리하는 필터 클래스들이 포함됩니다.
    - **handler**: 인증과 관련된 로깅을 처리하는 인터셉터 클래스들이 포함됩니다.
    - **model**: 사용자 인증 정보 및 관련 모델 객체들이 포함됩니다.
    - **util**: 인증 및 권한 관련 유틸리티 클래스들이 포함됩니다.

- `stempo-common`: 여러 모듈에서 공통으로 사용하는 클래스들을 포함하는 모듈입니다.
    - **config**: 프로젝트 전반에 걸쳐 공통적으로 사용되는 설정 클래스들이 포함됩니다.
    - **dto**: 여러 모듈에서 공통으로 사용하는 DTO 객체들이 포함됩니다.
    - **exception**: 프로젝트 전반에서 발생하는 예외를 처리하는 클래스들이 포함됩니다.
    - **util**:  프로젝트 전반에 걸쳐 공통적으로 사용되는 유틸리티 기능을 제공하는 클래스들이 포함됩니다.

- `stempo-domain`: 비즈니스 로직과 관련된 핵심 도메인 모델과 영속성 로직을 담당하는 모듈입니다.
    - **model**: 비즈니스 로직에 필요한 핵심 도메인 객체들이 포함됩니다.
      - **repository**: 도메인 객체를 데이터베이스에 저장하거나 불러오는 역할을 담당하는 리포지토리 인터페이스가 포함됩니다.

- `stempo-infrastructure`: 데이터베이스와 같은 외부 시스템과 상호작용하는 모듈입니다.
    - **config**: JPA 및 데이터베이스 관련 설정 클래스들이 포함됩니다.
    - **entity**: 데이터베이스 테이블과 매핑되는 엔티티 클래스들이 포함됩니다.
    - **exception**: 데이터 저장 또는 조회 과정에서 발생할 수 있는 예외 처리 클래스들이 포함됩니다.
    - **mapper**: 엔티티 객체와 도메인 객체 간의 변환을 처리하는 클래스들이 포함됩니다.
    - **repository**: 영속성 계층의 리포지토리 인터페이스를 구현하여 데이터베이스와 상호작용하는 클래스들이 포함됩니다.
    - **util**: 데이터베이스와 관련된 유틸리티 클래스들이 포함됩니다.

## 라이선스
이 프로젝트는 GNU 일반 공중 사용 허가서(GPL) v3.0에 따라 라이선스가 부여됩니다. 자세한 내용은 [LICENSE](https://github.com/KKKK-Stempo/stempo-server?tab=GPL-3.0-1-ov-file#readme) 파일을 확인하세요.

## 기여자
<a href="https://github.com/KKKK-Stempo/stempo-server/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=KKKK-Stempo/stempo-server" />
</a>
