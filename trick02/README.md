## Retry

實際開發工作上，要嘗試做 Retry的操作其實不少，無論是網路的因素或是取得特定鎖的問題，都能見到 retry機制的的使用。

當然，自行使用 try/catch + counter，也是片小蛋糕。

```java

boolean doRetry = true;
int counter = 1;
while (doRetry) {
    try {
        if (counter++ > 3 || doJob()) {
            doRetry = false;
        }
    } catch (Exception e) {
    }
    
}

```

但如果有更方便的寫法呢？

### Spring Retry

[Spring Retry](https://github.com/spring-projects/spring-retry) 使用AOP的方式來提供這樣的功能。

### 使用方式

#### Dependencies

maven要引入基本的dependencies

```
<dependency>
	<groupId>org.springframework</groupId>
	<artifactId>spring-aspects</artifactId>
	<version>1.3.0</version>
</dependency>
<dependency>
	<groupId>org.springframework</groupId>
	<artifactId>spring-aspects</artifactId>
</dependency>
```

#### 1. 基本Annotation

第一種方式，在Method上使用Annotation

1. @EnableRetry
2. @Retryable
3. @Recover

##### @EnableRetry

代表啟用Spring Retry機制

##### @Retryable

如果Retryable未註明哪一種Exception，代表遇到所有的Exception都會重試，

##### @Recover

當Retry的次數達到限制仍未能正常完成時，可以透過@Recover來做回復的處理。

![recover.png](https://blog.elliot.tw/wp-content/uploads/2020/09/SpringRetry01.png)

Recover的選擇與回傳、Exception與參數類型有關，當然也可以使用一個接收General Exception的Recover來處理。

#### 2. RetryTemplate

第二種使用RetryTemplate

如同Spring 其他常用的Template，像是JdbcTemplate，定義了Callback的Class。

1. RetryCallback 
2. RecoveryCallback

利用Builder建立RetryTemplate

```
@Bean
public RetryTemplate buildRetryTemplate() {
	RetryTemplate retryTemplate = RetryTemplate.builder()
			.maxAttempts(2)
			.build();

	return retryTemplate;
}
```

要執行的implement RetryCallback 跟 RecoveryCallback

```
class Service03Retry implements RetryCallback<String, RemoteAccessException>, RecoveryCallback<String> {

	private String task;

	@Override
	public String doWithRetry(RetryContext retryContext) throws RemoteAccessException {
		log.info("Execute doWithRetry");
		throw new RemoteAccessException("For Service03");
	}

	@Override
	public String recover(RetryContext retryContext) throws RemoteAccessException {
		log.info("Execute Recover for Exception [{}]", task);
		log.error("Recover for Exception", retryContext.getLastThrowable());
		return "Recovered";
	}
}
```

再來就是使用RetryTemplate來執行

```
@Autowired
private RetryTemplate retryTemplate;

public String retryMethod03(String job) throws Exception {
	Service03Retry task = new Service03Retry();
	task.setTask("task03");
	this.retryTemplate.execute(task, task);
	throw new RemoteAccessException("For RetryTemplate");
}
```
