# 북촌(BookVillage) — Spring Boot 백엔드 & 프로젝트 개요

> **⚠️ 경고: 본 프로젝트는 웹/모바일/클라우드 모의해킹 실습을 목적으로 취약점을 의도적으로 삽입한 학습용 애플리케이션입니다.**
> 실제 결제, 실제 개인정보, 실제 AWS 계정과는 무관하며, 프로덕션 환경에 그대로 배포해서는 안 됩니다. 코드 내 하드코딩된 자격증명·시크릿은 모두 훈련 목적의 더미 값입니다.

`오늘모해` 팀(GitHub Organization: [SecuR00T](https://github.com/SecuR00T))이 실전형 침투 테스트 역량 강화를 위해 자체 기획·구축한 온라인 서점 서비스 **북촌(BookVillage)**의 **백엔드(Spring Boot API)** 저장소이자, 프로젝트 전체 개요를 다루는 문서입니다. 이 프로젝트는 3개의 저장소로 분리되어 있습니다.

| 저장소 | 역할 |
| --- | --- |
| **[book-back](https://github.com/SecuR00T/book-back)** (본 저장소) | Spring Boot 백엔드 API + 프로젝트 전체 개요 |
| [book-front](https://github.com/SecuR00T/book-front) | React 프론트엔드 (스토어프론트 + 관리자 페이지) |
| [book-android](https://github.com/SecuR00T/book-android) | Android WebView 하이브리드 앱 |

## 목차

- [1. 프로젝트 개요](#1-프로젝트-개요)
- [2. 서비스 소개](#2-서비스-소개)
- [3. 시스템 아키텍처](#3-시스템-아키텍처)
- [4. 기술 스택](#4-기술-스택)
- [5. 백엔드 아키텍처 및 저장소 구조](#5-백엔드-아키텍처-및-저장소-구조)
- [6. API 명세](#6-api-명세)
- [7. 데이터베이스](#7-데이터베이스)
- [8. 주요 취약점](#8-주요-취약점)
- [9. AWS 배포 아키텍처 (과거 운영 기준)](#9-aws-배포-아키텍처-과거-운영-기준)
- [10. 빌드 및 실행 방법](#10-빌드-및-실행-방법)
- [11. 모의해킹 시나리오](#11-모의해킹-시나리오)
- [12. 수행 팀](#12-수행-팀)
- [13. 라이선스 및 면책 조항](#13-라이선스-및-면책-조항)

---

## 1. 프로젝트 개요

| 항목 | 내용 |
| --- | --- |
| 팀명 | 오늘모해 |
| 프로젝트명 | 북촌(Bukchon) / BookVillage 웹·모바일 모의해킹 프로젝트 |
| 목적 | 다양한 취약점을 나열하는 것이 아니라, 실제 공격자 관점에서 취약점을 조합해 침투 성공 가능성과 피해 영향을 입증하는 실전형 모의해킹 역량 강화 |
| 수행 범위 | Web(React + Spring Boot API), Mobile(Android/Kotlin), Cloud(AWS) |
| 수행 기간 | 서비스 구축 2026.02.23 ~ 03.13 / 취약점 분석 2026.03.03 ~ 03.20 / 모의침투 2026.03.05 ~ 03.24 |

이 프로젝트는 두 개의 산출물로 구성됩니다.

1. **진단 대상 서비스**: 실제 서비스처럼 동작하는 온라인 서점 "북촌"을 웹·모바일·클라우드 전 계층에 걸쳐 직접 구축하고, SQL Injection, XSS, CSRF, SSRF, 파일 업로드 등 대표적인 공격 벡터를 의도적으로 심어둠.
2. **모의침투 시나리오**: 심어둔 취약점들을 실제로 연쇄시켜 자원 탈취(모바일 채굴 봇넷), 정보 탈취(APT/결제정보), 인프라 장악(랜섬웨어)까지 이어지는 3가지 시나리오를 기획·수행. 상세 내용은 [모의해킹 시나리오 문서](PENTEST_SCENARIOS.md)를 참고하세요.

> 이 세 저장소 외에, 시나리오 재현을 위해 별도로 작성한 공격자 도구(`collect-main` — Java Agent 기반 결제정보 탈취 도구)가 있으나 SecuR00T 조직의 공개 저장소는 아닙니다. 자세한 내용은 [§11 모의해킹 시나리오](#11-모의해킹-시나리오)에서 설명합니다.

## 2. 서비스 소개

북촌(BookVillage)은 일반적인 온라인 서점 서비스입니다.

- **일반 사용자(bookvillage 웹/앱)**: 회원가입/로그인, 도서 검색·상세·미리보기, 리뷰 작성, 장바구니·결제(체크아웃), 주문 내역/배송 조회, 마이페이지(포인트, 위시리스트, 최근 본 상품), 1:1 문의 및 고객센터, 회원 게시판, 이벤트/공지사항 확인.
- **관리자(admin 웹)**: 상품(도서) 관리, 주문/결제 관리, 고객 관리, 재고 관리, 쿠폰 관리, 리뷰 관리, 공지사항/FAQ 관리(첨부파일 업로드 포함), 팝업 관리(앱/웹 노출용 업데이트·광고 팝업), 1:1 문의 답변, 접속 로그 모니터링.
- **모바일([book-android](https://github.com/SecuR00T/book-android))**: 위 웹 서비스를 WebView로 감싼 하이브리드 앱으로, 딥링크를 통한 주문 상세/마이페이지 진입과 앱 전용 업데이트 팝업 기능을 제공.

## 3. 시스템 아키텍처

```
사용자
  │  HTTPS/HTTP
  ▼
[Internet Gateway] ── Public Subnet ── [ALB] ─┬─ [Bastion Host] (SSH 관리 전용, Public)
                                               │
                                               ▼
                                        Private Subnet
                                   ┌───────────────────────┐
                                   │  EC2 (Docker)         │
                                   │  ┌─────────────────┐  │
                                   │  │ Nginx            │  │   /            → bookvillage 정적 파일
                                   │  │ (book-front)     │  │   /admin/       → admin 정적 파일
                                   │  │                  │──┼─▶ /api/         → Spring Boot(8080)
                                   │  │                  │  │   /admin/api/   → Spring Boot(8080)
                                   │  └─────────────────┘  │
                                   │  ┌─────────────────┐  │
                                   │  │ Spring Boot API  │──┼─▶ RDS(MySQL), S3
                                   │  │ (book-back)      │  │
                                   │  └─────────────────┘  │
                                   └───────────────────────┘
                                               │
                                     NAT Gateway ─▶ 외부(패키지 업데이트 등)
                                     S3 Gateway Endpoint ─▶ S3 (내부 통신)

book-android(모바일 앱)는 별도 배포 없이 위 ALB 엔드포인트를 백엔드로 직접 호출
```

- **네트워크**: VPC(ap-northeast-2), Public Subnet(ALB, NAT GW, Bastion Host) / Private Subnet(App EC2, RDS) 분리
- **애플리케이션**: 하나의 EC2 인스턴스 위에서 Docker로 Nginx(리버스 프록시 겸 정적 파일 서버) + Spring Boot API 컨테이너를 함께 구동
- **데이터**: RDS(MySQL)를 주 데이터 저장소로, S3를 이미지/첨부파일 스토리지로 사용

## 4. 기술 스택

| 구분 | 기술 |
| --- | --- |
| 프론트엔드 | React 18, JavaScript(JSX), React Router v6, TanStack Query, Vite |
| 백엔드 | Java 11, Spring Boot 2.6.5, Spring Data JPA, Spring JDBC, Spring Security, Flyway |
| 데이터베이스 | MySQL 8.x (RDS) |
| 스토리지 | AWS S3 (도서 이미지, 첨부파일) |
| 모바일 | Android(Kotlin), WebView 기반 하이브리드 |
| 인프라 | AWS(VPC, EC2, ALB, RDS, S3, Bastion Host, NAT Gateway), Docker, Nginx |
| CI/CD | GitHub + Self-hosted Runner |

## 5. 백엔드 아키텍처 및 저장소 구조

- **엔트리 포인트**: `src/main/java/com/bookvillage/backend/BookVillageMockApplication.java`
- **패키지 구조**(`com.bookvillage.backend`):

  | 패키지 | 역할 |
  | --- | --- |
  | `config` | `SecurityConfig`, `AuthTokenFilter`, `SessionTokenFilter`, `ServerInfoFilter`, `TomcatConfig`, `S3Config`, `WebMvcConfig` |
  | `controller` | REST 컨트롤러 33개(일반 API + `/admin/api/**` 관리자 API) |
  | `service` | 비즈니스 로직. `InMemoryDataStore`(JdbcTemplate 기반 관리자 대시보드 데이터 계층)와 JPA `service`가 공존 |
  | `entity` | JPA 엔티티: `User`, `Book`, `Order`, `OrderItem`, `Review`, `Coupon`, `CustomerService`, `BoardPost`, `BoardComment`, `BoardAttachment`, `AccessLog` |
  | `model` | 관리자 대시보드용 POJO(JdbcTemplate 직접 사용): `Customer`, `Order`, `Product`, `Review`, `Coupon`, `CustomerServiceInquiry`, `InventoryProduct` 등 |
  | `repository` | Spring Data JPA 레포지토리 |
  | `dto` / `request` / `response` | API 요청·응답 페이로드 |
  | `security` | `UserPrincipal`(JPA `User`를 `UserDetails`로 래핑) |
  | `util` | `Sha1PasswordEncoder`(커스텀 무솔트 SHA-1 인코더) |
  | `common` | `ApiException`, `RequestIpResolver`, `PageResponse`, `SuccessResponse`, `AdminGlobalExceptionHandler` |

> **설계상 특이점**: 동일한 도메인(주문, 리뷰, 쿠폰, 문의)에 대해 JPA `entity`/`repository`/`service`(고객용)와 `model` + `InMemoryDataStore`(관리자 대시보드용, 원시 `JdbcTemplate` 사용)가 이중으로 존재합니다. 관리자 API 다수가 파라미터화되지 않은 SQL을 직접 다루는 이유이기도 합니다(§8 참고).

```
book-back/  (저장소 루트)
├── src/main/java/com/bookvillage/backend/
│   ├── config/        보안·인프라 설정
│   ├── controller/    REST 컨트롤러
│   ├── service/       비즈니스 로직 (InMemoryDataStore 포함)
│   ├── entity/        JPA 엔티티
│   ├── model/         관리자 대시보드용 POJO
│   ├── repository/    Spring Data JPA 레포지토리
│   ├── dto/ request/ response/
│   ├── security/      UserPrincipal
│   ├── util/          Sha1PasswordEncoder
│   └── common/        ApiException, RequestIpResolver 등
├── src/main/resources/
│   ├── application.yml           기본/로컬 프로필
│   ├── application-prod.yml      운영 프로필
│   ├── schema.sql                (참고용, 실제 스키마는 Flyway가 관리)
│   ├── db/migration/             Flyway 마이그레이션 V1~V19
│   └── static/                   의도적으로 노출된 정적 파일(.env.bak, backup.sql, test.jsp 등 — §8 참고)
├── src/test/java/.../ApiIntegrationTest.java
├── src/test/resources/application-test.yml   H2 기반 테스트 프로필
├── deploy/bookvillage-backend.service        systemd 유닛 파일
├── Dockerfile, docker-compose-local.yml, docker-compose-ope.yml
├── .env.example, .env.prod.example
├── pom.xml
└── PENTEST_SCENARIOS.md   모의해킹 시나리오 상세 문서
```

## 6. API 명세

### 6.1 인증 (`AuthController`, `/api/auth/**`, 대부분 `permitAll`)

| Method | Path | 설명 |
| --- | --- | --- |
| POST | `/api/auth/register` | 회원가입 |
| POST | `/api/auth/login` | 로그인 |
| POST | `/api/auth/find-id` | 아이디 찾기 |
| POST | `/api/auth/password-reset/request` `/confirm` | 비밀번호 재설정 |
| GET | `/api/auth/address-search` | 주소 검색 |
| POST | `/api/auth/logout` | 로그아웃 |
| GET | `/api/auth/cookie-login` | 세션 쿠키 + IP 이중 검증 기반 관리자 로그인 |

### 6.2 도서/카탈로그 (`BookController`, `/api/books/**`, `permitAll`)

| Method | Path | 설명 |
| --- | --- | --- |
| GET | `/search`, `/categories`, `/{bookId}`, `/{bookId}/shipping-info` | 검색/카테고리/상세/배송정보 |
| GET | `/{bookId}/preview?filePath=` | 도서 미리보기 |
| GET | `/{bookId}/image-proxy?url=` | 이미지 프록시(서버가 임의 URL을 fetch — SSRF) |

### 6.3 리뷰 / 장바구니 / 주문

| Method | Path | 설명 |
| --- | --- | --- |
| GET/POST | `/api/books/{bookId}/reviews` | 리뷰 조회/작성 |
| POST | `/api/reviews/{id}/upload` `/like` `/report`, DELETE `/api/reviews/{id}` | 리뷰 이미지 업로드/좋아요/신고/삭제 |
| `/api/cart` | `CartController` | 장바구니 CRUD |
| POST | `/api/orders/checkout` | 결제(체크아웃) |
| GET | `/api/orders/lookup` | 비회원 주문 조회(`permitAll`) |
| GET | `/api/orders/{id}/tracking?trackingUrl=` | 배송 추적(SSRF 싱크) |
| PUT | `/api/orders/{id}/status` | 주문 상태 변경(상태 전이 검증 없음) |

### 6.4 마이페이지 / 사용자 / 프로필

`MypageController`(`/api/mypage/**`): 최근 본 상품, 위시리스트, 포인트 충전(`POST /points/charge`, 금액 검증 없음), 주문 취소/반품/교환, 즐겨찾기 게시글, 리뷰 관리.

`UserController`(`/api/users/**`): `GET/PUT /{userId}`, `GET /{userId}/orders`, `PUT /me/password`, `DELETE /me`, `DELETE /delete?user_id=`(소유자 검증 없음), `GET /me`(`remember_uid` 쿠키 신뢰), `ProfileController`: `GET /profile?user_id=`(IDOR).

### 6.5 게시판 / 고객센터

| Method | Path | 설명 |
| --- | --- | --- |
| `/api/board/**` | `BoardController` | 게시글/댓글/첨부파일 |
| `/api/customer-service` | `CustomerServiceController` | 1:1 문의 작성/조회 |
| `/api/notices`, `/{id}`, `/latest-urgent` | `SupportController` | 공지사항 |
| `/api/faqs` | `SupportController` | FAQ |
| POST `/api/customer-service/{id}/attachments` | `SupportController` | 문의 첨부파일 |
| `/api/community/attachments/**` | `CommunityAttachmentController` | 게시판 첨부파일 서빙 |

### 6.6 관리자 API (`/admin/api/**`)

> Spring Security가 아닌 커스텀 `AuthTokenFilter`로만 보호되며, 사실상 접근 제어가 유명무실합니다(§8 참고).

| 컨트롤러 | 주요 경로 | 설명 |
| --- | --- | --- |
| `AdminAuthController` | `/admin/api/auth/login`, `/session-login`, `/change-password` | 관리자 인증 |
| `AdminOrderController` | `/admin/api/orders/**` | 주문 관리 |
| `AdminPaymentController` | `/admin/api/payments`, `/{id}`, `/{id}/cancel` | 결제 관리(카드 데이터 노출) |
| `AdminReviewController` | `/admin/api/reviews/**` | 리뷰 관리 |
| `AdminCustomerServiceController` | `/admin/api/customer-service/{id}/reply` | 1:1 문의 답변 |
| `MonitoringController` | `/admin/api/monitoring/access-logs` | 접속 로그 조회 |
| `ProductController` | `/admin/api/products`, `/upload-image` | 상품 관리 |
| `CouponController` | `/admin/api/coupons/**` | 쿠폰 관리 |
| `InventoryController` | `/admin/api/inventory/**` | 재고 관리 |
| `CustomerController` | `/admin/api/customers`, `/member-access` | 고객 관리 |
| `PopupController` | `/admin/api/popups` (CRUD), `/upload-image` | 팝업 관리 |
| `NoticeController` | `/admin/api/notices` (파일 업로드 포함) | 공지사항 관리 |
| `MediaController` | `/admin/api/media/{filename}` | 미디어 서빙 |

### 6.7 공개 팝업 / 진단·실습용 엔드포인트

| Method | Path | 설명 |
| --- | --- | --- |
| GET | `/api/popups/active?deviceType=` | 활성 팝업 조회(`permitAll`) |
| POST | `/api/diagnostics/ping` | Host 핑(명령어 삽입, 인증 불필요) |
| GET | `/api/diagnostics/trace`, `/server-info` | 헤더 에코/서버 정보 노출 |
| POST | `/api/integration/link-preview`, GET `/api/integration/ping?target=` | 링크 미리보기(SSRF)/핑(명령어 삽입) |
| POST | `/api/link-preview` | 링크 미리보기(SSRF, 인증 불필요) |
| GET | `/api/greet?name=` | SpEL 인젝션(RCE, 인증 불필요) |
| GET | `/api/search?q=` | 반사형 XSS(HTML 응답) |
| GET | `/api/download?file=` | 경로 순회 |
| POST | `/api/upload` | 무제한 파일 업로드(인증 불필요) |
| GET | `/api/files?dir=` | 임의 디렉터리 리스팅 |
| GET | `/download/{filename}` | `classpath:/static/`에서 파일 서빙 |
| — | `/swagger-ui.html`, `/v3/api-docs/**` | OpenAPI 문서(`permitAll`) |

## 7. 데이터베이스

### 7.1 접속 설정

`application.yml`(기본/로컬) 및 `application-prod.yml`(운영 프로필)에서 사용하는 주요 프로퍼티입니다.

| 프로퍼티 | 설명 | 기본값(로컬) |
| --- | --- | --- |
| `spring.datasource.url` / `username` / `password` | MySQL 접속 정보 | `jdbc:mysql://localhost:3407/bookvillage_mock`, `root`/`1234` |
| `spring.jpa.hibernate.ddl-auto` | 스키마 자동 생성 여부 | `none`(Flyway가 관리) |
| `spring.flyway.*` | Flyway 설정 | — |
| `server.port` | 서버 포트 | `8080` |
| `server.error.include-stacktrace` / `include-exception` / `include-binding-errors` | 에러 응답 상세도 | `always`(운영에서도 스택 트레이스 노출) |
| `file.storage-path` / `file.base-path` / `file.lab-upload-path` / `file.product-image-path` | 파일 저장 경로 | `./uploads` 계열 |
| `cloud.aws.credentials.access-key` / `secret-key` | AWS 자격증명 | 미설정 시 `local-dummy-key`/`local-dummy-secret`로 폴백 |
| `cloud.aws.region.static`, `cloud.aws.s3.bucket` | S3 리전/버킷 | 미설정 시 `local-dummy-bucket` |
| `springdoc.api-docs.path`, `springdoc.swagger-ui.path` | API 문서 경로 | — |
| `jwt.expiration` | 토큰 만료(초) | `315360000`(약 10년) |

운영 프로필(`application-prod.yml`)의 DB 접속 fallback 값(`admin_book`/`village123!`)과 RDS 엔드포인트 등은 §8의 하드코딩 자격증명 항목을 참고하세요.

### 7.2 필수 환경변수 (운영 프로필 기준)

`docker-compose-ope.yml`, `.env.prod.example` 참고:

| 변수 | 설명 |
| --- | --- |
| `SPRING_PROFILES_ACTIVE` | `prod` |
| `SPRING_DATASOURCE_URL` / `USERNAME` / `PASSWORD` | MySQL 접속 정보 |
| `SPRING_FLYWAY_REPAIR_ON_MIGRATE` | Flyway 복구 옵션 |
| `AWS_ACCESS_KEY` / `AWS_SECRET_KEY` / `AWS_REGION` / `AWS_S3_BUCKET` | S3 연동 |

### 7.3 스키마 개요

Flyway 마이그레이션(`db/migration/V1~V19`) 기준 주요 테이블:

- **회원/인증**: `users`(SHA-1 `password`, `role`, `status`), `user_sessions`(`session_key`, `login_ip`), `password_reset_tokens`, `id_lookup_logs`
- **상품/주문**: `books`, `orders`/`order_items`, `cart_items`, `payment_transactions`(카드 마스킹 정보), `point_histories`
- **리뷰/커뮤니티**: `reviews`, `review_likes`, `review_reports`, `board_posts`/`board_comments`/`board_attachments`
- **고객센터**: `customer_service`(1:1 문의)/`customer_service_attachments`, `notices`(`link_url`/`urgent` 포함), `faqs`
- **운영**: `coupons`, `access_logs`, `device_fcm_tokens`, `popups`(`popup_type`/`image_url`)
- **훈련용 메타데이터**: `security_lab_requirements`/`security_lab_events`(REQ-COM-001~048 취약점 카탈로그 + 실습 이벤트 로그), `lab_uploaded_files`/`lab_execution_attempts`

JPA 연관관계는 대부분 `@ManyToOne` 대신 순수 `Long` FK 컬럼으로 관리되며(예외: `Order`↔`OrderItem`은 cascade ALL), 관리자 기능 다수는 JPA를 우회해 `JdbcTemplate`으로 직접 조인 쿼리를 실행합니다.

## 8. 주요 취약점

> 전체 48개 항목의 원본 카탈로그는 `src/main/resources/db/migration/V2__seed_data.sql`의 `security_lab_requirements` 시드(REQ-COM-001~048)를 참고하세요. 프론트엔드/모바일 취약점은 [book-front README](https://github.com/SecuR00T/book-front/blob/main/README.md#6-주요-취약점), [book-android README](https://github.com/SecuR00T/book-android/blob/main/README.md#5-주요-취약점)를 참고하세요. 각 취약점이 실제 공격 시나리오로 이어지는 흐름은 [PENTEST_SCENARIOS.md](PENTEST_SCENARIOS.md)에서 다룹니다.

| 분류 | 위치 | 설명 |
| --- | --- | --- |
| 접근 제어 누락 (Broken Access Control) | `SecurityConfig.java:50` | `/admin/api/**` 전체가 `permitAll()`로 열려 있고, 유일한 게이트인 `AuthTokenFilter`(`AuthTokenFilter.java:19-83`)도 `Bearer `로 시작하는 임의 토큰이면 통과시킴. `AuthTokenFilter.java:19`의 하드코딩 우회 토큰 `BV-BYPASS-KEY-2024`로도 전체 관리자 API 접근 가능 |
| SQL Injection | `AdminAuthController.java:135-151`(관리자 로그인 조회), `AuthService.java:33-93`(회원가입/로그인) | 문자열 결합으로 쿼리 생성 |
| Stored/반사형 XSS, SSTI | `SearchController.java:19-24`(반사형), `CustomerServiceService`/`AdminCustomerServiceController`(1:1 문의 저장형), `ReviewService.java:197-221`(SpEL 템플릿 SSTI) | 사용자 입력에 대한 출력 인코딩 전무 |
| SpEL 기반 RCE | `GreetingController.java:24-41` | `/api/greet?name=` 값을 SpEL로 그대로 평가 |
| SSRF | `BookController.java:101-146`(책 미리보기 이미지 프록시), `LinkPreviewController.java:26-82`, `LearningFeatureService.fetchTitleFromUrl()` | `127.0.0.1`/`169.254.169.254` 문자열만 차단, 10진수 IP 등으로 우회 가능(`2852039166` = `169.254.169.254`) |
| 파일 업로드(웹쉘) | `NoticeController.java:12-85`(66행 확장자 블랙리스트에 `.jspx` 누락), `FileController.java:75-111`(인증·검증 전혀 없음) | 업로드 디렉터리가 내장 Tomcat 웹 루트에 마운트되어(`TomcatConfig.java:27-49`) JSP 실행 가능 |
| 경로 순회(Path Traversal) | `FileService.java:247-256`, `FileController.java:113-126` | 사용자 입력 경로를 정규화만 하고 베이스 경로 포함 여부 미검증 |
| 명령어 삽입(Command Injection) | `DiagnosticsController.java:24-63`(인증 불필요), `IntegrationController.java:36-56` | `ping` 대상 호스트를 쉘 명령에 그대로 결합 |
| IDOR | `UserController.java`(`/api/users/delete?user_id=`), `ProfileController.java`(`/api/profile?user_id=`) | 소유자 검증 없이 임의 사용자 ID로 접근/삭제 가능 |
| 세션/쿠키 보안 미흡 | `AuthController.java:44-57`(HttpOnly 미설정), `SessionTokenFilter.java:33-105`(IP 바인딩·만료 없음), 로그아웃 시 서버측 세션 미무효화 | 탈취된 세션 토큰이 로그아웃 후에도 유효 |
| X-Forwarded-For 신뢰 | `RequestIpResolver.java:9-31`, `AuthController.java:133-190` | 관리자 접근 시 IP 검증을 프록시 헤더 값으로 우회 가능 |
| 하드코딩된 자격증명 | `application-prod.yml:6-7`(`admin_book`/`village123!`), `static/.env.bak`, `static/backup.sql`, `static/test.jsp` | DB 계정, AWS 키, JWT 시크릿, 관리자 기본 비밀번호가 소스/정적 파일에 평문 노출(정적 파일은 웹에서 직접 다운로드 가능) |
| 취약한 비밀번호 저장 | `util/Sha1PasswordEncoder.java` | 무솔트 SHA-1 해시 사용 |
| CORS/CSRF 전면 해제 | `SecurityConfig.java:45,93-102` | `allowedOrigins("*")` + CSRF 비활성화 |
| 정보 노출 | `GlobalExceptionHandler.java:41-63`, `ServerInfoFilter.java`, `application.yml:40-44` | 스택 트레이스, DB URL, 서버 배너를 응답에 그대로 포함 |

## 9. AWS 배포 아키텍처 (과거 운영 기준)

> **현재 AWS 인프라는 프로젝트 종료 후 만료되어 더 이상 운영되지 않으며, 클라우드에 배포된 서비스는 존재하지 않습니다.** 아래 내용은 프로젝트 수행 당시 실제로 구성했던 배포 환경에 대한 기록입니다.

- **리전/네트워크**: `ap-northeast-2`(서울), VPC CIDR `10.0.0.0/16`. Public Subnet에는 ALB·NAT Gateway·Bastion Host를, Private Subnet에는 애플리케이션 EC2와 RDS를 배치해 핵심 자산을 외부 노출로부터 격리.
- **보안 그룹**: ALB SG(80/443만 허용) → EC2 SG(ALB로부터 8080, Bastion으로부터 SSH 22만 허용) → RDS SG(EC2로부터 MySQL 3306만 허용)의 계층적 최소 권한 구조.
- **서버**: EC2 t3.medium(Ubuntu 22.04), Java 11 + Nginx 리버스 프록시, Jar를 systemd 서비스로 상시 구동(`deploy/bookvillage-backend.service`).
- **Bastion Host**: Public Subnet에 t3.small 인스턴스를 별도로 두어 관리자 SSH 접근을 이 경로로만 허용.
- **로드밸런서**: ALB가 80 포트로 요청을 받아 EC2의 Nginx(80)로 전달, Nginx가 다시 내부 앱(8080)으로 프록시하는 2단계 구조.
- **데이터베이스**: RDS MySQL 8.x, Multi-AZ 구성으로 장애 시 자동 전환.
- **스토리지**: S3에 도서 이미지/첨부파일 저장, 퍼블릭 액세스 전면 차단 + Gateway Endpoint를 통해 NAT Gateway를 거치지 않고 내부 백본으로 직접 통신.
- **CI/CD**: GitHub + Self-hosted Runner 기반 빌드/배포 자동화, Docker로 애플리케이션 실행 환경 표준화.

## 10. 빌드 및 실행 방법

요구사항: JDK 11, Maven(동봉된 `mvnw` 사용 가능), MySQL 8.x(로컬 컨테이너 제공)

```bash
# 1) 로컬 MySQL 컨테이너만 기동 (호스트 포트 3407)
docker compose -f docker-compose-local.yml up -d

# 2) 애플리케이션 실행 (기본 프로필: application.yml, root/1234로 접속)
./mvnw spring-boot:run

# 또는 빌드 후 jar 직접 실행
./mvnw clean package
java -jar target/bookvillage-mock-1.0.0.jar
```

- 운영 프로필로 띄우려면 `SPRING_PROFILES_ACTIVE=prod`와 함께 §7.2의 환경변수를 설정하세요(`docker-compose-ope.yml` 참고).
- Docker 이미지 빌드: `docker build -t bookvillage-backend .` (멀티스테이지, JDK11 빌더 → JRE11 런타임, 비루트 유저로 실행, `EXPOSE 8080`)
- `deploy/bookvillage-backend.service`는 systemd로 배포했을 때 사용한 유닛 파일 예시입니다(운영 당시 EC2에 적용, 현재는 참고용).
- 테스트 실행: `./mvnw test` (H2 인메모리 DB 기반 `application-test.yml` 프로필 사용, `src/test/java/.../ApiIntegrationTest.java`에 회원가입/로그인/검색/체크아웃/주문조회/관리자 접근제어 통합 테스트 포함)
- 프론트엔드/모바일 앱 빌드·실행 방법은 각 저장소의 README를 참고하세요: [book-front README](https://github.com/SecuR00T/book-front/blob/main/README.md#7-빌드-및-실행-방법), [book-android README](https://github.com/SecuR00T/book-android/blob/main/README.md#7-빌드-및-실행-방법)

## 11. 모의해킹 시나리오

이 프로젝트에서 실제로 수행한 3가지 모의침투 시나리오(모바일 채굴 봇넷 / APT 결제정보 탈취 / 웹·클라우드 랜섬웨어)의 상세 공격 흐름, 사용 기법, 근거 코드 위치는 별도 문서로 정리되어 있으며, 세 저장소(`book-back`, `book-front`, `book-android`) 모두에 동일한 내용으로 포함되어 있습니다.

**👉 [PENTEST_SCENARIOS.md — 모의해킹 시나리오 상세 문서](PENTEST_SCENARIOS.md)**

| 시나리오 | 한 줄 요약 |
| --- | --- |
| 1. 모바일 앱 피싱 기반 모바일 채굴 봇넷 | 앱 무결성 검증 부재 + 클라이언트 권한 검증 우회 → 관리자 팝업 API 악용 → 악성 APK 유포 → 저자원 분산 채굴 봇넷 구축 |
| 2. APT 기반 개인정보 및 금융정보 지속 탈취 | 1:1 문의 Stored XSS → 관리자 세션 탈취(쿠키 재사용 + X-Forwarded-For 우회) → 파일 업로드로 웹쉘 → DB 덤프 지속 유출 + Java Agent로 결제정보 실시간 탈취 |
| 3. 웹·클라우드 공격 체인 기반 랜섬웨어 | 책 미리보기 SSRF → IMDS 자격증명 탈취 → S3 PEM 키 확보 → Bastion Host 경유 내부망 장악 → DB 암호화 및 프론트엔드 디페이스 |

> `collect-main`(시나리오 2용 Java Agent, `OrderController.checkout()`을 Javassist로 바이트코드 패치)은 SecuR00T 조직의 공개 저장소가 아니라 팀이 별도로 관리한 공격자 도구입니다. 동작 방식은 [PENTEST_SCENARIOS.md §4](PENTEST_SCENARIOS.md#4-시나리오-2--apt-기반-개인정보-및-금융정보-지속-탈취)에서 설명합니다.

## 12. 수행 팀

| 이름 | 직함 | 역할 |
| --- | --- | --- |
| 배정연 | 팀장 | 모바일 채굴 봇넷 시나리오 |
| 박진용 | 팀원 | 모바일 채굴 봇넷 시나리오 |
| 신동하 | 팀원 | 결제정보 탈취(APT) 시나리오 |
| 최지혜 | 팀원 | 결제정보 탈취(APT) 시나리오 |
| 임평화 | 팀원 | 랜섬웨어 시나리오 |
| 이예원 | 팀원 | 랜섬웨어 시나리오 |

## 13. 라이선스 및 면책 조항

본 프로젝트(`book-back`, `book-front`, `book-android` 및 부속 도구)는 교육 및 보안 연구 목적으로만 제공됩니다. 포함된 취약점, 하드코딩된 자격증명, 공격 도구(`collect-main`, `book-android`의 `malicious-payload/` 등)를 승인되지 않은 시스템에 대해 사용하는 것은 금지되며, 이로 인해 발생하는 모든 책임은 사용자 본인에게 있습니다. 본 코드를 실제 운영 환경에 그대로 배포하지 마십시오.
