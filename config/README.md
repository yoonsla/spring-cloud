# Spring Cloud Config 란?

Spring Cloud Config는 분산 시스템에서 외부화된 설정 정보를 서버 및 클라이언트에 제공하는 시스템이다.\
Config Server는 외부에서 모든 환경에 대한 정보들을 관리하는 중앙 서버이다.\
기본적으로 설정 정보 저장을 위해 git을 사용하도록 설계되어 손쉽게 외부 도구들로 접근이 가능하며, 버전 관리도 용이하다.

![img.png](img.png)

Spring Cloud config는 설정 서버와 설정 클라이언트로 나뉜다.

- Sping Cloud Config Server(설정 서버): 버전 관리 레포지토리로 백업된 중앙 집중식 구성 노출을 지원한다.
- Sping Cloud Config Client(설정 클라이언트): 애플리케이션이 설정 서버에 연결하도록 지원한다.

---

## 특징

### Spring Cloud Config Server
- HTTP, 외부 구성을 위한 리소스 기반 API(이름-값 쌍 또는 동등한 YAML 콘텐츠)
- 속성 값 암호화 및 암호 해독(대칭 또는 비대칭)
- ``@EnableConfigServer``를 사용하여 쉽게 Spring Boot 애플리케이션에 포함할 수 있다.

### Spring Cloud Config Client (Spring 애플리케이션용)
- Config Server에 바인딩하고 Environment원격 속성 소스로 Spring을 초기화한다.
- 속성 값 암호화 및 암호 해독(대칭 또는 비대칭)

---

## 장점

Spring Cloud Config는 여러 서비스들의 설정 파일을 외부로 분리해, 하나의 중앙 설정 저장소처럼 관리할 수 있도록 해주며 특정 설정 값이 변경 시 각각의 서비스를 재기동 없이 적용이 가능하도록 도와준다.
- 여러 서버의 설정 파일을 중앙 서버에서 관리할 수 있다.
- 서버를 재배포 하지 않고 설정 파일의 변경사항을 반영할 수 있다.

## 단점

- Git 서버 또는 설정 서버에 의한 장애가 전파될 수 있다.
- 우선순위에 의해 설정 정보가 덮어씌워질 수 있다.

---

## Spring Cloud Config 설정 파일 우선 순위

설정 파일은 크게 다음의 위치에 존재할 수 있으며 다음의 순서로 읽힌다.\
나중에 잃히는 파일의 우선순위가 높다.

- 프로젝트의 application.yml
- 설정 저장소의 application.yml
- 프로젝트의 application-{profile}.yml
- 설정 저장소의 {application name}/{application name}-{profile}.yml

> 동일한 값을 지니는 설정 정보가 있다면 덮어씌워지므로 주의해야한다.

ex)
``hello`` 라는 이름의 애플리케이션에 local 프로파일인 환경변수가 로컬의 application.yml, application-local.yml로 있고\
설정 저장소에 application.yml, hello/hello-local.yml이 있다면 아래의 순서로 읽힌다.

1. 프로젝트의 application.yml
2. 설정 저장소의 application.yml
3. 프로젝트의 application-local.yml
4. 설정 저장소의 hello/hello-local.yml

=> 최종적으로 hello/hello-local.yml 이 읽힌다.

---

## Spring Cloud Config Server 구축

### 설정 파일 저장소 구축

먼저 설정 파일들을 저장할 git repository 를 만든다.\
중요한 건 파일 이름인데 ``{앱 이름}-{프로파일}.yml`` 구조로 작성한다.\

- sy-jp-dev.yml
- sy-kr-dev.yml

### 설정 서버 구축

```
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-actuator'
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.cloud:spring-cloud-config-server'
}
```

설정 서버 구성을 위해서는 spring-boot-starter-web에 spring-cloud-config-server 의존성이 필요하다.


application.yml
```
server:
  port: 8888
spring:
  application:
    name: config
  cloud:
    config:
      server:
        git:
          uri: https://github.com/yoonsla/spring-cloud
          search-paths: config/config-file/**
          default-label: main
```

- ``uri`` : 설정파일이 있는 깃 주소
- ``search-paths`` : 설정 파일들을 찾을 경로
- ``default-label`` : 깃 주소의 브랜치 이름

```
@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestCloudConfigServerApplication.class, args);
    }
}
```

메인 클래스에 ``@EnableConfigServer`` 을 붙여준다.

### 설정 서버 실행 및 확인

서버를 실행하면 설정 파일 저장소를 클론하고 설정 정보를 읽어오는데, spring cloud config server가 갖는 endpoint는 다음과 같다.

- /{application}/{profile}[/{label}]
- /{application}-{profile}.yml
- /{label}/{application}-{profile}.yml
- /{application}-{profile}.properties
- /{label}/{application}-{profile}.properties

=> localhost:8888/{앱이름}/{프로파일}로 접근하면 정상적으로 동작함을 확인할 수 있다.\
=> 만약 private repository 라면 SSL 설정이 추가로 필요하다.

![img_1.png](img_1.png)
![img_2.png](img_2.png)

