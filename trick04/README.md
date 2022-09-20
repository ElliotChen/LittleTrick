# Spring Boot Exception Handling

## @ControllerAdvice And @RestControllerAdvice

@RestControllerAdvice = @ControllerAdvice + Rest Response

### @ControllerAdvice
@ControllerAdvice 用以定義所有Controller共用的ExceptionHandler, InitBinder, ModelAttribute
1. @ExceptionHandler 如何處理Controller 抛出的特定Exception
2. @InitBinder 可用來定義model中資料格式轉換的方式
3. @ModelAttribute 則可在model裡加入或移除資料

### @RestControllerAdvice
@ResponseBodyAdvice 則會直接加上@ResponseBody，讓HttpMessageConverter進行轉換的工作

### HttpMessageConverter

HttpMessageConverter.canWrite 與HttpMessageConverter.canRead會依傳入MediaType決定是否可以轉換

可透過WebMvcConfigurer.configureMessageConverters加入指定的HttpMessageConverter
```java
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> messageConverters) {
		messageConverters.add(new MyHttpMessageConverter());
	}
}
```

再來就是在Http Request中要指定Header Accept的MediaType
```http
curl --header "Accept: application/xml" 
  http://localhost:8080/spring-boot-rest/foos/1
```

上列指出可收的為`application/xml`，Spring 就會輪替找出可支援轉換的HttpMessageConverter

## GsonHttpMessageConverter And MappingJackson2HttpMessageConverter

Spring預設是使用Jackson來做為JSON的處理，但如果想改用Gson來做也很快

### Gson

#### autoconfigure

SpringBoot 用了很多的autoconfigure，以class可否載入做為判斷，輔以特定型態的Bean是否存在
就可以自動產生基本的設定

要將Spring預設使用的Jackson改用Gson有幾個條件，
1. 載入Gson的Library
2. 移除Jackson的library，同時要求SpringBoot不要使用JacksonAutoConfiguration

##### library

```gradle
dependencies {
	//移除Spring使用的jackson library
	implementation("org.springframework.boot:spring-boot-starter-web") {
		exclude("org.springframework.boot","spring-boot-starter-json")
	}
	//加入Gson
	implementation("com.google.code.gson:gson")
	....
}
```

##### exclude autoconfigure

```kotlin
@SpringBootApplication(exclude = arrayOf(JacksonAutoConfiguration::class))
```

#### GsonAutoConfiguration

[GsonAutoConfiguration](https://github.com/spring-projects/spring-boot/blob/main/spring-boot-project/spring-boot-autoconfigure/src/main/java/org/springframework/boot/autoconfigure/gson/GsonAutoConfiguration.java)

稍唯看一下`GsonAutoConfiguration`的程式

```java
@AutoConfiguration
@ConditionalOnClass(Gson.class)
@EnableConfigurationProperties(GsonProperties.class)
public class GsonAutoConfiguration {
	....
}
```

`ConditionalOnClass(Gson.class)` 表明了要`Gson`有載入時才生效

```java
	@Bean
	@ConditionalOnMissingBean
	public GsonBuilder gsonBuilder(List<GsonBuilderCustomizer> customizers) {
		GsonBuilder builder = new GsonBuilder();
		customizers.forEach((c) -> c.customize(builder));
		return builder;
	}

	@Bean
	@ConditionalOnMissingBean
	public Gson gson(GsonBuilder gsonBuilder) {
		return gsonBuilder.create();
	}
```

`ConditionalOnMissingBean` 代表了只有當系統中沒有`Gson`實例時才會自動產生，也就是如果我們自己在config裡產生一個，那這就沒作用了

```java
	@Bean
	public StandardGsonBuilderCustomizer standardGsonBuilderCustomizer(GsonProperties gsonProperties) {
		return new StandardGsonBuilderCustomizer(gsonProperties);
	}

	static final class StandardGsonBuilderCustomizer implements GsonBuilderCustomizer, Ordered { 
		.....
	}
```

利用`StandardGsonBuilderCustomizer`來載入客製的調整，依據 [GsonProperties](https://github.com/spring-projects/spring-boot/blob/main/spring-boot-project/spring-boot-autoconfigure/src/main/java/org/springframework/boot/autoconfigure/gson/GsonProperties.java) 內容來改變初使化設定
所以如果SpringBoot己有可設定的方式，就大可不必再自行產生了。

如果有其他需要改變的，可以再自行產生一個`GsonBuilderCustomizer`即可，不一定要全部打掉。

## 輸入資料檢核與錯誤處理

### validation framework

除非知道在做什麼，否則儘量使用已存在的framework，例如`spring-boot-starter-validation`

錯誤的處理就依靠先前提到的`@RestControllerAdvice`

輸入資料檢核上根據使用的方式主要會有三類的Exception:

1. `BindException`: form mapping  成request object時
2. `MethodArgumentNotValidException`: json mapping成request object時
3. `ConstraintViolationException`: 直接使用@RequestParam時

### Exception Handler

搭配`RestControllerAdvice`與回應`ApplicationResult`可以簡寫成下列程式

```
	@ExceptionHandler(BindException.class)
	public ApplicationResult handleBindException(BindException e) {
		ApplicationResult result = ApplicationResult.create(ErrorEnum.PARAMETER);
		String message = e.getBindingResult()
				.getAllErrors()
				.stream()
				.map(error -> error.getDefaultMessage())
				.collect(Collectors.joining(";"));
		result.setMessage(message);
		return result;
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ApplicationResult handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
		ApplicationResult result = ApplicationResult.create(ErrorEnum.PARAMETER);
		String message = e.getAllErrors()
				.stream()
				.map(error -> error.getDefaultMessage())
				.collect(Collectors.joining(";"));
		result.setMessage(message);
		return result;
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ApplicationResult handleConstraintViolationException(ConstraintViolationException e) {
		ApplicationResult result = ApplicationResult.create(ErrorEnum.PARAMETER);
		String message = e.getConstraintViolations()
				.stream()
				.map(error -> error.getMessage())
				.collect(Collectors.joining(";"));
		result.setMessage(message);
		return result;
	}
```

回應的結果會整合多個錯誤訊息一次提醒，如下例：

```
{
  "code": 8001,
  "message": "user name cant be blank.; user id length must great than 5."
}
```

