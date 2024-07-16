# OpenFeign 이란?

Open Feign 은 Netflix에 의해 처음 만들어진 선언적 HTTP Client 도구로, 외부 API 호출을 용이하게 해준다.
> 선언적인 이유 : 어노테이션을 사용하여 편리한 개발이 가능

```
@FeignClient(name = "OpenFeign", url = "${test.api.uri}")
public interface OpenFeign {
```

## 히스토리

Open Feign의 초기 모델인 Feign은 Netflix에 의해 만들고 공개되었다.\
그 후 Spring Cloud Netflix라는 프로젝트로 Netflix OSS를 Spring Cloud 생태계로 포함되었다.\
현재는 넷플릭스 내부적으로 feign의 개발은 중단되었으며 OpenFeign이라는 이름으로 오픈소스화되었다.\
이 과정에서 기존의 Feign 자체 어노테이션과 JAX-RS 어노테이션만 사용 가능했던 부분을 Spring MVC 어노테이션을 지원하도록 추가되었다.
> Netflix OSS → Spring Cloud Netflix → Open Feign → Spring Cloud Open Feign

## 장점

- 인터페이스와 어노테이션 기반으로, 작서할 코드가 줄어든다.
```
@FeignClient(name = "OpenFeign", url = "${test.api.uri}")
public interface OpenFeign {

    @GetMapping
    void call(
        @RequestHeader String apiKey,
        @RequestParam Currency source,
        @RequestParam Currency currencies
    );
}
```
- Spring MVC 어노테이션으로 개발 할 수 있어 진입장벽이 높지 않다.
- 다른 Spring Cloud 프로젝트와의 통합이 용이하다.

## 단점 + 한계

- 기본 HTTP Client가 HTTP2를 지원하지 않는다. (추가 설정 필요)
- 테스트 도구를 제공하지 않는다. (별도의 설정 파일을 작성하여 대응)

## 사용 방법

```
ext {
    set('springCloudVersion', "{{사용할 버전}})
}
dependencyManagement {
    imports {
        mavenBom "org.springframework.cloud:spring-cloud-dependencies:${springCloudVersion}"
    }
}

dependency {
    implementation 'org.springframework.cloud:spring-cloud-starter-openfeign'
```

OpenFeign을 활성화하기 위해서는 ``@EnableFeignClients`` 어노테이션을 붙이면 된다.
```
@EnableFeignClients
@SpringBootApplication
public class OpenFeignSampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(OpenFeignSampleApplication.class, args);
    }
}

------------------------- OR -------------------------
Main 클래스보다는 별도의 Config 파일을 만드는 것을 권장한다.

@Configuration
@EnableFeignClients("com.mangkyu.openfeign")
class OpenFeignConfig {

}
```

구현할 때는 인터페티스에 ``@FeignClient`` 어노테이션을 붙여주면 된다.\
이 때 value 와 url 설정이 필요한데 url 에는 호출할 주소를, value 에는 임의의 client 이름을 적는다. 

```
@FeignClient(name = "OpenFeign", url = "${test.api.uri}")
public interface OpenFeign {

    @GetMapping
    OpenFeignResponse call(
            @RequestHeader String apiKey,
            @RequestParam Currency source,
            @RequestParam Currency currencies);

}
```