---

## Spring Cloud Config Client 구축

### 클라이언트 서버 구축

Spring Cloud Config Client 실행을 위해서는 아래의 의존성이 필요하다.

```
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-actuator'
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.cloud:spring-cloud-starter-config'
}

dependencyManagement {
    imports {
        mavenBom "org.springframework.cloud:spring-cloud-dependencies:${springCloudVersion}"
    }
}
```

```
@Setter
@Getter
@ConfigurationProperties("com.sy")
@RefreshScope
@ToString
public class MyConfig {

    private String profile;
    private String region;
}
```

- ``@RefreshScope`` : 설정 정보가 바뀌면 다시 불러올 수 있도록 도와준다.\
Git 주소에 있는 설정 파일을 수정했을 경우 /actuator/refresh 엔드포인트를 호출하면 변경된 설정 값이 반영된다.

application.yml
```
spring:
  application:
    name: sy
  profiles:
    active: kr-dev
  config:
    import: optional:configserver:http://localhost:8888
```

config server 와 통신에 실패했을 때 에러를 던지고, 서버 실행을 멈추고 싶다면 ``optional:`` 부분을 제거한다.

```
@RestController
@RequiredArgsConstructor
public class ConfigController {

    private final MyConfig myConfig;

    @GetMapping("/config")
    public ResponseEntity<String> config() {
        System.out.println(myConfig);
        return ResponseEntity.ok(myConfig.toString());
    }
}
```

---

## Spring Cloud Config 설정 파일 내용 갱신하는 방법

스프링 클라우드를 통해 깃 주소에서 관리하는 설정 파일에 변경이 필요할 수 있다.\
그러나 별도의 설정 없이는 설정 파일이 변경되어도 변경 사항이 client에 반영되지 않는다.\
이는 Spring Cloud Config Server 부하를 줄이도록 애플리케이션을 실행 시점에 1번만 설정 정보를 읽고 로컬에 캐싱하기 때문이다.

때문에 설정이 자동 갱신되도록 하기 위해서는 아래 3가지 방법을 사용할 수 있다.
- spring cloud client 서버들에 actuator API 호출해주기
- spring cloud bus 를 사용해 이벤트 전파하기
- watcher 를 통해 spring cloud server 에 변경 여부를 확인하기

### Spring Cloud Client 서버들에 actuator API 호출해주기

가장 심플한 방법이다.\
클라이언트에 spring-boot-actuator 의존성을 추가하고 ``POST`` 로 ``/actuator/refresh`` 요청을 보낸다.\
그러면 actuator 가 설정 정보를 다시 읽고 값을 refresh 한다.

간단한 방법이지만 운영 중인 서버가 여러대라면 상당히 번거로워질 수 있다.\
특히나 Spring Cloud Eureka 와 같이 서비스 디스커버리 기술을 사용하고 있지 않다면 더더욱 적용이 어려워진다.

### Spring Cloud Bus 를 사용해 이벤트 전파하기

Spring Cloud bus 는 서비스 노드를 연결해 설정 정보 등의 변경을 전파하기 위한 경량화 메시지 브로커이다.\
기본적으로 AMQP 메시지 브로커로 사용한다.\

이는 효율적인 방법이지만 관리해야 하는 서버가 늘어난다는 부담이 존재한다.

### Watcher 를 통해 Spring Cloud Sever 에 변경 여부를 확인하기

Watcher 는 설정 서버에 변경 여부를 지속적으로 물어보고 확인하는 컴포넌트이다.\
일반적으로 설정 서버는 별다른 일을 하지 않도록 분리되어 있어 요청에 대한 부담이 적다.

하지만 Git으로 설정 파일을 관리하는 경우, Spring Cloud Config에서 제공하는 Watcher로는 변경 감지가 불가하다.\
설정 서버는 설정 클라이언트에게 state 와 version 을 내려주는데, Git은 version 이 git HEAD 의 checksum 에 해당한다.\
하지만 Spring Cloud Config 가 구현해둔 ConfigCloudWatcher 는 state 의 변경 여부만 검사하기 때문에, Git은 변경 여부 탐지가 불가하다.

이는 ConfigWatcher 를 참고해 version 의 수정 여부를 검사하는 구현체를 만들어 해결할 수 있다.

---

## Spring Cloud Config 사용 시 주의사항

![img_3.png](img_3.png)

스프링 클라우드 공식 문서에서는 이와 같은 내용을 Warning으로 다루고 있다.\
OS에 따라 주기적으로 임시 디렉토리가 삭제될 수 있으니 ``basedir``을 설정하라는 내용이다.

즉, 설정 파일을 클론 받을 위치를 직접 지정해서 임시 디렉토리에 받으면 안된다는 것이다.

---

application.yml
```
server:
  port: 8888
spring:
  application:
    name: config
  cloud:
    config:
      server:
        git:
          uri: https://github.com/yoonsla/spring-cloud
          search-paths: config/config-file/**
          default-label: main
          basedir: ./repo
```

위와 같이 Config Server의 설정 파일에서 basedir 설정을 해야한다.
